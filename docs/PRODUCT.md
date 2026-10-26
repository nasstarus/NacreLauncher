# Product scope

## Vision

Make an Android phone feel like a calm, compact desktop: a small dock for often-used apps, an uncluttered home surface and a larger searchable space for everything else. Visual direction is minimal and Fedora Workstation-inspired, not a copy of Fedora branding or assets.

The Nothing Phone (1) is the first hands-on test device. App behavior should use standard Android APIs and avoid Nothing-only assumptions so the launcher can work on other compatible Android devices.

## First milestone

Deliver the launcher foundation:

1. Register as an Android Home app and be selectable as the default launcher.
2. Discover and launch installed apps that expose a launcher activity.
3. Present a minimal home surface with a frequently used app dock and full app collection.
4. Let users find apps with a text filter.
5. Rank dock entries by launch counts stored locally on the device.

The first version does not promise configurable dock pinning, folders, widgets, gesture customization or app windowing. Use small, working increments and record decisions as behavior becomes real.

## Later milestones

### Widgets

Investigate standard AppWidgetHost support, configuration flows, resizing, persistence and screen-space behavior. Keep widget hosting distinct from the app catalog and make the home layout recoverable after process death.

### App switching gestures

Prototype three-finger upward motion to reveal a dock/switcher and three-finger horizontal movement to change the active app. Check gesture conflicts with Android system navigation, accessibility services and applications. Make gestures configurable or disableable if they conflict; validate on physical Nothing hardware before treating them as reliable.

### Small-window apps

First investigate standard Android multi-window and Picture-in-Picture support. PiP is intended for apps that opt into it (commonly video); it does not let a normal launcher embed every arbitrary third-party app in a freely movable box. Any privileged, rooted or OEM-specific approach is outside the standard-app MVP and requires an explicit feasibility and maintenance decision before implementation.

## Design principles

- Minimal visual hierarchy; generous spacing and restrained color.
- App identity and launch behavior should be clear and accessible.
- Prefer Android system capabilities over overlays, privileged access or device-specific hacks.
- Keep private launch-usage data local; do not add analytics or network access without a separate product decision.

