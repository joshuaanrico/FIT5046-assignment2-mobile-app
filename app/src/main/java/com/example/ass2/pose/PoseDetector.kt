package com.example.ass2.pose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PoseDetector(private val context: Context) : Closeable {

    // ── Exercise FSM feed (ViewModel collects this) ───────────────────────────
    private val _result = MutableStateFlow<PoseLandmarkerResult?>(null)
    val result: StateFlow<PoseLandmarkerResult?> = _result.asStateFlow()

    // ── Overlay feed — plain raw ref, same pattern as AIpose ─────────────────
    // Set by LiveSessionScreen AndroidView factory + update lambda.
    @Volatile var overlayView: PoseOverlayView? = null

    private var poseLandmarker: PoseLandmarker? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        initLandmarker()
    }

    private fun initLandmarker() {
        try {
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(
                    BaseOptions.builder().setModelAssetPath(MODEL_PATH).build()
                )
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ ->
                    // Feed the ViewModel (background thread is fine for StateFlow)
                    _result.value = result
                    // Feed the overlay on the main thread — same as AIpose's runOnUiThread
                    mainHandler.post {
                        val ov = overlayView ?: return@post
                        if (result.landmarks().isNotEmpty()) ov.setResults(result) else ov.clear()
                    }
                }
                .setErrorListener { e -> Log.e(TAG, "MediaPipe error: ${e.message}") }
                .build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init PoseLandmarker: ${e.message}")
        }
    }

    fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .apply { setAnalyzer(cameraExecutor, ::analyzeFrame) }

            val cameraSelector = if (provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
                CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
            } catch (e: Exception) {
                Log.e(TAG, "Camera bind failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun analyzeFrame(proxy: ImageProxy) {
        val lm = poseLandmarker ?: run { proxy.close(); return }
        val bitmap = proxy.toBitmap()
        val matrix = Matrix().apply { postRotate(proxy.imageInfo.rotationDegrees.toFloat()) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        lm.detectAsync(BitmapImageBuilder(rotated).build(), proxy.imageInfo.timestamp)
        proxy.close()
    }

    override fun close() {
        overlayView = null
        cameraExecutor.shutdown()
        poseLandmarker?.close()
    }

    companion object {
        private const val TAG = "PoseDetector"
        const val MODEL_PATH = "pose_landmarker_lite.task"
    }
}
