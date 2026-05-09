package com.example.ass2.pose

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Base class for upper-body pose exercises for disabled users.
 * Each subclass implements a 3-state FSM (start → mid → peak) and
 * counts a rep when a specific state transition occurs.
 *
 * Landmark indices follow MediaPipe Pose (33 landmarks):
 *  11 = LEFT_SHOULDER   12 = RIGHT_SHOULDER
 *  13 = LEFT_ELBOW      14 = RIGHT_ELBOW
 *  15 = LEFT_WRIST      16 = RIGHT_WRIST
 *  23 = LEFT_HIP        24 = RIGHT_HIP
 */
abstract class UpperBodyExercise(
    val displayName: String,
    val isBilateral: Boolean = false,
) {
    protected var state: String = initialState()
    var repCount: Int = 0
    private var lastRepMs: Long = 0L
    private val minRepIntervalMs = 800L

    abstract fun initialState(): String

    /** Returns (leftAngle, rightAngle) in degrees from normalized landmark list. */
    abstract fun computeAngles(landmarks: List<NormalizedLandmark>): Pair<Float, Float>

    /**
     * Given the current state and angles, returns (nextState, didCount, feedback).
     * didCount should be true only on the specific transition that signals a completed rep.
     */
    abstract fun step(
        currentState: String,
        leftAngle: Float,
        rightAngle: Float,
    ): Triple<String, Boolean, String>

    /** Override to provide exercise-specific quality scoring (0–100). */
    protected open fun estimateQuality(leftAngle: Float, rightAngle: Float): Float = 75f

    fun process(landmarks: List<NormalizedLandmark>): ExerciseUpdate {
        if (landmarks.size < 27) {
            return ExerciseUpdate(repCount, state, "Step back so your upper body is visible", 0f, 0f, 0f)
        }
        val (left, right) = computeAngles(landmarks)
        val (nextState, didCount, feedback) = step(state, left, right)
        val now = System.currentTimeMillis()
        if (didCount && now - lastRepMs > minRepIntervalMs) {
            repCount++
            lastRepMs = now
        }
        state = nextState
        return ExerciseUpdate(repCount, state, feedback, estimateQuality(left, right), left, right)
    }

    fun reset() {
        state = initialState()
        repCount = 0
        lastRepMs = 0L
    }

    // ── Shared angle helper ───────────────────────────────────────────────────

    /** Angle (degrees) at landmark [b] formed by vectors b→a and b→c. */
    protected fun angleBetween(
        landmarks: List<NormalizedLandmark>,
        a: Int, b: Int, c: Int,
    ): Float {
        val ax = landmarks[a].x() - landmarks[b].x()
        val ay = landmarks[a].y() - landmarks[b].y()
        val cx = landmarks[c].x() - landmarks[b].x()
        val cy = landmarks[c].y() - landmarks[b].y()
        val dot = (ax * cx + ay * cy).toDouble()
        val magA = sqrt((ax * ax + ay * ay).toDouble())
        val magC = sqrt((cx * cx + cy * cy).toDouble())
        if (magA == 0.0 || magC == 0.0) return 0f
        return Math.toDegrees(acos((dot / (magA * magC)).coerceIn(-1.0, 1.0))).toFloat()
    }

    protected fun symmetryQuality(leftAngle: Float, rightAngle: Float): Float =
        (1f - abs(leftAngle - rightAngle) / 180f).coerceIn(0f, 1f) * 100f
}
