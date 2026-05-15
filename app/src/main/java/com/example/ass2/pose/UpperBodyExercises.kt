package com.example.ass2.pose

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

// 1. Bicep Curl — tracks the shoulder-elbow-wrist angle bilaterally.
//    Averages both arms so the user must curl both sides together.
//    States: EXTENDED (>150°) → CURLING (50–150°) → CONTRACTED (≤50°)
//    Rep counted on CURLING → CONTRACTED (peak contraction reached)
class BicepCurlExercise : UpperBodyExercise("Bicep Curl", isBilateral = true) {
    override fun initialState() = "EXTENDED"

    // Measures the elbow angle on both sides (shoulder → elbow → wrist)
    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 13, 15), // left shoulder-elbow-wrist
        angleBetween(landmarks, 12, 14, 16), // right shoulder-elbow-wrist
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "EXTENDED" -> when {
                avg <= 50f  -> "CONTRACTED" to "Full curl! Squeeze at the top"
                avg <= 150f -> "CURLING"    to "Curl upward, keep shoulders still"
                else        -> "EXTENDED"   to "Start: raise both arms"
            }
            "CURLING" -> when {
                avg <= 50f  -> "CONTRACTED" to "Fully contracted — great form!"
                avg > 150f  -> "EXTENDED"   to "Extend fully between reps"
                else        -> "CURLING"    to "Keep raising — almost there"
            }
            "CONTRACTED" -> when {
                avg > 50f   -> "CURLING"    to "Lower with control"
                else        -> "CONTRACTED" to "Hold, then lower slowly"
            }
            else -> "EXTENDED" to ""
        }
        val counted = currentState == "CURLING" && next == "CONTRACTED"
        return Triple(next, counted, feedback)
    }

    // Quality is based on how symmetrically both arms move together
    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// 2. Side Stretch — tracks the elbow-shoulder-hip angle to detect lateral arm raises.
//    Uses the peak (max) of both sides so either arm raising triggers the state change,
//    allowing the user to stretch one side at a time.
//    States: LOWERED (<30°) → RAISING (30–130°) → RAISED (≥130°)
//    Rep counted on RAISING → RAISED (full overhead stretch reached)
class SideStretchExercise : UpperBodyExercise("Side Stretch", isBilateral = true) {
    override fun initialState() = "LOWERED"

    // Measures how far each arm is raised relative to the torso
    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 13, 11, 23), // left elbow-shoulder-hip
        angleBetween(landmarks, 14, 12, 24), // right elbow-shoulder-hip
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        // Use the higher of the two angles so one arm raising is enough
        val peak = maxOf(leftAngle, rightAngle)
        val (next, feedback) = when (currentState) {
            "LOWERED" -> when {
                peak >= 130f -> "RAISED"  to "Great stretch! Hold for 5 seconds"
                peak >= 30f  -> "RAISING" to "Raise your arm higher for a deeper stretch"
                else         -> "LOWERED" to "Start: raise one arm up and over"
            }
            "RAISING" -> when {
                peak >= 130f -> "RAISED"  to "Full stretch — feel the side body lengthen"
                peak < 30f   -> "LOWERED" to "Lower fully, then raise the other side"
                else         -> "RAISING" to "Keep reaching — almost there"
            }
            "RAISED" -> when {
                peak < 30f   -> "LOWERED" to "Rep complete! Switch sides"
                peak < 130f  -> "RAISING" to "Lower slowly with control"
                else         -> "RAISED"  to "Hold the stretch, breathe deeply"
            }
            else -> "LOWERED" to ""
        }
        val counted = currentState == "RAISING" && next == "RAISED"
        return Triple(next, counted, feedback)
    }

    // Quality increases as the arm reaches further overhead (30° = 0%, 155° = 100%)
    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val peak = maxOf(leftAngle, rightAngle)
        return ((peak - 30f) / (155f - 30f)).coerceIn(0f, 1f) * 100f
    }
}

// 3. Forward Fold — tracks the shoulder-hip-knee angle to detect forward bending.
//    Averages both sides for a stable spine angle reading.
//    States: UPRIGHT (>100°) → FOLDING (70–100°) → FOLDED (≤70°)
//    Rep counted on FOLDING → FOLDED (deepest point of the fold reached)
class ForwardFoldExercise : UpperBodyExercise("Forward Fold", isBilateral = true) {
    override fun initialState() = "UPRIGHT"

