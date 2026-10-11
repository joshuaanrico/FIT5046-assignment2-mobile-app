# Sit&Fit

An Android app that helps people with disabilities exercise at home. It uses the phone camera to track body movement, count repetitions and give live feedback on form.

Built for Monash FIT5046 (Mobile and Distributed Computing Systems), Semester 1 2026, by a team of four. This is the Assignment 4 version. The earlier UI-only version of the app is on the `main` branch.

## What it does

- **Camera-based sessions.** MediaPipe pose detection reads body landmarks from the camera. The app works out joint angles, counts reps and shows feedback such as "Lower with control" while you move.
- **Four seated, upper-body exercises:** Bicep Curl, Side Stretch, Forward Fold and Seated Twist. Each one is a small state machine over joint angles, with a movement-symmetry quality score.
- **Pre-session check-in and heart-rate warning.** The warning threshold is based on the user's age. Heart rate is simulated from a dataset, not read from a sensor.
- **Exercise library** loaded from the public [wger](https://wger.de) API and cached locally with Room.
- **Session results, history and progress tracking.**
- **Reminders** scheduled with the Android alarm manager, restored after a reboot.
- **Accounts:** email sign-up and login, password reset, and Google sign-in.

## Tech stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose, MVVM with ViewModels
- Room for local storage
- Firebase Authentication and Google sign-in (Credential Manager)
- CameraX and MediaPipe Tasks Vision (pose landmarker)
- Retrofit with Gson for the wger API
- Min SDK 24, target SDK 36

## My contribution

Lead contributor, with 73 of 155 commits. I built:

- the app screens in Jetpack Compose
- the Room database layer for sessions, reminders, profile and exercises
- the reminder alarms and notifications
- session history, session detail and progress tracking
- most of the live session screen, integrated with the team's pose detection

Teammates built most of the MediaPipe pose detection, overlay and rep counting (Talent Lim, Yapa Jayasinghe), the wger API integration (Yashaswi), and the heart-rate simulation (Yapa Jayasinghe).

## Running it locally

The app uses Firebase Authentication, so you need your own Firebase project. `google-services.json` is not included in this repository.

The steps below are the original team setup guide. They refer to the team's own Firebase project, so create your own project and register your SHA-1 there instead.

---

## How to run: step by step

Follow these steps in order. The Firebase steps are required once per machine.

1. **Open Android Studio.**

2. **Clone the repository** in Android Studio.

3. **Create or get access to a Firebase project** with an Android app whose package name is `com.example.ass2`. Enable Email/Password and Google sign-in under Authentication.

4. **Get your SHA-1 fingerprint.** Firebase authentication needs your machine's debug SHA-1 registered.

   Windows (PowerShell):
   ```powershell
   & "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```

   Mac/Linux (Terminal):
   ```bash
   keytool -list -v \
     -keystore ~/.android/debug.keystore \
     -alias androiddebugkey \
     -storepass android \
     -keypass android
   ```

   Copy the value next to `SHA1:` in the output (looks like `AB:CD:12:34:...`).

5. **Register your SHA-1 in Firebase.** Firebase Console, then Project Settings, then **Your apps**, then `com.example.ass2`, then **Add fingerprint**.

6. **Download `google-services.json`** from the same Project Settings page.

7. **Place it at `app/google-services.json`**, next to `app/build.gradle.kts`. Do not commit this file (it is in `.gitignore`).

8. **Set up the emulator camera.** The app needs camera access for pose estimation. In Android Studio, open Device Manager, then Edit your emulator, then Show Advanced Settings, and set **Front** and **Back** camera to **Webcam0**. Then use **Cold Boot Now**, because the camera setting only applies on a fresh start.

9. **Run the project** on the emulator or a physical device.
