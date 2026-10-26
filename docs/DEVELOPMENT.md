# Development workflow

## Working style

Use test-driven development and micro-iterations to keep changes reviewable and context focused:

1. Pick one observable behavior and define its test or manual acceptance check.
2. Add a failing focused test before changing behavior where practical.
3. Implement the smallest coherent change.
4. Run the focused test, then the relevant build/check.
5. Review the diff and update the relevant docs in the same change.
6. Move to the next behavior only after the current increment is understood.

Avoid broad refactors and unrelated work in feature changes. If Android behavior cannot be tested locally, say so and record the exact device/emulator check still required.

## Plans, issues and micro-iterations

Keep plans at the level where they are useful:

- [Product scope](./PRODUCT.md) is the durable product roadmap: goals, milestones and explicit non-goals.
- [Architecture](./ARCHITECTURE.md) records the system structure, boundaries and technical decisions.
- GitHub issues are the work backlog and source of truth for status. Use a parent issue for a multi-step milestone and linked child issues for independently reviewable behaviors. A GitHub Project board is optional; it should reflect the issues, not duplicate their descriptions.
- Every GitHub issue, including parent issues, must contain exactly these four sections in this order. Treat the issue body as a self-contained implementation prompt; do not add other sections or text outside these headings:
  1. `Target Files` — explicitly list the relevant files for the task. Include existing files to inspect and expected files to change or add; use repository-relative paths.
  2. `Current State` — accurately describe existing behavior and relevant test coverage. Verify this against the current code before writing the issue.
  3. `Desired State` — state the exact technical outcome and boundaries, not just a broad goal.
  4. `Acceptance Criteria (TDD Hooks)` — give observable, testable conditions. Name focused tests/checks, expected assertions or behavior, and any manual device verification. For behavior changes, specify a focused test to add or update before implementation where practical.
- Keep each issue's scope small enough to complete and review in a few focused test-driven increments. Use parent issues to group related children and summarize their combined outcome; link children through GitHub's sub-issue relationship, not merely by mentioning their numbers in prose.

For each issue, update its checklist as work proceeds. Each micro-iteration should cover one observable behavior: state the expected result, add or update the focused test, observe it fail when practical, implement the smallest change, run the focused check, and review the diff before moving on. Add broader test/build checks before completion as required below. If investigation changes the approach, update the issue rather than maintaining a separate plan document.

Do not create standalone planning or tracking files for individual changes, and do not use chat/session notes as the only record of a decision or task. If GitHub issue write access is unavailable, do not imply that GitHub was updated; provide the complete four-section issue bodies and the exact steps or access needed from the repository owner.

## Tests and build checks

Run from the repository root:

```sh
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

VS Code is the primary editor. The workspace tasks file exposes these same test and build commands through **Terminal → Run Task**. Keep the JDK and Android SDK configured in the user's environment rather than hard-coding machine-specific paths in workspace settings.

For launcher integration, install to an emulator/device and verify:

- The app appears in Android's Home app chooser/settings and can be selected.
- The Home screen displays discovered launcher apps without duplicates.
- Search filters labels and package names; tapping an app launches it.
- Returning to Home refreshes the app inventory.
- The frequent-app dock ranks actual launches first and resolves ties consistently.
- Small and large display sizes remain usable.

Record device model, Android/Nothing OS version and exact manual steps with bug reports. Never state that a device-specific behavior is verified when it has only been built or tested in a JVM unit test.

## Git conventions

Use a small feature branch for each coherent change:

- `feature/<short-description>` for behavior
- `fix/<short-description>` for a defect
- `docs/<short-description>` for documentation
- `test/<short-description>` for test-only changes
- `chore/<short-description>` for maintenance

Use Conventional Commit subjects, for example `feat: add searchable app grid`, `test: cover dock ranking`, or `docs: describe Android windowing limits`. Keep commits focused and do not include generated build output, local SDK paths, signing material, keys or secrets.

Before proposing a merge:

1. Rebase or update from the shared default branch using the team's chosen policy.
2. Run focused tests and `./gradlew :app:assembleDebug`.
3. Review the full diff for unintended files, private data and stale documentation.
4. Open a pull request with the behavior, test evidence and any remaining device-only checks.
5. Merge only after review and required checks pass.

Do not commit directly to the shared default branch for ordinary development. Follow the repository's configured branch protection and merge strategy.
