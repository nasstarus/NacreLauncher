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

Do not commit directly to the shared default branch for ordinary development. Choose branch protection and merge strategy when the GitHub repository is created.

## GitHub remote setup

The project files are intentionally not initialized as a Git repository. The owner can create an empty GitHub repository, then run these commands from the project root, replacing the URL:

```sh
git init --initial-branch=main
git add .
git commit -m "chore: create Android launcher project"
git remote add origin https://github.com/OWNER/REPOSITORY.git
git push -u origin main
```

Prefer SSH if that is how the owner's GitHub account is configured. Confirm that no credentials or local machine configuration are staged before the first push.
