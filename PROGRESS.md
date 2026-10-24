# OpenFight rebrand progress

Status: DONE

## Done
- Confirmed `upstream/` is pinned to `a78aa24b72e90a9d09f5fd61925e4369d117abaf`.
- Imported the EF port's build files, Gradle wrapper, scripts, and source tree, excluding `src/main/resources/assets` and `src/disabled`.
- Rebranded Java sources to `dev.war.openfight`, split Fabric `MOD_ID` from compatible `NAMESPACE`, removed inactive mixin metadata and online account/cosmetics/update-check code, and retained local emotes.
- Generated the 26.3 asset overlay (only new/changed non-language assets and changed language keys); build-time upstream assembly excludes native libraries, remote cosmetics assets, and WaveyCapes integration metadata.
- Removed the 104 `separate_transforms` split models from `overlay/`; Gradle now generates the equivalent files from the pinned upstream models into build resources. Compared all 104 outputs with a pre-deletion backup; every file is byte-identical.
- Audited tracked files under `overlay/` and `src/main/resources/` against upstream main and generated resources; no other byte-identical copies were found. `verifyNoArrAssets` now rejects models under the Epic Fight overlay.
- Rewrote local history to remove the generated model path from every commit. The repository has never been pushed.
- Added GPL-3.0 license, README, ignore rules, and the `verifyNoArrAssets` check.
- `gradlew clean build` passes with the mixin audit and component verifications.

## In progress
- None.

## Next
- None. Final local commits are present; no remote was contacted. `WORKER.md` has been deleted.

## Final verification
- `gradlew clean build`: PASS after history cleanup, including `verifyNoArrAssets`, 47 mixin target checks, and the port component verification tasks.
- `scripts/smoke_client.py`: PASS after history cleanup; title/create-world flow, joined player, combo data, 600 combat ticks, 60 attacks, six weapon switches, three mode toggles, and runtime mixin audit.
- The build-generated models match the pre-deletion backup byte for byte (104/104); `git log --all -- overlay/assets/epicfight/models/item/fabric_port` returns no commits.

## Iris / Complementary Unbound shard fix
- Root cause: meshes were drawn through custom TRIANGLES clones of vanilla entity pipelines/render types (`EpicFightRenderTypes.getTriangulated`). Iris only maps vanilla pipelines to its entity programs and vertex layout, so the clones were unknown to it and the shader pack received mismatched vertex data, producing exploded triangle shards.
- Fix: Epic Fight triangle-list meshes (`ClassicMesh`, `SkinnedMesh`, `SkinnedMesh` parts, cloth parts) now repeat the last vertex of each triangle, producing degenerate QUADS drawn through the unmodified vanilla render types. `getTriangulated` is the identity, overlay/particle/afterimage pipelines are QUADS, the compute-shader draw path (triangle output) is bypassed. Only the GUI triangle pipelines (not mesh/Iris related) remain TRIANGLES.
- Build: Iris 1.11.7 (compileOnly/runtimeOnly) and Sodium via the Modrinth maven for dev runs.
- Verification: `gradlew build` PASS; `scripts/smoke_client.py` PASS (with Iris + Complementary Unbound loaded); screenshots with `-PportSmoke -PportScreens` land in `run/screenshots/smoke_*.png` (third person and first person show no shards).
- Open: compute-shader skinning is disabled for now (needs a quad-compatible output path); third-person-front smoke capture can clip into terrain.
