# FIT5046 Assignment 4

## How to run — step by step

Follow these steps in order. The Firebase steps are required once per machine.

1. **Open Android Studio.**

2. **Clone the repository** in Android Studio.

3. **Get added to the Firebase project.** Firebase setup (steps 5–9) can only be done if you have access to the **sitandfit** Firebase project. Ask the project owner to invite your Google account:
   - In the [Firebase Console](https://console.firebase.google.com) → **sitandfit** project → gear icon → **Users and permissions** → **Add member** → enter your email.
   - Role: ask to be added as **Owner**. (Editor *may* be enough to add SHA-1 fingerprints and download `google-services.json`, but Owner avoids permission errors — use Owner unless told otherwise.)
   - Wait for the invite email and accept it before continuing.

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

   If you get `keytool: command not found` on Mac, use Android Studio's bundled keytool:
   ```bash
   /Applications/Android\ Studio.app/Contents/jbr/Contents/Home/bin/keytool -list -v \
     -keystore ~/.android/debug.keystore \
     -alias androiddebugkey \
     -storepass android \
     -keypass android
   ```

   Copy the value next to `SHA1:` in the output (looks like `AB:CD:12:34:...`).

5. **Register your SHA-1 in Firebase.**
   - Go to the [Firebase Console](https://console.firebase.google.com) → open the **sitandfit** project.
   - Click the gear icon (top-left) → **Project Settings**.
   - Scroll down to **Your apps** → click **com.example.ass2**.
   - Under **SHA certificate fingerprints**, click **Add fingerprint**.
   - Paste your SHA-1 value → click **Save**.

6. **Download `google-services.json`.** On the same Project Settings page, click **Download google-services.json**.

7. **Place `google-services.json`** at `app/google-services.json` — inside the `app/` folder at the root of this repo, alongside `build.gradle.kts`:
   ```
   A24-Lab07-G01/
   ├── app/
   │   ├── google-services.json   ← goes here
   │   ├── build.gradle.kts
   │   └── src/
   └── build.gradle.kts
   ```
   Do **not** commit this file — it is already in `.gitignore`. Other developers are unaffected; their existing SHA-1 entries stay registered.

8. **Set up the emulator camera (use your laptop webcam).** The app needs camera access for pose estimation. By default the emulator uses a virtual camera — switch it to your webcam:
   - In Android Studio, open **Device Manager** in the right sidebar (or **View → Tool Windows → Device Manager**).
   - Find your emulator → click the **three-dot menu (⋮)** → select **Edit**.
   - Click **Show Advanced Settings** at the bottom of the configuration window.
   - Scroll to the **Camera** section → set **Front** and/or **Back** to **Webcam0**.
   - Click **Finish**.
   - Back in Device Manager, click the **three-dot menu (⋮)** → **Cold Boot Now** (the camera setting only takes effect on a fresh start).

9. **Run the project** on the emulator (or a physical device).
