# Tap Kick Football (Android)

This repository now includes a complete Android Studio project that wraps the HTML game in a WebView with AdMob banner and rewarded ads.

## Project structure
- `app/src/main/assets/index.html`: Packaged game HTML.
- `app/src/main/java/com/example/tapkickfootball/MainActivity.kt`: WebView + AdMob integration.
- `app/src/main/res/layout/activity_main.xml`: WebView + banner layout.

## AdMob configuration
Replace the test IDs with your real AdMob IDs in **`app/src/main/res/values/strings.xml`**:
- `admob_app_id`: Your AdMob App ID (manifest uses this).
- `admob_banner_id`: Banner Ad Unit ID.
- `admob_rewarded_id`: Rewarded Ad Unit ID.

The test IDs included by default are:
- Banner: `ca-app-pub-3940256099942544/6300978111`
- Rewarded: `ca-app-pub-3940256099942544/5224354917`

## Build in Android Studio
1. Open Android Studio.
2. Select **Open** and choose this repository folder.
3. Let Gradle sync the project.
4. Click **Run** to build and install, or **Build > Build Bundle(s) / APK(s) > Build APK(s)** to generate an APK.

## WebView behavior
- JavaScript and DOM storage are enabled.
- The game loads locally from `file:///android_asset/index.html` and remains offline.
- Rewarded revive calls go through `AndroidBridge.showRewardedRevive()` and trigger `window.onAndroidRewardGranted('revive')` when the reward is earned.
