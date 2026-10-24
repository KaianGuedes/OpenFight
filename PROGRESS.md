# OpenFight rebrand progress

Status: IN PROGRESS

## Done
- Confirmed `upstream/` is pinned to `a78aa24b72e90a9d09f5fd61925e4369d117abaf`.
- Imported the EF port's build files, Gradle wrapper, scripts, and source tree, excluding `src/main/resources/assets` and `src/disabled`.
- Rebranded Java sources to `dev.war.openfight`, split Fabric `MOD_ID` from compatible `NAMESPACE`, removed inactive mixin metadata and online account/cosmetics/update-check code, and retained local emotes.
- Generated the 26.3 asset overlay (only new/changed non-language assets and changed language keys); build-time upstream assembly excludes native libraries, remote cosmetics assets, and WaveyCapes integration metadata.
- Added GPL-3.0 license, README, ignore rules, and the `verifyNoArrAssets` check.
- `gradlew clean build` passes with the mixin audit and component verifications.

## In progress
- `scripts/smoke_client.py` passes in-world: 600 combat ticks, 60 attacks, six weapon switches, three mode toggles, and runtime mixin audit. The script seeds a private run's onboarding/focus options for deterministic first launch.

## Next
- Re-run the final build/audit with all changes staged, commit work in small local commits, remove `WORKER.md`, and mark this file DONE.
