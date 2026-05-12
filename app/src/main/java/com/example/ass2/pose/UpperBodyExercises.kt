package com.example.ass2.pose

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

// 1. Hammer Curl - bilateral, shoulder-elbow-wrist angle
//    States: EXTENDED (>155) -> RISING (47-155) -> CONTRACTED (<=47)
//    Rep counted on RISING -> CONTRACTED
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

// 2. Bicep Curl - bilateral, shoulder-elbow-wrist angle
//    States: EXTENDED (>150) -> CURLING (50-150) -> CONTRACTED (<=50)
//    Rep counted on CURLING -> CONTRACTED
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

// 3. Tricep Dip - uses left shoulder-elbow-wrist angle (primary)
//    States: EXTENDED (>160) -> LOWERING (90-160) -> LOWERED (<=90)
//    Rep counted on LOWERED -> EXTENDED (pressing back up)
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

// 4. Shoulder Press - hip-shoulder-elbow angle (arm raised overhead)
//    States: RESTING (<90) -> PRESSING (90-160) -> PRESSED (>=160)
//    Rep counted on PRESSING -> PRESSED
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

// 5. Lateral Raise - hip-shoulder-elbow angle (arm raised to the side)
//    States: LOWERED (<30) -> RAISING (30-80) -> RAISED (>=80)
//    Rep counted on RAISING -> RAISED
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

// 6. Side Stretch - elbow-shoulder-hip angle (arm raised to the side overhead)
//    States: LOWERED (<30) -> RAISING (30-130) -> RAISED (>=130)
//    Rep counted on RAISING -> RAISED (uses max of both arms, one arm at a time)
class SideStretchExercise : UpperBodyExercise("Side Stretch", isBilateral = true) {
    override fun initialState() = "LOWERED"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 13, 11, 23), // left elbow-shoulder-hip
        angleBetween(landmarks, 14, 12, 24), // right elbow-shoulder-hip
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
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

    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val peak = maxOf(leftAngle, rightAngle)
        return ((peak - 30f) / (155f - 30f)).coerceIn(0f, 1f) * 100f
    }
}

// 7. Forward Fold - spine angle: shoulder-hip-knee
//    States: UPRIGHT (>100) -> FOLDING (70-100) -> FOLDED (<=70)
//    Rep counted on FOLDING -> FOLDED
class ForwardFoldExercise : UpperBodyExercise("Forward Fold", isBilateral = true) {
    override fun initialState() = "UPRIGHT"

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

    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val avg = (leftAngle + rightAngle) / 2f
        return ((100f - avg) / (100f - 45f)).coerceIn(0f, 1f) * 100f
    }
}

// 8. Seated Twist - wrist-elbow-shoulder angle difference between sides
//    States: NEUTRAL (diff <15) -> TWISTING (15-30) -> TWISTED (>30)
//    Rep counted on TWISTING -> TWISTED
class SeatedTwistExercise : UpperBodyExercise("Seated Twist", isBilateral = true) {
    override fun initialState() = "NEUTRAL"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 15, 13, 11), // left wrist-elbow-shoulder
        angleBetween(landmarks, 16, 14, 12), // right wrist-elbow-shoulder
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
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

    override fun estimateQuality(leftAngle: Float, rightAngle: Float): Float {
        val diff = kotlin.math.abs(leftAngle - rightAngle)
        return (diff / 45f).coerceIn(0f, 1f) * 100f
    }
}

// 9. Cat-Cow - spine angle (shoulder-hip-knee) cycling between arch and round
//    States: NEUTRAL (70-110) -> ARCHING (>110, cow) -> ROUNDING (<=70, cat)
//    Rep counted on ARCHING -> ROUNDING (one full cat-cow cycle)
class CatCowExercise : UpperBodyExercise("Cat-Cow", isBilateral = true) {
    override fun initialState() = "NEUTRAL"

    override fun computeAngles(landmarks: List<NormalizedLandmark>) = Pair(
        angleBetween(landmarks, 11, 23, 25), // left shoulder-hip-knee
        angleBetween(landmarks, 12, 24, 26), // right shoulder-hip-knee
    )

    override fun step(currentState: String, leftAngle: Float, rightAngle: Float): Triple<String, Boolean, String> {
        val avg = (leftAngle + rightAngle) / 2f
        val (next, feedback) = when (currentState) {
            "NEUTRAL" -> when {
                avg > 110f  -> "ARCHING"  to "Cow pose — lift your head and tailbone"
                avg <= 70f  -> "ROUNDING" to "Cat pose — round your back, tuck chin"
                else        -> "NEUTRAL"  to "Start: arch your back (cow), then round it (cat)"
            }
            "ARCHING" -> when {
                avg <= 70f  -> "ROUNDING" to "Cat pose! Round your spine fully"
                avg in 70f..110f -> "NEUTRAL" to "Flow through neutral to cat"
                else        -> "ARCHING"  to "Good cow pose — now flow to cat"
            }
            "ROUNDING" -> when {
                avg > 110f  -> "ARCHING"  to "Rep complete! Flow back to cow"
                avg in 70f..110f -> "NEUTRAL" to "Keep flowing — arch into cow"
                else        -> "ROUNDING" to "Good cat pose — now flow to cow"
            }
            else -> "NEUTRAL" to ""
        }
        val counted = currentState == "ARCHING" && next == "ROUNDING"
        return Triple(next, counted, feedback)
    }

    override fun estimateQuality(leftAngle: Float, rightAngle: Float) = symmetryQuality(leftAngle, rightAngle)
}

// Registry of all upper-body exercises available in the app
object UpperBodyExercises {
    val all: List<UpperBodyExercise> = listOf(
        HammerCurlExercise(),
        BicepCurlExercise(),
        TricepDipExercise(),
        ShoulderPressExercise(),
        LateralRaiseExercise(),
        SideStretchExercise(),
        ForwardFoldExercise(),
        SeatedTwistExercise(),
        CatCowExercise(),
    )

    fun byName(name: String): UpperBodyExercise =
        all.firstOrNull { it.displayName == name } ?: HammerCurlExercise()
}
