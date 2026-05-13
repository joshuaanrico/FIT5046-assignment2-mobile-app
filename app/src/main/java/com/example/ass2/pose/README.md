# pose/

Real-time pose estimation using MediaPipe and CameraX. This package contains the exercise logic, skeleton rendering, and data model for the live session screen.

## Files

### `UpperBodyExercise.kt`
Abstract base class for all pose exercises. Each exercise is implemented as a **finite state machine (FSM)** with 3 states (e.g. EXTENDED → CURLING → CONTRACTED). Subclasses implement:
- `initialState()` — the starting state string
- `computeAngles()` — which MediaPipe landmarks to measure and how
- `step()` — FSM transitions and feedback messages based on joint angles

Also provides shared utilities:
- `angleBetween(a, b, c)` — calculates the joint angle at landmark `b` using the dot product formula
- `symmetryQuality()` — scores 0–100 based on left/right angle symmetry
- `process()` — called every camera frame; runs the FSM, debounces rep counting, and returns an `ExerciseUpdate`

---

### `UpperBodyExercises.kt`
Contains all concrete exercise implementations and a registry object.

| Class | Angle Measured | Rep Trigger |
|---|---|---|
| `BicepCurlExercise` | Shoulder–elbow–wrist (both arms averaged) | CURLING → CONTRACTED |
| `SideStretchExercise` | Elbow–shoulder–hip (peak of both sides) | RAISING → RAISED |
| `ForwardFoldExercise` | Shoulder–hip–knee (both sides averaged) | FOLDING → FOLDED |
| `SeatedTwistExercise` | Difference in wrist–elbow–shoulder between sides | TWISTING → TWISTED |

`UpperBodyExercises` is a singleton registry (`object`) that holds the list of all available exercises and provides a `byName()` lookup used when navigating to a session.

---

### `ExerciseUpdate.kt`
A simple data class snapshot produced every camera frame by `UpperBodyExercise.process()` and emitted to the UI via `PoseViewModel`.

| Field | Description |
|---|---|
| `repCount` | Total completed reps for the current session |
| `state` | Current FSM state (e.g. `"CURLING"`) |
| `feedback` | Coaching message shown to the user |
| `poseQuality` | Score from 0–100 rating the current frame's form |
| `leftAngle` | Computed angle for the left side (degrees) |
| `rightAngle` | Computed angle for the right side (degrees) |

---

### `PoseOverlayView.kt`
A transparent Android `View` rendered on top of the CameraX `PreviewView`. Draws the detected skeleton on the live camera feed using `Canvas`:
- **White lines** — bone connections from `PoseLandmarker.POSE_LANDMARKS`
- **Teal dots** — joint positions at each visible landmark

Only landmarks with a visibility score ≥ 0.5 are drawn to avoid rendering noisy or occluded points. Normalized landmark coordinates (0–1) are scaled to the view's pixel dimensions at draw time.
