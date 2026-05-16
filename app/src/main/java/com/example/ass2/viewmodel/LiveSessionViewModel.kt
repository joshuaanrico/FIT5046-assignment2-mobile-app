package com.example.ass2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ass2.data.repository.ProfileRepository
import com.example.ass2.sensor.HeartRateSimulator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * For the runtime state of the live session.
 * Scoped to the session flow to ensure that a single instance is being share,
 * between live session screen and alert screen. - preventing the use of having ot use companion obj.
 *
 * @author Yapa Jayasinghe
 */
class LiveSessionViewModel(application: Application) : AndroidViewModel(application)
{
    //----------------------------------------------------------------------------------------------
    // to access the Profile and HR simulated data
    private val profileRep = ProfileRepository(application)
    private val hRSim = HeartRateSimulator(application)
    //----------------------------------------------------------------------------------------------
    // To store the HR values during the session to calculate the avg at the end of session.
    // readonly vals
    private val hr = MutableStateFlow(0)
    val heartRate: StateFlow<Int> = hr.asStateFlow()
    // for when HR exceeds the threshold - oneshot event
    private val hRAlertChannel = Channel<Unit>(Channel.CONFLATED)
    val hRAlertEvent = hRAlertChannel.receiveAsFlow()
    //----------------------------------------------------------------------------------------------
    private val hrSamples = mutableListOf<Int>()
    // The coroutine for the HR simulation
    private var simulatorJob: Job? = null
    // to hold the current user age and HR threshold (decided from age)
    private var currentUserAge = 0
    private var currentHrThreshold = 0

    //----------------------------------------------------------------------------------------------
    private var sessionSrtTime = 0L
    // for when the session is paused / when alert shown.
    private var sessionPausedTime = 0L

    //----------------------------------------------------------------------------------------------
    // when the user decides to continue with a reduced intensity - flag.
    // live session observes this val
    private val selectedLowerIntensity = MutableStateFlow(false)
    val selectedLow: StateFlow<Boolean> = selectedLowerIntensity.asStateFlow()

    //----------------------------------------------------------------------------------------------
    /**
     * Calculating the current user age, then the HR threshold,
     * starts the stream of HR values.
     */
    fun beginHr()
    {
        // getting the user id - so that age can be calculated.
        val userId = FirebaseAuth.getInstance().currentUser?.uid?: return

        // starting a new coroutine - tied to the viewmodel lifecycle
        viewModelScope.launch(Dispatchers.IO){
            // accessing the user profile (from the Room).
            val userProfile = profileRep.observe(userId).firstOrNull()
            // age calculation (default set to 40yrs)
            currentUserAge = if (userProfile?.dob != null) ageCalc(userProfile.dob) else 40
            // getting the personalized threshold HR for current user
            currentHrThreshold = hRSim.getThresholdHRForAge(currentUserAge)
            startHrStream()
        }
    }

    /**
     * Starts the stream of HR values and stores them.
     */
    private fun startHrStream()
    {
        simulatorJob?.cancel()
        simulatorJob = viewModelScope.launch(Dispatchers.IO){
            hRSim.streamHR(currentUserAge).collect { bpm ->
                hrSamples.add(bpm)
                hr.value = bpm
                if (bpm > currentHrThreshold)
                {
                    hRAlertChannel.trySend(Unit)
                }
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    /**
     * Being called when the live session screen is opened for the firs time.
     */
    fun sessionStart() {
        if (sessionSrtTime > 0L) return  // making sure to only run if already not running.
        sessionSrtTime = System.currentTimeMillis()
        sessionPausedTime = 0L
        hrSamples.clear()
        // remove any leftover event from previous session.
        consumeHrAlert()
        selectedLowerIntensity.value = false
        beginHr()
    }

    /**
     * Called when the user presses the pause btn,
     * records when the pause is being started.
     * cancels the HR job / simulation.
     */
    fun pauseSession()
    {
        if(sessionPausedTime > 0L) return
        sessionPausedTime = System.currentTimeMillis()
        stopSimulationOfHR()
    }

    /**
     * Called on when the user presses the resume btn on the alert screen.
     */
    fun resumeSession()
    {
        if (sessionPausedTime > 0L)
        {
            val durationPaused = System.currentTimeMillis() - sessionPausedTime
            sessionSrtTime += durationPaused
            sessionPausedTime = 0L
        }
        consumeHrAlert()
        startHrStream()
    }

    /**
     * Called on when the user clicks on the continue with low intensity btn,
     * Timer and reps reset to O, target rep count is reduced.
     */
    fun sameExerciseWithLowIntensity()
    {
        // resetting timer
        sessionSrtTime = System.currentTimeMillis()
        sessionPausedTime = 0L
        // making sure the old simulator job is removed / done
        stopSimulationOfHR()
        resetHrSample()
        selectedLowerIntensity.value = true
        consumeHrAlert()
        startHrStream()
    }

    /**
     * Being called by the live session screen after it had applied the lower intensity flag,
     * to prevent it a reduction for the next time.
     */
    fun acknowledgeTheLowerIntensity()
    {
        selectedLowerIntensity.value = false
    }

    /**
     * calculates the time passed by.
     *
     * @return duration in milliseconds
     */
    fun getDurationOfSession(): Int
    {
        val base = if (sessionPausedTime > 0L) sessionPausedTime else System.currentTimeMillis()
        return ((base - sessionSrtTime)/1000).toInt()
    }

    //----------------------------------------------------------------------------------------------
    /**
     * Calculates the age of the user.
     *
     * @param dob the birthdate as Long
     * @return age as Int
     */
    private fun ageCalc(dob: Long): Int
    {
        val birth = Calendar.getInstance().apply { timeInMillis = dob }
        val today = Calendar.getInstance()
        val userAge = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)

        return  userAge.coerceIn(18,100)
    }

    /**
     * This method is run when the live session is ended / when navigated to a new screen.
     */
    private fun stopSimulationOfHR()
    {
        simulatorJob?.cancel()
        simulatorJob = null
    }

    fun resetHrSample()
    {
        hrSamples.clear()
        hr.value = 0
    }

    fun consumeHrAlert()
    {
        hRAlertChannel.tryReceive()
    }

    /**
     * Calculates and returns the avg HR observed during the session.
     */
    fun getAvgHR(): Int
    {
        return hrSamples.average().toInt()
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulationOfHR()
    }

}