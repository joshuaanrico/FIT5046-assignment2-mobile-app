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
import com.example.ass2.pose.ExerciseUpdate
import com.example.ass2.pose.BicepCurlExercise
import com.example.ass2.pose.PoseOverlayView
import com.example.ass2.pose.UpperBodyExercise
import com.example.ass2.pose.UpperBodyExercises
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.Executors

class PoseViewModel(application: Application) : AndroidViewModel(application)
{
    // Public state observed by LiveSessionScreen
    private val _exerciseUpdate = MutableStateFlow(ExerciseUpdate())
    val exerciseUpdate: StateFlow<ExerciseUpdate> = _exerciseUpdate.asStateFlow()
    private val _currentExerciseName = MutableStateFlow(UpperBodyExercises.all.first().displayName)
    val currentExerciseName: StateFlow<String> = _currentExerciseName.asStateFlow()
    val availableExercises: List<UpperBodyExercise> = UpperBodyExercises.all
    private val exerciseHolder = object { var ex: UpperBodyExercise = BicepCurlExercise() }
    private val selectedLowerIntensity = MutableStateFlow(false)
    @Volatile private var sessionLocked = false

    // Internal - plain holders, not Compose state
    // The overlay view is set/cleared by LiveSessionScreen's AndroidView callbacks.
    // @Volatile ensures the MediaPipe callback thread always sees the latest value.
    @SuppressLint("StaticFieldLeak")
    @Volatile private var overlayView: PoseOverlayView? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // MediaPipe PoseLandmarker initialised once with the lite model for real-time performance.
    // LIVE_STREAM mode delivers results asynchronously via the result listener callback.
    private val poseLandmarker: PoseLandmarker? = try {
        val opts = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL).build())
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                // Run on the camera background thread — post state updates back to main thread
                result.landmarks().firstOrNull()?.let { landmarks ->
                    // does not execute when the rep goal has been met.
                    if (!sessionLocked) {
                        val u = exerciseHolder.ex.process(landmarks)
                        viewModelScope.launch(Dispatchers.Main) { _exerciseUpdate.value = u }
                    }
                }
                // Update the skeleton overlay — clear it if no person is detected
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

    // Called by LiveSessionScreen's AndroidView factory and update lambda
    // to keep the overlay reference in sync with the composable lifecycle
    fun setOverlayView(view: PoseOverlayView?) {
        overlayView = view
    }

    // Binds CameraX to the lifecycle and starts streaming frames to MediaPipe.
    // Prefers the front camera for self-facing exercise sessions.
    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(getApplication())
        future.addListener({
            val provider = future.get()

            // Connect the camera preview stream to the PreviewView surface
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            // Analyse each frame: rotate to portrait, convert to Bitmap, send to MediaPipe
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build().apply {
                    setAnalyzer(cameraExecutor) { proxy ->
                        val lm = poseLandmarker ?: run { proxy.close(); return@setAnalyzer }
                        val bmp = proxy.toBitmap()
                        // Rotate the bitmap to match the device's display orientation
                        val mat = Matrix().apply {
                            postRotate(proxy.imageInfo.rotationDegrees.toFloat())
                        }
                        val rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, mat, true)
                        // Send the frame to MediaPipe asynchronously using the frame timestamp
                        lm.detectAsync(BitmapImageBuilder(rotated).build(), proxy.imageInfo.timestamp)
                        proxy.close()
                    }
                }

            // Use front camera if available, otherwise fall back to rear camera
            val selector = if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
                CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            // Mirror the skeleton overlay to match CameraX's horizontally-flipped front preview
            overlayView?.isMirrored = (selector == CameraSelector.DEFAULT_FRONT_CAMERA)
            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(getApplication()))
    }

    // Switches to a different exercise, resetting its rep count and FSM state
    fun selectExercise(exercise: UpperBodyExercise) {
        exercise.reset()
        exerciseHolder.ex = exercise
        _currentExerciseName.value = exercise.displayName
        _exerciseUpdate.value = ExerciseUpdate()
        unlockExercise()
    }

    fun resetExercise()
    {
        exerciseHolder.ex.reset()
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

    // Releases camera and MediaPipe resources when the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        overlayView = null
        cameraExecutor.shutdown()
        poseLandmarker?.close()
    }

    companion object
    {
        private const val TAG = "PoseViewModel"
        // Lite model used for real-time inference — balanced accuracy vs. performance
        private const val MODEL = "pose_landmarker_lite.task"

    }
}
