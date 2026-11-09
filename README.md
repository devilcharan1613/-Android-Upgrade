# Android Upgrade

Native Kotlin and Jetpack Compose utility app for Android 10+ devices. The app is offline-first and does not upgrade the Android operating system.

## Current version: 0.2.0 (development)

### Working in this milestone
- Home dashboard with Android API level and charge percentage.
- In-app Control Center with flashlight control (asks for camera permission) and shortcuts to Wi-Fi, Bluetooth, display, battery saver and sound settings.
- Installed-app drawer with app-name search and launch actions.
- Optional HOME entry point that opens the app drawer.
- Offline command helper for Wi-Fi, Bluetooth, display, Android settings, app drawer and battery status.
- Private local note stored in app-private preferences (not encrypted).
- Feature directory with explicit implementation status.
- No INTERNET permission, accounts, advertising, analytics or cloud service.

### Still incomplete
- Full notification manager and notification history.
- Encrypted privacy vault and app lock.
- Full battery diagnostics and performance monitoring.
- Multitasking/floating window tools.
- Dedicated accessibility service features.
- Camera/media suite.
- Automation and reminder scheduler.
- Local generative AI model.
- Full launcher customization, widgets and icon packs.
- Release signing and production hardening.

## Build

Open the project in Android Studio with JDK 17 and Android SDK Platform 36.

```bash
gradle assembleDebug
gradle test
gradle lint
```

GitHub Actions runs the same checks on pushes and pull requests. Download the generated debug APK from the workflow run's Artifacts section.

## Permissions and privacy

- CAMERA is requested only when the user activates the flashlight. If denied, the rest of the app remains usable.
- App visibility is limited to activities that declare the standard launcher intent.
- No INTERNET permission is declared.
- The local note is app-private but not encrypted. Do not store passwords or highly sensitive information in it.

## Platform limitations

Android protects system Quick Settings and privileged settings. This app provides its own in-app shortcuts and opens the relevant Android settings pages; it does not silently alter protected system controls. Hardware availability, Android API level and OEM behavior affect functionality.

See [DEVELOPER_AUDIT.md](DEVELOPER_AUDIT.md) for the implementation inventory, audit findings, test status and remaining work.
