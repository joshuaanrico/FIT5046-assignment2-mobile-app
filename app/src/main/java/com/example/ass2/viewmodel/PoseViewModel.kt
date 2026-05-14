package com.example.ass2.viewmodel

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import java.util.concurrent.Executors

class PoseViewModel(application: Application) : AndroidViewModel(application)
{
    // Public state observed by LiveSessionScreen

    private var sessionLocked = false
    private val _exerciseUpdate = MutableStateFlow(ExerciseUpdate())
    val exerciseUpdate: StateFlow<ExerciseUpdate> = _exerciseUpdate.asStateFlow()
    private val _currentExerciseName = MutableStateFlow(UpperBodyExercises.all.first().displayName)
    val currentExerciseName: StateFlow<String> = _currentExerciseName.asStateFlow()
    val availableExercises: List<UpperBodyExercise> = UpperBodyExercises.all

    // Internal - plain holders, not Compose state
    // The overlay view is set/cleared by LiveSessionScreen's AndroidView callbacks.
    // @Volatile ensures the MediaPipe callback thread always sees the latest value.
    @Volatile private var overlayView: PoseOverlayView? = null

    private val exerciseHolder = object { var ex: UpperBodyExercise = BicepCurlExercise() }
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
    // hr - observable variable.
    private val hr = MutableStateFlow(0)
    // readonly heartRate
    val heartRate: StateFlow<Int> = hr.asStateFlow()
    private val showAlert = MutableStateFlow(false)
    // readonly showHRAlert
    val showHRAlert: StateFlow<Boolean> = showAlert.asStateFlow()
    // To store the HR values during the session to calculate the avg at the end of session.
    private val hrSamples = mutableListOf<Int>()
    // Reference to the coroutine in which the simulation is being run.
    private var simulatorJob: Job? = null
    private var sessionSrtTime = 0L

    /**
     * Being called when the live session screen opens.
     * Reads the user profile, calculates the user HR threshold.
     * Then the HR-BPM stream is started / the simulation.
     *
     * @author Yapa Jayasinghe
     */
    fun sessionStart()
    {
        sessionSrtTime = System.currentTimeMillis()
        hrSamples.clear()
        showAlert.value = false

        // getting the user id - so that age can be calculated.
        val userId = FirebaseAuth.getInstance().currentUser?.uid?: return

        // starting a new coroutine - tied to the viewmodel lifecycle
        simulatorJob = viewModelScope.launch(Dispatchers.IO)
        {
            // accessing the user profile (Room).
            val userProfile = profileRepo.observe(userId).firstOrNull()
            // age calculation (default set to 40yrs)
            val currentUserAge = if (userProfile?.dob != null) ageCalc(userProfile.dob) else 40
            // getting the personalized threshold HR for current user
            val threshold = heartRateSim.getThresholdHRForAge(currentUserAge)
            // Log.d("HeartRate", "Age=$currentUserAge Threshold=$threshold uid=$userId profile=$userProfile")
            // starting BPM stream.
            heartRateSim.streamHR(currentUserAge).collect()
            {
                bpm ->
                // Log.d("HeartRate", "BPM=$bpm threshold=$threshold")
                hrSamples.add(bpm)
                hr.value = bpm
                // deciding whether to give warning or not
                if (bpm > threshold && !showAlert.value)
                {
                    showAlert.value = true
                }
            }
        }
    }

    /**
     * This method is run when the live session is ended / when navigated to a new screen.
     */
    fun stopSimulationOfHR()
    {
        simulatorJob?.cancel()
        simulatorJob = null
    }

    /**
     * Calculates and returns the avg HR observed during the session.
     */
    fun getAvgHR(): Int
    {
        return hrSamples.average().toInt()
    }

    /**
     * calculates the duration of the session.
     *
     * @return duration in milliseconds
     */
    fun getDurationOfSession(): Int
    {
        return ((System.currentTimeMillis() - sessionSrtTime)/1000).toInt()
    }

    /**
     * After the user is directed to the alert screen,
     * When the user returns to the session this method is executed to reset.
     */
    fun dismissAlert()
    {
        showAlert.value = false
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
        stopSimulationOfHR()
    }

    companion object {
        private const val TAG = "PoseViewModel"
        private const val MODEL = "pose_landmarker_lite.task"
    }
}
