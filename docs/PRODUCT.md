# Product scope

## Vision

Make an Android phone feel like a calm, compact desktop: a small dock for often-used apps, an uncluttered home surface and a larger searchable space for everything else. Visual direction is minimal and Fedora Workstation-inspired, not a copy of Fedora branding or assets.

The Nothing Phone (1) is the first hands-on test device. App behavior should use standard Android APIs and avoid Nothing-only assumptions so the launcher can work on other compatible Android devices.

## Completed milestone: Launcher foundation

The launcher foundation is implemented and verified on the Nothing Phone (1), per the project owner's device check.

1. Register as an Android Home app and be selectable as the default launcher.
2. Discover and launch installed apps that expose a launcher activity.
3. Present a minimal home surface with a frequently used app dock and full app collection.
4. Let users find apps with a text filter.
5. Rank dock entries by launch counts stored locally on the device.

## Next milestone: Desktop dock and app library

Replace the always-visible all-apps grid with a calm desktop-like Home surface and a separate app-library space:

1. Show a visually floating dock containing apps the user explicitly selects as favorites; keep its membership locally across restarts.
2. Move the searchable collection of all launchable apps to a secondary library surface. All other apps remain discoverable there.
3. Make the library reachable with a visible, accessible control and a three-finger upward drag. The visible control is the dependable fallback if the gesture is unavailable or conflicts with Android navigation/accessibility.
4. Keep existing app discovery, launch behavior, and search behavior. Use deterministic dock ordering and do not infer favorites from launch frequency.
5. Treat “floating” as visual layout inside the launcher's Home UI, not as an Android overlay or a promise to host other apps in movable windows.

Track this milestone in [GitHub parent issue #3](https://github.com/nasstarus/NacreLauncher/issues/3), with linked, independently reviewable child issues, focused tests and emulator/device checks. Validate three-finger behavior on the Nothing Phone (1) and retain the visible library control regardless of gesture results.

The foundation milestone's auto-ranked frequent dock is superseded by explicit user-selected favorites in this milestone. Do not retain or expose launch-count tracking solely to rank this dock.

## Later milestones

The launcher does not yet promise folders, widgets, app-switching gestures, or app windowing. Use small, working increments and record decisions as behavior becomes real.

### Widgets

Investigate standard AppWidgetHost support, configuration flows, resizing, persistence and screen-space behavior. Keep widget hosting distinct from the app catalog and make the home layout recoverable after process death.

### App switching gestures

After the app-library navigation gesture is validated, investigate three-finger horizontal movement to change the active app. Check conflicts with Android system navigation, accessibility services and applications. Make gestures configurable or disableable if they conflict; validate on physical Nothing hardware before treating them as reliable.

### Small-window apps

First investigate standard Android multi-window and Picture-in-Picture support. PiP is intended for apps that opt into it (commonly video); it does not let a normal launcher embed every arbitrary third-party app in a freely movable box. Any privileged, rooted or OEM-specific approach is outside the standard-app MVP and requires an explicit feasibility and maintenance decision before implementation.

## Design principles

- Minimal visual hierarchy; generous spacing and restrained color.
- App identity and launch behavior should be clear and accessible.
- Prefer Android system capabilities over overlays, privileged access or device-specific hacks.
- Keep private launch-usage data local; do not add analytics or network access without a separate product decision.
