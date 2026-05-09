package com.example.ass2.pose

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

// ─────────────────────────────────────────────────────────────────────────────
// 1. Hammer Curl — bilateral, shoulder-elbow-wrist angle
//    States: EXTENDED (>155°) → RISING (47–155°) → CONTRACTED (≤47°)
//    Rep counted on RISING → CONTRACTED
// ─────────────────────────────────────────────────────────────────────────────
class HammerCurlExercise : UpperBodyExercise("Hammer Curl", isBilateral = true) {
    override fun initialState() = "EXTENDED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 13, 15),
        angleBetween(landmarks, 12, 14, 16),
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "EXTENDED" -> when {
                avg <= 47f  -> "CONTRACTED" to "Full curl! Great squeeze"
                avg <= 155f -> "RISING"     to "Curl both arms, keep elbows fixed"
                else        -> "EXTENDED"   to "Start: curl both arms up"
            }
            "RISING" -> when {
                avg <= 47f  -> "CONTRACTED" to "Full curl! Hold briefly"
                avg > 155f  -> "EXTENDED"   to "Extend fully between reps"
                else        -> "RISING"     to "Keep going — elbows pinned to sides"
            }
            "CONTRACTED" -> when {
                avg > 47f   -> "RISING"     to "Lower slowly and controlled"
                else        -> "CONTRACTED" to "Good squeeze — now lower"
            }
            else -> "EXTENDED" to ""
        }
        val counted = currentState == "RISING" && next == "CONTRACTED"
        return Triple(next, counted, feedback)
    }

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Bicep Curl — bilateral, shoulder-elbow-wrist angle
//    States: EXTENDED (>150°) → CURLING (50–150°) → CONTRACTED (≤50°)
//    Rep counted on CURLING → CONTRACTED
// ─────────────────────────────────────────────────────────────────────────────
class BicepCurlExercise : UpperBodyExercise("Bicep Curl", isBilateral = true) {
    override fun initialState() = "EXTENDED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 13, 15),
        angleBetween(landmarks, 12, 14, 16),
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

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Tricep Dip — uses left shoulder-elbow-wrist angle (primary)
//    States: EXTENDED (>160°) → LOWERING (90–160°) → LOWERED (≤90°)
//    Rep counted on LOWERED → EXTENDED (pressing back up)
// ─────────────────────────────────────────────────────────────────────────────
class TricepDipExercise : UpperBodyExercise("Tricep Dip") {
    override fun initialState() = "EXTENDED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 13, 15),
        angleBetween(landmarks, 12, 14, 16),
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val primary = leftAngle
        val (next, feedback) = when (currentState) {
            "EXTENDED" -> when {
                primary <= 90f  -> "LOWERED"  to "Deep dip — now press back up"
                primary <= 160f -> "LOWERING" to "Lower yourself — keep elbows back"
                else            -> "EXTENDED" to "Start: lower your body"
            }
            "LOWERING" -> when {
                primary <= 90f  -> "LOWERED"  to "Good depth! Press up now"
                primary > 160f  -> "EXTENDED" to "Fully extended — go again"
                else            -> "LOWERING" to "A bit lower for full benefit"
            }
            "LOWERED" -> when {
                primary > 160f  -> "EXTENDED" to "Rep complete! Great press"
                primary > 90f   -> "LOWERING" to "Push all the way up"
                else            -> "LOWERED"  to "Hold and press back up"
            }
            else -> "EXTENDED" to ""
        }
        val counted = currentState == "LOWERED" && next == "EXTENDED"
        return Triple(next, counted, feedback)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Shoulder Press — hip-shoulder-elbow angle (arm raised overhead)
//    States: RESTING (<90°) → PRESSING (90–160°) → PRESSED (≥160°)
//    Rep counted on PRESSING → PRESSED
// ─────────────────────────────────────────────────────────────────────────────
class ShoulderPressExercise : UpperBodyExercise("Shoulder Press", isBilateral = true) {
    override fun initialState() = "RESTING"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 23, 11, 13),  // left hip-shoulder-elbow
        angleBetween(landmarks, 24, 12, 14),  // right hip-shoulder-elbow
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "RESTING" -> when {
                avg >= 160f -> "PRESSED"  to "Arms overhead — lower with control"
                avg >= 90f  -> "PRESSING" to "Press upward — extend fully"
                else        -> "RESTING"  to "Start: press arms overhead"
            }
            "PRESSING" -> when {
                avg >= 160f -> "PRESSED"  to "Full press! Excellent"
                avg < 90f   -> "RESTING"  to "Lower arms to rest position"
                else        -> "PRESSING" to "Keep pressing — nearly there"
            }
            "PRESSED" -> when {
                avg < 90f   -> "RESTING"  to "Good rep! Press again"
                avg < 160f  -> "PRESSING" to "Lower slowly and controlled"
                else        -> "PRESSED"  to "Hold overhead, then lower"
            }
            else -> "RESTING" to ""
        }
        val counted = currentState == "PRESSING" && next == "PRESSED"
        return Triple(next, counted, feedback)
    }

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Lateral Raise — hip-shoulder-elbow angle (arm raised to the side)
//    States: LOWERED (<30°) → RAISING (30–80°) → RAISED (≥80°)
//    Rep counted on RAISING → RAISED
// ─────────────────────────────────────────────────────────────────────────────
class LateralRaiseExercise : UpperBodyExercise("Lateral Raise", isBilateral = true) {
    override fun initialState() = "LOWERED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 23, 11, 13),  // left hip-shoulder-elbow
        angleBetween(landmarks, 24, 12, 14),  // right hip-shoulder-elbow
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "LOWERED" -> when {
                avg >= 80f  -> "RAISED"  to "Arms at shoulder height — nice!"
                avg >= 30f  -> "RAISING" to "Raise to shoulder height, keep arms straight"
                else        -> "LOWERED" to "Start: raise both arms out to the side"
            }
            "RAISING" -> when {
                avg >= 80f  -> "RAISED"  to "Good height! Hold briefly"
                avg < 30f   -> "LOWERED" to "Lower fully then raise again"
                else        -> "RAISING" to "Lift a little more — no swinging"
            }
            "RAISED" -> when {
                avg < 30f   -> "LOWERED" to "Rep complete! Lower with control"
                avg < 80f   -> "RAISING" to "Lower slowly — fight gravity"
                else        -> "RAISED"  to "Hold, then lower with control"
            }
            else -> "LOWERED" to ""
        }
        val counted = currentState == "RAISING" && next == "RAISED"
        return Triple(next, counted, feedback)
    }

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Push-Up — shoulder-elbow-wrist angle (arm bend at bottom)
//    States: EXTENDED (>150°) → DESCENDING (70–150°) → DESCENDED (≤70°)
//    Rep counted on DESCENDING → DESCENDED (arms fully bent at bottom)
// ─────────────────────────────────────────────────────────────────────────────
class PushUpExercise : UpperBodyExercise("Push-Up", isBilateral = true) {
    override fun initialState() = "EXTENDED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 13, 15),
        angleBetween(landmarks, 12, 14, 16),
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "EXTENDED" -> when {
                avg <= 70f  -> "DESCENDED"  to "Full range — push up now"
                avg <= 150f -> "DESCENDING" to "Lower your chest toward the surface"
                else        -> "EXTENDED"   to "Start: lower yourself down"
            }
            "DESCENDING" -> when {
                avg <= 70f  -> "DESCENDED"  to "Bottom reached — press up!"
                avg > 150f  -> "EXTENDED"   to "Fully extended — go again"
                else        -> "DESCENDING" to "Lower a bit more for full range"
            }
            "DESCENDED" -> when {
                avg > 150f  -> "EXTENDED"   to "Rep complete! Great push"
                avg > 70f   -> "DESCENDING" to "Push all the way up"
                else        -> "DESCENDED"  to "Hold and push up"
            }
            else -> "EXTENDED" to ""
        }
        val counted = currentState == "DESCENDING" && next == "DESCENDED"
        return Triple(next, counted, feedback)
    }

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// ─────────────────────────────────────────────────────────────────────────────
// Registry of all upper-body exercises available in the app
// ─────────────────────────────────────────────────────────────────────────────
object UpperBodyExercises {
    val all: List<UpperBodyExercise> = listOf(
        HammerCurlExercise(),
        BicepCurlExercise(),
        TricepDipExercise(),
        ShoulderPressExercise(),
        LateralRaiseExercise(),
        PushUpExercise(),
    )

    fun byName(name: String): UpperBodyExercise =
        all.firstOrNull { it.displayName == name } ?: HammerCurlExercise()
}
