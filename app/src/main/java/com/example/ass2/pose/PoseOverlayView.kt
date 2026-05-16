package com.example.ass2.pose

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

// Transparent View drawn over the CameraX PreviewView.
// Renders the full-body skeleton: bone connections as white lines and joints as teal dots.
// Set isMirrored = true when using the front camera so the skeleton aligns with the
// horizontally-flipped preview that CameraX shows by default.
class PoseOverlayView(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyleAttr: Int = 0,
) : View(context, attrs, defaultStyleAttr) {

    private var result: PoseLandmarkerResult? = null

    // Flip landmark x-coordinates horizontally when using the front-facing camera
    var isMirrored: Boolean = false

    // Paints are declared as class fields so they are created once when the View is constructed
    // and reused every frame. If they were declared inside onDraw (called 60+ times/sec),
    // a new object would be created and discarded on every frame, flooding the garbage
    // collector and causing it to periodically pause the app — resulting in visible stuttering.
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

    // Updates the landmark result and triggers a redraw
    fun setResults(r: PoseLandmarkerResult) {
        result = r
        invalidate()
    }

    // Clears the overlay when no person is detected or the camera is stopped
    fun clear() {
        result = null
        invalidate()
    }

    // Converts a normalised MediaPipe x-coordinate [0, 1] to a canvas pixel x.
    // Mirrors the value for front-camera sessions so the skeleton matches the preview.
    private fun toCanvasX(normalizedX: Float): Float =
        if (isMirrored) (1f - normalizedX) * width else normalizedX * width

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = result ?: return
        if (r.landmarks().isEmpty()) return

        // Index 0 is the first (and typically only) detected person
        val landmarks = r.landmarks()[0]

        // Draw bone connections first so joint dots render on top
        for (connection in PoseLandmarker.POSE_LANDMARKS) {
            val s = connection.start()
            val e = connection.end()
            // if out of bounds skip the connection
            if (s >= landmarks.size || e >= landmarks.size) continue
            val sLm = landmarks[s]
            val eLm = landmarks[e]
            // Skip connections where either endpoint is not reliably visible
            if (sLm.visibility().orElse(0f) < VISIBILITY_THRESHOLD) continue
            if (eLm.visibility().orElse(0f) < VISIBILITY_THRESHOLD) continue
            canvas.drawLine(
                toCanvasX(sLm.x()), sLm.y() * height,
                toCanvasX(eLm.x()), eLm.y() * height,
                bonePaint,
            )
        }

        // Draw a dot at each sufficiently visible landmark
        for (lm in landmarks) {
            if (lm.visibility().orElse(0f) < VISIBILITY_THRESHOLD) continue
            canvas.drawCircle(toCanvasX(lm.x()), lm.y() * height, JOINT_RADIUS, jointPaint)
        }
    }

    companion object {
        private const val VISIBILITY_THRESHOLD = 0.5f
        private const val JOINT_RADIUS = 10f
    }
}
