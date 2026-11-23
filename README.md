# EC2 Mobile Application (MenuAnNam)

## Project Description
- Built a Kotlin flashcard learning app (**MenuAnNam**) with Jetpack Compose UI, REST APIs, and DataStore auth for AWS Lambda TTS.
- Room Database + DAO for flashcard CRUD, Retrofit for token/audio generation, and ExoPlayer for MP3 playback.
- Added email/token login, Base64 audio decoding, MD5-hashed file caching, type-safe navigation, and randomized study sessions.
- **Stack:** Kotlin, Jetpack Compose, Room, Retrofit, DataStore, ExoPlayer, AWS Lambda, Coroutines, Gradle.

## Contents
- `MenuAnNam/` – Android Studio project for the MenuAnNam flashcard app

## Prerequisites
- Android Studio Giraffe+ with Android SDK 34
- JDK 17 (bundled JDK from Android Studio is fine)
- Git

## Quick Start
1. Clone the repository:
   ```bash
   git clone https://github.com/MiyaChinenn/EC2_MobileApplication.git
   cd EC2_MobileApplication
   ```
2. Open `MenuAnNam/` in Android Studio.
3. Let Gradle sync, then build/run on an emulator or device (API 26+).

## Build & Test from the Command Line
Run inside `MenuAnNam/`:
```bash
cd MenuAnNam
./gradlew assembleDebug         # build APK
./gradlew test                  # run unit tests (Robolectric/Compose)
./gradlew connectedAndroidTest  # instrumentation tests (device/emulator required)
```
On Windows cmd, use `gradlew.bat` instead of `./gradlew`.

## App Notes
- The app uses DataStore to cache `EMAIL` and `TOKEN` after login.
- API endpoints are configured via Retrofit with AWS Lambda URLs in `NetworkService.kt`.
- Audio files are generated from the API, saved internally (MD5-hashed `.mp3`), and played with ExoPlayer.
