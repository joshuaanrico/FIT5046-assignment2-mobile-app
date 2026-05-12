# FIT5046 Assignment 2

How to run:
1. Open Android Studio
2. Clone the repository in android studio
3. Run the project on an emulator or physical device

## Firebase Setup (required for new developers)

Firebase requires your machine's SHA-1 fingerprint to be registered before authentication and other Firebase services work.

### Step 1 — Get your SHA-1 fingerprint

Open a terminal and run:

```bash
keytool -list -v \
  -keystore ~/.android/debug.keystore \
  -alias androiddebugkey \
  -storepass android \
  -keypass android
```

If you get "Unable to locate a Java Runtime", use Android Studio's bundled Java instead:

```bash
/Applications/Android\ Studio.app/Contents/jbr/Contents/Home/bin/keytool -list -v \
  -keystore ~/.android/debug.keystore \
  -alias androiddebugkey \
  -storepass android \
  -keypass android
```

Copy the value next to `SHA1:` in the output.

### Step 2 — Add your SHA-1 to Firebase

1. Go to [Firebase Console](https://console.firebase.google.com) and open the project
2. Click the gear icon → **Project Settings**
3. Scroll to **Your apps** → select the Android app
4. Click **Add fingerprint**
5. Paste your SHA-1 value → **Save**

### Step 3 — Download your google-services.json

1. On the same page, click **Download google-services.json**
2. Replace `app/google-services.json` in the project with the downloaded file
3. Do **not** commit this file — it is already in `.gitignore`

Only you need to do this. Existing developers are unaffected — their SHA-1 entries remain registered in Firebase.