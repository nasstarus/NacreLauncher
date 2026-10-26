# Nacre Launcher

A minimal Android home screen inspired by the focused, desktop-like feel of Fedora Workstation. The Nothing Phone (1) is the first physical test device; the project targets broadly compatible Android APIs.

## Current milestone

The first milestone is a usable home replacement with:

- A quiet, dark home surface
- A frequently used app dock, ranked by launch counts on this device
- A searchable collection of installed launchable apps
- A standard Android home-app entry point

Widgets, multi-touch app switching and floating windows are later milestones. In particular, ordinary third-party apps cannot reliably embed arbitrary other apps in movable windows through standard Android APIs. Later work must assess Android Picture-in-Picture and any other supported modes without promising universal freeform windows.

## Build and run

Requirements: JDK 17, Android SDK Platform 36 and the Android SDK Build Tools installed. Android Studio is optional; the checked-in Gradle wrapper is the build entry point. Set `ANDROID_HOME` or `ANDROID_SDK_ROOT` to the SDK location if it is not auto-detected.

VS Code is the primary development editor. Open the project root; use **Terminal → Run Task** for the configured Android unit-test and debug-assembly tasks. The same commands work in an integrated terminal.

```sh
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

Install on a connected device or emulator:

```sh
./gradlew :app:installDebug
```

On the Nothing Phone (1), select **Nacre Launcher** as the default Home app in Android's Home app settings. Android settings labels can differ by Nothing OS version.

## Project guide

- [Product scope and milestones](docs/PRODUCT.md)
- [Architecture and Android constraints](docs/ARCHITECTURE.md)
- [Development, testing and Git workflow](docs/DEVELOPMENT.md)
- [Workspace AI instructions](.github/copilot-instructions.md)
- [VS Code build/test tasks](.vscode/tasks.json)

## Repository setup

This workspace has not been initialized as a Git repository and has no GitHub remote. Follow the user-owned setup steps in [Development](docs/DEVELOPMENT.md#github-remote-setup) when ready to publish it.
