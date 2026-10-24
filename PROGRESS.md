# OpenFight rebrand progress

Status: IN PROGRESS

## Done
- Confirmed `upstream/` is pinned to `a78aa24b72e90a9d09f5fd61925e4369d117abaf`.
- Imported the EF port's build files, Gradle wrapper, scripts, and source tree, excluding `src/main/resources/assets` and `src/disabled`.

## In progress
- Build-time upstream asset assembly and minimal overlay generation.
- Package/mod identity rebrand and online-service removal.

## Next
- Add README, GPL-3.0 license, ignore rules, and `verifyNoArrAssets` audit.
- Run build, audits, and the existing gameplay smoke harness; fix any failures.
- Commit finished changes in small local commits, remove `WORKER.md`, and mark this file DONE.
