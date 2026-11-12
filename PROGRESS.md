# OpenFight progress

> Public-safe progress log: no local paths, usernames, secrets or private notes. Machine-specific notes live in a
> gitignored `TASK.md`.

**Status:** `DONE` (playable; see known limitations)
**Target:** Fabric, Minecraft 26.3 (Java 25)
**Based on:** Epic Fight 21.17.3 for NeoForge 1.21.1, upstream commit `a78aa24b72e90a9d09f5fd61925e4369d117abaf` (code GPL-3.0; assets All Rights Reserved, pulled from the `upstream/` submodule at build time)

## Summary
OpenFight runs Epic Fight on Fabric 26.3: combat, skills, animations, first-person model, emotes from datapacks,
multiplayer, and Iris shaders. Online services from the original (account login, remote cosmetics, update checks)
were removed. The repository contains no Epic Fight assets.

## Milestones
| # | Milestone | Status | Notes |
|---|---|---|---|
| 1 | Rebrand (package `dev.war.openfight`, mod id `openfight`, namespace `epicfight` kept) | ✅ | |
| 2 | Build-time asset assembly from the pinned submodule | ✅ | `verifyNoArrAssets` guards the repo |
| 3 | Gameplay (world join, combat, skills, first person) | ✅ | |
| 4 | Iris / shader packs | ✅ | |
| 5 | Shoulder Surfing Reloaded compat | ✅ | |
| 6 | GPU (compute shader) skinning | ⛔ | 26.3 exposes no compute dispatch API |

Legend: ✅ done, 🚧 in progress, ⏳ not started, ⛔ blocked.

## Changes

### Rebrand and licensing
- Java package `dev.war.openfight`, Fabric mod id `openfight`. The `epicfight` resource, registry, command and network namespace is kept so datapacks, resource packs and worlds stay compatible.
- Removed online services: Epic Fight account login, the native `ServerCommunicationHelper` library, remote cosmetics, remote meshes/textures and the version notifier. Local datapack emotes still work; multiplayer is unaffected.
- Removed inactive NeoForge and third-party compat mixin configs.

### Assets
- No Epic Fight asset is committed. Gradle copies `upstream/src/main/resources/assets` into the build, applies `overlay/assets` (26.3 item definitions, equipment, new translation keys only) and merges language files key by key.
- The 104 per-context item models needed by 26.3 are generated at build time from the upstream `separate_transforms` models (byte-identical to the previous hand-split files).
- `verifyNoArrAssets` (part of `check`) fails the build if any upstream asset, or a model folder in the overlay, is tracked.

## Fixes
| Issue | Root cause | Fix | Verified by |
|---|---|---|---|
| Exploded triangle shards with Iris shader packs | Meshes were drawn through custom TRIANGLES copies of vanilla entity pipelines, which Iris does not recognise | Emit meshes as degenerate quads through the vanilla entity render types; overlay, particle and afterimage pipelines use QUADS | Smoke test with Iris and Complementary Unbound, first and third person screenshots |
| Cannot move after respawning | NeoForge's client `Clone` event had no Fabric equivalent, so `ControlEngine` kept the dead player | `MixinClientPacketListener` calls `onClonedInClient(old, new)` at the end of `handleRespawn` | `smoke_client.py --respawn`: forward input moves the player after respawn |
| Inverted WASD with Shoulder Surfing | The compat plugin never loaded (sources excluded, json used `entrypoint` instead of `entrypoints`), so both mods rotated movement | Ship and compile the compat for SSR 5.2.0; it disables Epic Fight's own third person camera like upstream | `smoke_client.py --respawn --shoulder-surfing`: movement follows the camera (cosine 1.00) |
| Datapack weapons of the same type overwrote each other's attributes | Weapon capability builders shared attribute maps with their presets | Builders copy the maps | Epic Fight x Spartan Weaponry compat: 434/434 capabilities resolve with their own values |

## Not ported / known limitations
- GPU (compute shader) skinning: disabled, CPU skinning is used. Needs a quad-compatible compute output path and a 26.3 compute API.
- Model preview in the datapack editor screens: render-to-texture not yet ported to 26.3's GUI picture-in-picture path.
- Controlify (gamepad) integration: not ported.
- Not exercised by automated tests: Shoulder Surfing free-look with Epic Fight lock-on.

## Verification
| Check | Result |
|---|---|
| `./gradlew build` (includes `verifyNoArrAssets` and the mixin target audit, 47 mixins) | PASS |
| `scripts/smoke_client.py` (title screen, create world, join, 600 combat ticks, weapon switches, battle mode toggles, runtime mixin audit) | PASS |
| `scripts/smoke_client.py --respawn` | PASS |
| `scripts/smoke_client.py --respawn --shoulder-surfing` | PASS |
| Smoke with Iris + Complementary Unbound | PASS, no shards in screenshots |

## How to reproduce the tests
```sh
git clone --recursive https://github.com/KaianGuedes/OpenFight.git
cd OpenFight
./gradlew build
python scripts/smoke_client.py                       # combat smoke
python scripts/smoke_client.py --respawn             # respawn check
python scripts/smoke_client.py --respawn --shoulder-surfing
```
Use a JDK 25 via `JAVA_HOME`. Screenshots from `-PportSmoke -PportScreens` land in `run/screenshots/`.

## Next steps
1. Port the datapack editor model preview to the 26.3 GUI picture-in-picture path.
2. Quad-compatible compute skinning if 26.3 gains a compute API.
3. Replace the Epic Fight assets with open ones so a jar can be published.
