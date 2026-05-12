package com.example.ass2.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.ass2.pose.ExerciseUpdate
import com.example.ass2.pose.HammerCurlExercise
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

class PoseViewModel(application: Application) : AndroidViewModel(application) {

    // Public state observed by LiveSessionScreen
    private val _exerciseUpdate = MutableStateFlow(ExerciseUpdate())
    val exerciseUpdate: StateFlow<ExerciseUpdate> = _exerciseUpdate.asStateFlow()

    private val _currentExerciseName = MutableStateFlow(UpperBodyExercises.all.first().displayName)
    val currentExerciseName: StateFlow<String> = _currentExerciseName.asStateFlow()

    val availableExercises: List<UpperBodyExercise> = UpperBodyExercises.all

    // Internal - plain holders, not Compose state
    // The overlay view is set/cleared by LiveSessionScreen's AndroidView callbacks.
    // @Volatile ensures the MediaPipe callback thread always sees the latest value.
    @Volatile private var overlayView: PoseOverlayView? = null

    private val exerciseHolder = object { var ex: UpperBodyExercise = HammerCurlExercise() }
    private val mainHandler = Handler(Looper.getMainLooper())
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // MediaPipe — initialised once with application context
    private val poseLandmarker: PoseLandmarker? = try {
        val opts = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath(MODEL).build())
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                // Exercise FSM (background thread → post state update to main)
                result.landmarks().firstOrNull()?.let { landmarks ->
                    val u = exerciseHolder.ex.process(landmarks)
                    mainHandler.post { _exerciseUpdate.value = u }
                }
                // Overlay (post to main — same as AIpose's runOnUiThread)
                mainHandler.post {
                    val ov = overlayView ?: return@post
                    if (result.landmarks().isNotEmpty()) ov.setResults(result) else ov.clear()
                }
            }
            .setErrorListener { e -> Log.e(TAG, "MediaPipe error: ${e.message}") }
            .build()
        PoseLandmarker.createFromOptions(application, opts)
    } catch (e: Throwable) {
        // We catch Throwable (not just Exception) because UnsatisfiedLinkError (JNI failure)
        // is an Error. This prevents the app from crashing if the native library fails to load.
        Log.e(TAG, "Model load failed (likely JNI error): ${e.message}")
        null
    }

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
    }

    override fun onCleared() {
        super.onCleared()
        overlayView = null
        cameraExecutor.shutdown()
        poseLandmarker?.close()
    }

    companion object {
        private const val TAG = "PoseViewModel"
        private const val MODEL = "pose_landmarker_lite.task"
    }
}
