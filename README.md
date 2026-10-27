# Nacre Launcher

A minimal Android home screen inspired by the focused, desktop-like feel of Fedora Workstation. The Nothing Phone (1) is the first physical test device; the project targets broadly compatible Android APIs.

## Milestone status and next step

The launcher foundation is implemented and verified on the Nothing Phone (1), per the project owner's device check:

- A quiet, dark home surface
- A frequently used app dock, ranked by launch counts on this device
- A searchable collection of installed launchable apps
- A standard Android home-app entry point

The next milestone ([GitHub parent issue #3](https://github.com/nasstarus/NacreLauncher/issues/3)) is a cleaner desktop-style Home surface with a visually floating dock for user-selected favorite apps and a separate searchable app library. Open the library with both a visible accessible control and a three-finger upward drag. Here “floating” describes the dock's visual placement in the launcher UI, not an Android overlay or a way to host arbitrary apps in movable windows.

Widgets, app-switching gestures and app windowing remain later milestones. In particular, ordinary third-party apps cannot reliably embed arbitrary other apps in movable windows through standard Android APIs. Future work must assess Android Picture-in-Picture and other supported modes without promising universal freeform windows.

## Build and test

Requirements: JDK 17, Android SDK Platform 36, Android SDK Build-Tools 35.0.0 and 36.0.0. Android Studio is optional for builds and unit tests; the checked-in Gradle wrapper is the build entry point. Set `JAVA_HOME` to a JDK 17 installation and `ANDROID_HOME` or `ANDROID_SDK_ROOT` to your Android SDK location if they are not auto-detected.

VS Code is the primary development editor. Open the project root; use **Terminal → Run Task** for the configured Android unit-test and debug-assembly tasks. The same commands work in an integrated terminal.

```sh
# Set these for your machine if Gradle does not detect them automatically.
export JAVA_HOME=/path/to/jdk-17
export ANDROID_HOME="$HOME/Android/Sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"

# Run the JVM unit tests and build a debug APK.
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`. The unit tests run on your computer; they do not verify Android Home selection, package discovery, app launching or device-specific layout.

## Install and test on an emulator

1. Install Android Studio, open this project, and allow it to install the Android SDK components requested by Gradle. In **Tools → SDK Manager → SDK Tools**, also install **Android SDK Platform-Tools** and **Android Emulator** if they are not already installed.
2. In Android Studio, open **Tools → Device Manager**, choose **Create Virtual Device**, select a phone profile, then download and select an Android system image (API 28 or later).
3. Start the virtual device from Device Manager and wait for Android to finish booting.
4. In a terminal at the repository root, run:

   ```sh
   adb devices
   ./gradlew :app:installDebug
   ```

   `adb devices` should list the running emulator. If more than one device is connected, install the built APK directly to the intended emulator using its serial:

   ```sh
   adb -s <device-serial> install -r app/build/outputs/apk/debug/app-debug.apk
   ```

5. Press the emulator's Home button. Select **Nacre Launcher** in Android's Home-app chooser and choose **Always** (or select it in the system's Default apps → Home app settings).
6. Verify the launcher using the checklist below. To install a later build, rerun `./gradlew :app:installDebug`.

### Updating or removing an installed build

`./gradlew :app:installDebug` installs the new debug APK over the existing app because the application ID remains `dev.stepan.nacrelauncher`. You do not need to uninstall the previous version first; Android replaces its code and normally preserves the app's private data, including local launch counts. Keep the same application ID and signing key for Android to accept an APK as an update. Debug builds are signed consistently by the local Android build tools.

To remove the launcher, first select a different Home app in Android's default Home-app settings. Then uninstall it from the connected device:

```sh
adb uninstall dev.stepan.nacrelauncher
```

Uninstalling removes the app and its private data. It is not needed for normal updates.

## Install and test on a Nothing Phone (1)

1. On the phone, enable Developer options: open **Settings → About phone**, find **Build number**, and tap it seven times. Enter the device PIN if prompted.
2. In **Settings → System → Developer options**, enable **USB debugging**.
3. Connect the phone to your computer with a data-capable USB cable, unlock it, and accept the **Allow USB debugging?** prompt. Select **Always allow from this computer** only on a computer you trust.
4. From a terminal at the repository root, check that ADB recognizes the phone, then install:

   ```sh
   adb devices
   ./gradlew :app:installDebug
   ```

   The device should appear with status `device`. If it says `unauthorized`, unlock the phone and accept the debugging prompt. If multiple devices are connected, use the serial-specific `adb -s <device-serial> install -r app/build/outputs/apk/debug/app-debug.apk` command shown above.
5. Select **Nacre Launcher** as the default Home app in Android's Home-app chooser or **Settings → Apps → Default apps → Home app**. Labels and paths can differ by Nothing OS version.
6. Press Home and verify the checklist. For updates, rerun `./gradlew :app:installDebug`; to remove it, follow [Updating or removing an installed build](#updating-or-removing-an-installed-build).

## Launcher verification checklist

- The app appears in Android's Home-app chooser/settings and can be selected as the default.
- The Home screen lists launchable apps without duplicates; tapping an app launches it.
- Search filters apps by label and package name; clearing the query restores the full list.
- Returning to Home refreshes the app inventory after an app is installed or removed.
- The frequent-app dock reflects app launches and orders tied usage consistently.
- The layout remains usable on small and large emulator/device displays.

The emulator is useful for general integration and layout checks. Verify default-home behavior, USB installation and Nothing OS-specific behavior on the physical Nothing Phone (1) before considering those checks complete. Record the Android/Nothing OS version and exact steps for any issue.

## Project guide

- [Product scope and milestones](docs/PRODUCT.md)
- [Architecture and Android constraints](docs/ARCHITECTURE.md)
- [Development, testing and Git workflow](docs/DEVELOPMENT.md)
- [Workspace AI instructions](.github/copilot-instructions.md)
- [VS Code build/test tasks](.vscode/tasks.json)
