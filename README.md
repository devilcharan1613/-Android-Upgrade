# Android Upgrade

A native Kotlin/Jetpack Compose app providing compatible modern utilities for Android 10+ devices. It does **not** upgrade the operating system or replace protected system UI.

## Current milestone: foundation (0.1.0)

Implemented:
- Native Compose app shell with Home, Features and Settings.
- Android 10 minimum SDK (API 29), compile/target SDK 36.
- Device API/release compatibility reporting.
- Offline manifest: no INTERNET permission, analytics, ads, login or cloud SDK.
- Optional launcher HOME activity entry entry.
- Settings shortcuts for system, accessibility, notification access and Wi-Fi settings.
- Feature directory clearly labels starter, permission-dependent and planned modules.

Not yet implemented: full control center, notification listener/history, encrypted vault, battery diagnostics suite, floating tools, accessibility service, camera scanner, automation engine, local LLM, full launcher customization and release signing. These must not be represented as complete.

## Build
Open in Android Studio with JDK 17 or 21 and Android SDK Platform 36. The repository CI workflow builds debug APK, runs unit tests and lint.

```bash
gradle wrapper --gradle-version 8.11.1
./gradlew assembleDebug
./gradlew test
./gradlew lint
```

## Privacy
No INTERNET permission is declared. No analytics, ads, login or cloud service. Future features requiring special access must explain why, request explicit consent and provide a fallback.

## Compatibility
minSdk 29 means Android 10+ can install the app; it does not mean every feature works on every device. Hardware, Android API level, OEM behavior and explicit permissions determine availability.
