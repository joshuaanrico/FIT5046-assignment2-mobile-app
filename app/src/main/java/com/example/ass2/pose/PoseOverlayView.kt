package com.example.ass2.pose

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

/**
 * Transparent View drawn over the CameraX PreviewView.
 * Renders upper-body skeleton connections and joint dots from MediaPipe results.
 */
class PoseOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var result: PoseLandmarkerResult? = null

    private val bonePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val jointPaint = Paint().apply {
        color = Color.parseColor("#26C6C6") // teal dots
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun setResults(r: PoseLandmarkerResult) {
        result = r
        invalidate()
    }

    fun clear() {
        result = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = result ?: return
        if (r.landmarks().isEmpty()) return
        val landmarks = r.landmarks()[0]

        for (connection in PoseLandmarker.POSE_LANDMARKS) {
            val s = connection.start()
            val e = connection.end()
            if (s >= landmarks.size || e >= landmarks.size) continue
            val sLm = landmarks[s]
            val eLm = landmarks[e]
            if ((sLm.visibility().orElse(0f) ?: 0f) < 0.5f) continue
            if ((eLm.visibility().orElse(0f) ?: 0f) < 0.5f) continue
            canvas.drawLine(
                sLm.x() * width, sLm.y() * height,
                eLm.x() * width, eLm.y() * height,
                bonePaint,
            )
        }

        for (lm in landmarks) {
            if ((lm.visibility().orElse(0f) ?: 0f) < 0.5f) continue
            canvas.drawCircle(lm.x() * width, lm.y() * height, 10f, jointPaint)
        }
    }
}
