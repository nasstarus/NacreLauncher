# Architecture

## Initial structure

- `app/`: native Android application, launcher activity, Compose UI and unit tests.
- `gradle/`: version catalog and Gradle wrapper configuration.
- `docs/`: product, architecture and contributor workflow.
- `.vscode/tasks.json`: editor tasks for focused tests and debug assembly.
- `.github/copilot-instructions.md`: short project-wide agent guidance that links to the detailed docs.

The project uses Kotlin, Jetpack Compose and Gradle. The Gradle wrapper is the canonical build entry point. Dependencies are versioned in `gradle/libs.versions.toml`; normal Gradle caches remain outside the repository rather than vendoring downloaded libraries.

## Current responsibilities

- `LauncherActivity` registers the Home experience, refreshes the app catalog when resumed, connects lifecycle-aware state to Compose and shows an actionable message when a launch fails.
- `LauncherViewModel` queries launchable activities through `PackageManager`, stores favorite package names and per-package launch counts in private preferences, exposes both as state and starts the selected app.
- `AppCatalog` contains deterministic search and dock-ranking behavior that can be tested as plain Kotlin.
- `LauncherHome` renders the date, frequently used app row, search field and adaptive app grid.

The manifest declares a `MAIN`/`HOME`/`DEFAULT` activity and a package-visibility query for `MAIN`/`LAUNCHER`. Do not request broad package visibility: only launcher activities are needed for this catalog.

## Next milestone direction

- Replace the usage-ranked dock with an explicit, locally persisted set of favorite package names; dock membership must not depend on launch counts.
- Separate the desktop Home surface from the searchable all-apps library. Keep library navigation available through both a visible accessible control and a three-finger upward drag.
- Implement the dock as Compose/UI within the launcher's Home surface. “Floating” is a visual treatment, not a system overlay or arbitrary third-party app window.
- Test favorite selection/order and gesture recognition independently where possible. Validate touch dispatch, Android navigation/accessibility conflicts, and the visible fallback on an emulator and physical Nothing Phone (1).

## Android capability boundaries

A regular launcher can request to be the Home app and launch exported app activities. Android does not provide a general API for one ordinary app to re-parent arbitrary third-party activities into freely movable windows. Use only the target app's supported Android multi-window/PiP behavior unless a separately approved privileged integration is feasible and maintainable.

Widgets use Android's AppWidget APIs and have lifecycle/configuration requirements; they are not equivalent to drawing arbitrary app UI. Treat them as a separate milestone.

## State and privacy

Favorite package names and launch counts are stored locally in private preferences. The explicit-favorites milestone is replacing launch-count-based dock ranking; do not infer favorites from launch history. Keep any retained usage data private to the app, and do not add telemetry, account sync or network permissions without an explicit product requirement.

## Testing boundaries

- Unit-test app filtering, ordering and dock ranking without Android framework behavior.
- Test Android integration (Home role, package discovery, app launch and lifecycle) on an emulator.
- Verify Nothing OS behavior, default-home selection, display cutouts, gesture navigation and any future multi-touch interaction on a physical Nothing Phone (1).
