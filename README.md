# FIT5046 Assignment 4

## How to run
1. Open Android Studio
2. Clone the repository in Android Studio
3. Complete Firebase setup below (required once per machine)
4. Run the project on an emulator or physical device

---

## Firebase Setup (required for new developers)

Firebase authentication needs your machine's debug SHA-1 fingerprint registered. Do this once per machine.

### Step 1 — Get your SHA-1 fingerprint

**Windows (PowerShell):**
```powershell
& "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**Mac/Linux (Terminal):**
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

### Step 2 — Register your SHA-1 in Firebase

1. Go to [Firebase Console](https://console.firebase.google.com) → open the **sitandfit** project
2. Click the gear icon (top-left) → **Project Settings**
3. Scroll down to **Your apps** → click **com.example.ass2**
4. Under **SHA certificate fingerprints**, click **Add fingerprint**
5. Paste your SHA-1 value → click **Save**

### Step 3 — Download and place google-services.json

1. On the same Project Settings page, click **Download google-services.json**
2. Place the file at **`app/google-services.json`** — that is, inside the `app/` folder at the root of this repo, alongside `build.gradle.kts`
   ```
   A24-Lab07-G01/
   ├── app/
   │   ├── google-services.json   ← goes here
   │   ├── build.gradle.kts
   │   └── src/
   └── build.gradle.kts
   ```
3. Do **not** commit this file — it is already in `.gitignore`

> Other developers are unaffected — their existing SHA-1 entries stay registered.