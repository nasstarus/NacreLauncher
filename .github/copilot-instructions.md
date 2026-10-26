# Project instructions

- Read [product scope](../docs/PRODUCT.md), [architecture](../docs/ARCHITECTURE.md) and [development workflow](../docs/DEVELOPMENT.md) before changing product behavior.
- Work in small, test-driven increments: add or update a focused test, implement one behavior, run the narrow check, then review the diff.
- Keep Android API use standard and broadly compatible. Do not claim arbitrary third-party apps can be embedded in movable windows; follow the documented feasibility boundary.
- Keep launch-usage data local. Do not add telemetry, network access, broad package visibility, or privileged/root behavior without explicit approval.
- Run `./gradlew :app:testDebugUnitTest` and `./gradlew :app:assembleDebug` for relevant changes. Clearly call out device-only Nothing Phone (1) checks that were not performed.
- Update human-facing docs when scope, architecture or workflow changes. Do not initialize Git or configure a remote unless the owner asks.