    // Measures the hip hinge angle (how far the torso tilts forward)
    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 23, 25), // left shoulder-hip-knee
        angleBetween(landmarks, 12, 24, 26), // right shoulder-hip-knee
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "UPRIGHT" -> when {
                avg <= 70f  -> "FOLDED"  to "Deep fold! Keep breathing"
                avg <= 100f -> "FOLDING" to "Fold deeper — relax your back"
                else        -> "UPRIGHT" to "Start: hinge forward at the hips"
            }
            "FOLDING" -> when {
                avg <= 70f  -> "FOLDED"  to "Perfect fold! Hold and breathe"
                avg > 100f  -> "UPRIGHT" to "Rise back to upright"
                else        -> "FOLDING" to "A little deeper — let gravity help"
            }
            "FOLDED" -> when {
                avg > 100f  -> "UPRIGHT" to "Rep complete! Rise with a flat back"
                avg > 70f   -> "FOLDING" to "Rise slowly, keeping your back long"
                else        -> "FOLDED"  to "Hold the fold, soften your knees"
            }
            else -> "UPRIGHT" to ""
        }
        val counted = currentState == "FOLDING" && next == "FOLDED"
        return Triple(next, counted, feedback)
    }

    // Quality increases as the fold gets deeper (100° = 0%, 45° = 100%)
    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val avg = (leftAngle + rightAngle) / 2f
        return ((100f - avg) / (100f - 45f)).coerceIn(0f, 1f) * 100f
    }
}

// 4. Seated Twist — detects torso rotation by comparing the wrist-elbow-shoulder
//    angle between the left and right sides. A large difference indicates a twist.
//    States: NEUTRAL (diff <15°) → TWISTING (15–30°) → TWISTED (>30°)
//    Rep counted on TWISTING → TWISTED (sufficient rotation achieved)
class SeatedTwistExercise : UpperBodyExercise("Seated Twist", isBilateral = true) {
    override fun initialState() = "NEUTRAL"

    // Measures arm angle relative to the shoulder on each side
    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 15, 13, 11), // left wrist-elbow-shoulder
        angleBetween(landmarks, 16, 14, 12), // right wrist-elbow-shoulder
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        // The angle difference between sides indicates the degree of rotation
        val diff = kotlin.math.abs(leftAngle - rightAngle)
        val (next, feedback) = when (currentState) {
            "NEUTRAL" -> when {
                diff > 30f  -> "TWISTED"  to "Good twist! Keep shoulders level"
                diff >= 15f -> "TWISTING" to "Rotate further — lead with your chest"
                else        -> "NEUTRAL"  to "Start: rotate your torso to one side"
            }
            "TWISTING" -> when {
                diff > 30f  -> "TWISTED"  to "Full twist — hold briefly"
                diff < 15f  -> "NEUTRAL"  to "Return to centre, then twist the other way"
                else        -> "TWISTING" to "A little more rotation — breathe out"
            }
            "TWISTED" -> when {
                diff < 15f  -> "NEUTRAL"  to "Rep complete! Twist to the other side"
                diff < 30f  -> "TWISTING" to "Unwind slowly with control"
                else        -> "TWISTED"  to "Hold the twist, keep your spine tall"
            }
            else -> "NEUTRAL" to ""
        }
        val counted = currentState == "TWISTING" && next == "TWISTED"
        return Triple(next, counted, feedback)
    }

    // Quality increases as the angle difference grows (0° = no twist, 45° = full quality)
    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val diff = kotlin.math.abs(leftAngle - rightAngle)
        return (diff / 45f).coerceIn(0f, 1f) * 100f
    }
}

// Registry of all exercises available in the app.
// Add new exercises here to make them appear in the session picker.
object UpperBodyExercises {
    val all: List<UpperBodyExercise> = listOf(
        BicepCurlExercise(),
        SideStretchExercise(),
        ForwardFoldExercise(),
        SeatedTwistExercise(),
    )

    // Looks up an exercise by display name, defaulting to Bicep Curl if not found
    fun byName(name: String): UpperBodyExercise =
        all.firstOrNull { it.displayName == name } ?: BicepCurlExercise()
}
