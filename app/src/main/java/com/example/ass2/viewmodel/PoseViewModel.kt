package com.example.ass2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.ass2.data.repository.ProfileRepository
import com.example.ass2.pose.ExerciseUpdate
import com.example.ass2.pose.BicepCurlExercise
import com.example.ass2.pose.PoseOverlayView
import com.example.ass2.pose.UpperBodyExercise
import com.example.ass2.pose.UpperBodyExercises
import com.example.ass2.sensor.HeartRateSimulator
import com.google.firebase.auth.FirebaseAuth
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Calendar
import java.util.concurrent.Executors

class PoseViewModel(application: Application) : AndroidViewModel(application)
{
    // Public state observed by LiveSessionScreen
    val exerciseUpdate: StateFlow<ExerciseUpdate> = _exerciseUpdate.asStateFlow()
    private val _currentExerciseName = MutableStateFlow(UpperBodyExercises.all.first().displayName)
    val currentExerciseName: StateFlow<String> = _currentExerciseName.asStateFlow()
    val availableExercises: List<UpperBodyExercise> = UpperBodyExercises.all


    // Internal - plain holders, not Compose state
    // The overlay view is set/cleared by LiveSessionScreen's AndroidView callbacks.
    // @Volatile ensures the MediaPipe callback thread always sees the latest value.
    @SuppressLint("StaticFieldLeak")
    @Volatile private var overlayView: PoseOverlayView? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // MediaPipe — initialised once with application context
    private val poseLandmarker: PoseLandmarker? = try {
        val opts = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL).build())
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                // Exercise FSM (background thread → post state update to main)
                result.landmarks().firstOrNull()?.let { landmarks ->
                    // does not execute when the rep goal has been met.
                    if (!sessionLocked) {
                        val u = exerciseHolder.ex.process(landmarks)
                        viewModelScope.launch(Dispatchers.Main) { _exerciseUpdate.value = u }
                    }
                }
                viewModelScope.launch(Dispatchers.Main) {
                    val ov = overlayView ?: return@launch
                    if (result.landmarks().isNotEmpty()) ov.setResults(result) else ov.clear()
                }
            }
            .setErrorListener { e -> Log.e(TAG, "MediaPipe error: ${e.message}") }
            .build()
        PoseLandmarker.createFromOptions(application, opts)
    } catch (e: Exception) {
        Log.e(TAG, "Model load failed: ${e.message}")
        null
    }

    // ---------------------------------------------------------------------------------------------

    // heart rate simulation - related
    private val profileRepo = ProfileRepository(application)
    private val heartRateSim = HeartRateSimulator(application)
    // To store the HR values during the session to calculate the avg at the end of session.
    // readonly vals
    val heartRate: StateFlow<Int> = hr.asStateFlow()
    val hrAlertEvent = hrAlertChannel.receiveAsFlow()
    // when the user decides to continue with a reduced intensity - flag.
    val selectedLow: StateFlow<Boolean> = selectedLowerIntensity.asStateFlow()

    /**
     * Responsible for getting the simulated HR values,
     * stores the values in hrSamples val.
     */
    private fun startHrStream()
    {
        simulatorJob?.cancel()
        simulatorJob = sessionScope.launch{
            heartRateSim.streamHR(currentUserAge).collect { bpm ->
                hrSamples.add(bpm)
                hr.value = bpm
                if (bpm > currentThreshold)
                {
                    hrAlertChannel.trySend(Unit)
                }
            }
        }
    }

    /**
     * Being called when the live session screen opens.
     * Reads the user profile, calculates the user HR threshold.
     * Then the HR-BPM stream is started / the simulation.
     *
     * @author Yapa Jayasinghe
     */
    fun sessionStart()
    {
        if (sessionSrtTime > 0L) return  // making sure to only run if already not running.
        sessionSrtTime = System.currentTimeMillis()
        sessionPausedTime = 0L
        hrSamples.clear()
        // remove any leftover event from previous session.
        hrAlertChannel.tryReceive()
        // for a new session needs to reset.
        selectedLowerIntensity.value = false

        // getting the user id - so that age can be calculated.
        val userId = FirebaseAuth.getInstance().currentUser?.uid?: return

        // starting a new coroutine - tied to the viewmodel lifecycle
        sessionScope.launch{
            // accessing the user profile (Room).
            val userProfile = profileRepo.observe(userId).firstOrNull()
            // age calculation (default set to 40yrs)
            currentUserAge = if (userProfile?.dob != null) ageCalc(userProfile.dob) else 40
            // getting the personalized threshold HR for current user
            currentThreshold = heartRateSim.getThresholdHRForAge(currentUserAge)
            // Log.d("HeartRate", "Age=$currentUserAge Threshold=$threshold uid=$userId profile=$userProfile")
            startHrStream()
        }
    }

    /**
     * This method is run when the live session is ended / when navigated to a new screen.
     */
    private fun stopSimulationOfHR()
    {
        simulatorJob?.cancel()
        simulatorJob = null
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
        hrAlertChannel.tryReceive()
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
        // resetting rep count
        exerciseHolder.ex.reset()
        _exerciseUpdate.value = ExerciseUpdate() // UI change, rep - 0
        unlockExercise()
        selectedLowerIntensity.value = true
        hrAlertChannel.tryReceive()
        startHrStream()
    }

    /**
     * Calculates and returns the avg HR observed during the session.
     */
    fun getAvgHR(): Int
    {
        return hrSamples.average().toInt()
    }

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

    // ---------------------------------------------------------------------------------------------


    // Called by LiveSessionScreen's AndroidView factory + update lambda
    fun setOverlayView(view: PoseOverlayView?) {
        overlayView = view
    }

    // Called by LiveSessionScreen once the PreviewView is ready
    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(getApplication())
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build().apply {
                    setAnalyzer(cameraExecutor) { proxy ->
                        val lm = poseLandmarker ?: run { proxy.close(); return@setAnalyzer }
                        val bmp = proxy.toBitmap()
                        val mat = Matrix().apply {
                            postRotate(proxy.imageInfo.rotationDegrees.toFloat())
                        }
                        val rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
                        lm.detectAsync(BitmapImageBuilder(rotated).build(), proxy.imageInfo.timestamp)
                        proxy.close()
                    }
                }
            val selector = if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
                CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(getApplication()))
    }

    fun selectExercise(exercise: UpperBodyExercise) {
        exercise.reset()
        exerciseHolder.ex = exercise
        _currentExerciseName.value = exercise.displayName
        _exerciseUpdate.value = ExerciseUpdate()
        unlockExercise()
    }

    fun lockExercise(){
        sessionLocked = true
    }

    fun unlockExercise()
    {
        sessionLocked = false
    }

    override fun onCleared() {
        super.onCleared()
        overlayView = null
        cameraExecutor.shutdown()
        poseLandmarker?.close()
    }

    // a companion object is used to share the state states across when changing screens.
    // cuz in the navigations new instances of PoseViewModel are being called on.
    companion object
    {
        private const val TAG = "PoseViewModel"
        private const val MODEL = "pose_landmarker_lite.task"

        // the shared session states across all the PoseViewModel instances
        private val hrSamples = mutableListOf<Int>()

        // following fields with @Volatile - read and written across threads,
        // @Volatile guarantees - each thread referencing the latest value.
        // Reference to the coroutine in which the simulation is being run.
        @Volatile private var simulatorJob: Job? = null
        @Volatile private var sessionSrtTime = 0L
        // for when the session is paused / when alert shown.
        @Volatile private var sessionPausedTime = 0L
        @Volatile private var currentUserAge = 0
        @Volatile private var currentThreshold = 0
        // hr - observable variable.
        private val hr = MutableStateFlow(0)
        private val hrAlertChannel = Channel<Unit>(Channel.CONFLATED)
        private val _exerciseUpdate = MutableStateFlow(ExerciseUpdate())
        private val exerciseHolder = object { var ex: UpperBodyExercise = BicepCurlExercise() }
        private val selectedLowerIntensity = MutableStateFlow(false)
        @Volatile private var sessionLocked = false

        // a shared scope os that the Hr job survives across nav entry scoped viewmodel instances
        private val sessionScope = kotlinx.coroutines.CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    }
}
