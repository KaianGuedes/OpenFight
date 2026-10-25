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

## Respawn freeze fix
- Root cause: upstream calls `VanillaGeneralClientEventHooks.onClonedInClient` from NeoForge's `ClientPlayerNetworkEvent.Clone`; the Fabric port never wired it. After death, 26.3's `ClientPacketListener.handleRespawn` replaces `Minecraft.player`, but `ControlEngine` kept the dead `LocalPlayer`/patch (verified by the smoke: "ControlEngine still references the previous LocalPlayerPatch" without the fix), so movement input was processed against a stale, movement-locked patch until a re-login re-ran `onPlayerLoggedIn`.
- Fix: `MixinClientPacketListener` captures the old player at HEAD of `handleRespawn` and calls `onClonedInClient(old, new)` at RETURN (after the new `LocalPlayer`, its `KeyboardInput` and game mode are set up, matching where NeoForge fires Clone). The hook is now null-safe for a missing new patch. Audited every other subscriber in upstream's `ClientEvents`: LoggingIn (JOIN) and LoggingOut (DISCONNECT) were already wired; Clone was the only gap. `ClientModBusEvent`/`EpicFightShaders`/`ClientConfig` bus subscribers are mod-bus lifecycle, not player events.
- Opt-in smoke: `python scripts/smoke_client.py --respawn` (`-PsmokeRespawn` / `-Dopenfight.smokeRespawn=true`) runs after the combat smoke: holds forward (baseline), kills the player, respawns, asserts the cached patch and `ControlEngine` follow the new `LocalPlayer`, then asserts forward input moves the player >= 1 block along the camera forward vector.

## Shoulder Surfing Reloaded compat
- Root cause of the inverted WASD: the compat plugin was never loaded (compat sources excluded from compilation, `shouldersurfing_plugin.json` excluded from resources, and the json used a singular `entrypoint` key where SSR 5.x reads `entrypoints`). Epic Fight's TPS camera therefore stayed active (`EpicFightCameraAPI.getRelativeMove` rotates movement by the EF camera yaw) while SSR also rotates the input vector to be camera-relative, so the rotation was applied twice. Before the fix the smoke measured 0.37 blocks of forward movement in 40 ticks with SSR loaded.
- Fix: SSR discovers plugins by reading `shouldersurfing_plugin.json` from every Fabric mod, so no `MinecraftMod`/`ICompatModule` registration is needed (upstream excludes SSR from that enum for the same reason) and the class is only loaded when SSR is. Only `compat/shouldersurfing` is now compiled; the json is shipped with `entrypoints`. `ShoulderSurfingCompat` was ported to the 5.2.0 API (`IShoulderSurfingPlugin.register(IEventBus)`, `ComputeCameraCouplingEvent` replacing the removed `ICameraCouplingCallback`, `IShoulderSurfing.getInstance()`), and disables the EF TPS camera via `EpicFightTpsCameraDisableState` as upstream does. `fabric.mod.json` now `suggests` shouldersurfing.
- Build: SSR `maven.modrinth:shoulder-surfing-reloaded:sEg9bfXx` (26.3-5.2.0 Fabric) as `compileOnly`; the dev runtime (plus its required Forge Config API Port) is opt-in with `-PwithShoulderSurfing` so the default smoke keeps the vanilla camera.
- Verification: `python scripts/smoke_client.py --respawn --shoulder-surfing` PASS with SSR in shoulder-surfing perspective (`cameraType=THIRD_PERSON_BACK shoulderSurfing=true epicFightTps=false`): forward input moved 8.5 blocks with camera-forward cosine 1.00 before death and 7.7 blocks / cosine 1.00 after respawn.
- Open: SSR's camera-decoupled free-look and EF lock-on sync (`BUILD_TRANSFORM_PRE`/`LOCK_ON_TICK` handlers) were ported but not exercised in the headless smoke.
