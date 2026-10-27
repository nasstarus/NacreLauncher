# Nacre Launcher — Copilot Workspace Instructions

These repository-wide instructions apply to planning, coding, testing, Git worktrees, commits, issues and pull requests. Read [product scope](../docs/PRODUCT.md), [architecture](../docs/ARCHITECTURE.md), [development workflow](../docs/DEVELOPMENT.md) and the [README](../README.md) for details before changing behavior or development procedures.

## Plan work in GitHub

- Treat GitHub issues as the development backlog and status record. Keep the long-term product roadmap in `docs/PRODUCT.md`; technical decisions belong in `docs/ARCHITECTURE.md`. Avoid standalone plans for individual changes.
- Every GitHub issue, including parent issues, must contain exactly these four sections in this order, with no extra body sections: `Target Files`, `Current State`, `Desired State`, `Acceptance Criteria (TDD Hooks)`.
- List repository-relative files to inspect/change; describe the verified current behavior; specify the technical outcome and boundaries; and give measurable acceptance criteria, focused tests and any manual device checks.
- Use a parent issue to group a milestone and linked child issues for independently reviewable increments. Update issue checklists as work proceeds and close issues only when their acceptance criteria are met. If GitHub write access is unavailable, do not claim an issue was changed; provide the complete issue text and state what access/action is needed.

## Develop in micro-iterations

For each observable behavior: identify its acceptance test, add/update a focused test first, run it and observe failure when practical, implement the smallest coherent change, rerun the focused test, and review the diff. Move to the next behavior only when this increment is understood. Do not mix unrelated refactors into the change.

- Unit-test pure catalog/filter/ranking behavior; use emulator/device checks for Android integration.
- For relevant changes run `./gradlew :app:testDebugUnitTest` and `./gradlew :app:assembleDebug`; prefer a focused test selector while iterating.
- Requirements and install steps are in the README. Never report a physical Nothing Phone (1) check as passed unless it was actually performed; state remaining device checks explicitly.
- Update human-facing docs when behavior, scope, architecture or workflow changes.

## Android and privacy boundaries

- Prefer standard Android APIs and broad device compatibility. A regular launcher cannot embed arbitrary third-party apps in freely movable windows; follow the feasibility boundaries in the architecture and product docs.
- Keep launch-usage data on-device. Do not add telemetry, network access, broad package visibility, privileged/root behavior or other privacy-sensitive capabilities without explicit owner approval.

## Git, worktrees, commits and pull requests

- Do not develop directly on the shared default branch. Before editing, inspect `git status`, the current branch and remotes; preserve unrelated user changes.
- Use a small `feature/`, `fix/`, `test/`, `docs/` or `chore/` branch for each coherent change. For parallel work, create a separate worktree and branch (for example, `git worktree add ../nacre-<short-task> -b <type>/<short-task>`); do not reuse or remove another worktree without checking its status and obtaining authorization before destructive cleanup.
- Do not initialize Git, change remotes, discard user changes, force-push, or merge without explicit authorization. Do not include generated files, SDK paths, signing material, keys or secrets.
- Use a focused Conventional Commit subject (`test: cover dock ranking`, `docs: clarify device installation`). Include `Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>` in commits unless the owner asks otherwise. Commit only when requested.
- When asked to open a pull request, use a focused Conventional title and summarize behavior, tests/build results, issue links and unperformed device checks. Do not merge unless explicitly asked.
