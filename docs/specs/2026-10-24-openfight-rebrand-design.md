# OpenFight — fork & rebrand design (sub-project 1)

## Goal
Public GitHub repo `KaianGuedes/OpenFight`: a Fabric (Minecraft 26.3) port of Epic Fight, faithful to the original, compatible with Epic Fight datapacks/resource packs. Code GPL-3.0-or-later. **No Epic Fight ARR asset is ever committed.** No jar is published for now; users build locally.

## Source
- Code: the working personal port at `C:\Users\eugue\Downloads\ef-port\proj` (git history NOT carried over; fresh history).
- Assets: git submodule `upstream/` → https://github.com/Yesssssman/epicfightmod pinned at `a78aa24b72e90a9d09f5fd61925e4369d117abaf`.

## Decisions
- Java package `yesman.epicfight` → `dev.war.openfight` (all classes, mixins, mixin configs, entrypoints, access widener refs).
- Fabric mod id `openfight`. Resource/registry/network/command namespace stays `epicfight` (IDs like `epicfight:iron_longsword`, `/epicfight`, saves stay compatible). Split constants: `MOD_ID = "openfight"`, `NAMESPACE = "epicfight"`; every former `MODID` use is classified: resource/registry/packet IDs → NAMESPACE; FabricLoader mod-container lookups/metadata → MOD_ID.
- Display name "OpenFight" in fabric.mod.json, settings screen, titles, chat messages.
- Assets assembled at build time: copy `upstream/src/main/resources/assets/**` → build dir, then apply `overlay/assets/**` (our own files: 26.3 `items/`, `equipment/`, `models/item/fabric_port`, etc.), merging `lang/*.json` key-by-key (overlay wins). Our lang overlay contains only keys the port added/changed. Excluded from upstream copy: `assets/epicfight/nativelib/**`.
- `src/main/resources/data/**` (not ARR) stays in the repo.
- Online services removed: Epic Fight account login, native `ServerCommunicationHelper` lib, remote cosmetics/capes/skins, remote meshes/textures, version notifier. Local datapack emotes keep working. Multiplayer is unaffected.
- Remove inactive NeoForge/compat mixin configs (fgm, geckolib, skinlayers3d, vampirism, werewolves, platform.neoforge) and `src/disabled/`.
- LICENSE = GPL-3.0; README: credit + link to Epic Fight, states repo contains no Epic Fight assets, build instructions (clone with `--recursive`), and the grant-back clause required by Epic Fight's README fork terms; AI-assistance disclosure.
- `.gitignore`: build outputs, run/, .gradle/, .idea/.

## Verification
- `./gradlew build` passes (incl. existing mixin/component audits).
- In-world smoke test (from the port) passes.
- New `verifyNoArrAssets` task in `check`: fails if any file under `src/main/resources/assets` exists in the repo, or any tracked file is byte-identical to a file in upstream assets.
- `git ls-files` contains no `.dll/.so/.dylib`, no upstream asset copies.

## Out of scope (later sub-projects)
Open replacement assets; publishing (Modrinth/CurseForge); own cosmetics service; ModelPreviewer PIP bridge.
