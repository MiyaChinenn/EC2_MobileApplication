# EC2 Mobile Application

This branch contains the full Elective2-Mobile workspace, including the MenuAnNam Android flashcard app and course PDF.

## Contents
- `MenuAnNam/` – Android Studio project for the Menu An Nam flashcard app
- `MenuAnNam.zip` – zipped copy of the same project (optional; you can ignore or remove if using Git LFS)
- `Compulsory Elective 2_Mobile Programming_Manuel_Assoc. Prof. Manuel Clavel.pdf` – course material

## Branches
- **master** (this branch): full workspace with PDF and zip
- **main**: only the `MenuAnNam` Android project

## Prerequisites
- Android Studio Giraffe+ with Android SDK 34
- JDK 17 (bundled JDK from Android Studio is fine)
- Git
- (Optional) Git LFS if you want to keep `MenuAnNam.zip` tracked efficiently

## Quick start (master branch)
1) Clone and checkout master
   ```cmd
   git clone https://github.com/MiyaChinenn/EC2_MobileApplication.git
   cd EC2_MobileApplication
   git checkout master
   ```
2) Open `MenuAnNam/` in Android Studio (or keep project view at the repo root and select that module).
3) Let Gradle sync; then build/run on an emulator or device (API 26+).

## Build & test from the command line
Run from the repo root or inside `MenuAnNam/`:
```cmd
cd MenuAnNam
./gradlew assembleDebug      # build APK
./gradlew test               # run unit tests (Robolectric/Compose)
./gradlew connectedAndroidTest  # instrumentation tests (device/emulator required)
```
On Windows cmd, use `gradlew.bat` instead of `./gradlew`.

## App notes
- The app uses DataStore to cache `EMAIL` and `TOKEN` after login.
- API endpoints are configured via Retrofit with default AWS Lambda URLs in `NetworkService.kt`.
- Audio files are generated from the API, saved internally (MD5-hashed mp3), and played with ExoPlayer.

## Large file note
`MenuAnNam.zip` (~64 MB) exceeds GitHub’s recommended 50 MB. If you don’t need it, delete it before pushing; otherwise consider Git LFS.
