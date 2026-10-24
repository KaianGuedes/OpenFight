from pathlib import Path
import re
root=Path(__file__).resolve().parents[1]
p=root/'PORT_NOTES.md';s=p.read_text(encoding='utf-8')
s=re.sub(r'\n{3,}','\n\n',s).replace('Continuation â€”','Continuation -')
a=s.index('## Status');b=s.index('## Baseline',a)
s=s[:a]+'''## Status

Build/title-screen milestone achieved (19 October 2026): **about 0 compiler errors (deduplicated)**. Full `build` passes, including all four component checks and the 41-mixin target preflight. The isolated dev client completed `MixinEnvironment.audit()`, applied all 41 configured mixins, remained on the title screen for 100 client ticks, saved a screenshot, and shut down normally without resource-pack recovery. Evidence: MIXIN_RUNTIME_AUDIT.txt and verification/title-screen.png.

This is still an incomplete gameplay port: no world/combat playthrough has been performed. Existing limitations include the stubbed model previewer, compute shaders, VanillaModelTransformer and EpicFightShaders, and unverified custom rendering/input/event behavior described below.

'''+s[b:]
s=s.replace('Client startup is being verified separately.', 'Client startup/title-screen verification also passes; see MIXIN_RUNTIME_AUDIT.txt.').replace('Build evidence is in full-build.log;', 'Final build evidence is in final-build.log (1,091 JSON files parsed);')
a=s.index('## Remaining\n');b=s.index('## Progress journal',a)
s=s[:a]+'''## Remaining

- Create an isolated world and exercise combat, skills, saving/loading, camera/input, armor, particles and animation rendering.
- Validate the gameplay event bridges and first-person submission. Successful mixin transformation does not prove that every hook executes with the intended behavior.
- Finish the documented stubs and rendering deviations; the build/title milestone is not a playable-port claim.

'''+s[b:]
a=s.index('## Remaining work in priority order');b=s.index('The source migration scripts',a)
s=s[:a]+'''## Remaining work in priority order

1. Run an isolated world and compare combat, skills, equipment, projectile impacts, entity sizing, effects, movement, disk saves and dimension changes with the pinned upstream. Audit gameplay callbacks and datapack/skill synchronization at runtime.
2. Wire/validate native first-person hand submission, boss overlays, input edge cases and respawn cloning. The existing engine methods compiling does not prove all replacement callbacks are connected.
3. Compare custom particle additive/depth/stencil rendering, armor snapshots, cloth, outlines and model/item transforms visually. The filled-map adapter currently uses a fixed item-model submission.
4. Replace the existing ModelPreviewer, VanillaModelTransformer and shader/compute stubs. Dynamic emote data and custom rarity remain unverified.
5. Inspect startup diagnostics: the upstream weapon_data extension registry has no built-in entries and triggers vanilla's empty-registry development diagnostic; the intentionally empty fracture model has no particle texture; the intentionally silent sfx.no_sound event has no sound. Windows performance-counter and offline development-session authentication diagnostics also occur. The successful smoke run has no Epic Fight resource reload exception, recovery or mixin transformation failure.

'''+s[b:]
s=s.replace('Runtime validation remains ongoing.', 'The successful title-screen runtime validation is recorded below.').replace('- Latest full build: 251 distinct diagnostics (272 printed). No client launch yet. Remaining errors are primarily client Fabric registrations, small render/input/skin API changes, and remaining mixins.', '- Intermediate checkpoint: 251 distinct diagnostics (272 printed), before the subsequent build-passing and runtime chunks.')
s+='''
## Runtime completion checkpoint

- Repeated real-client launches caught and fixed the trident receiver signature, registry dependency order, premature skin lookup and premature sheath ItemStack creation. Item default components are now bound during world-data loading in 26.3; the katana renderer retains the Item and creates its stack only when needed in a world.
- Generated native display-context variants for 26 items from the original NeoForge separate_transforms definitions. Preserved the original reference assets unchanged and added native aliases plus select-item definitions. Removed five erroneously generated item definitions for unregistered model templates.
- Optional WaveyCapes integration data is excluded from processed resources, as permitted by TASK.md. It no longer attempts to load an absent compatibility layer.
- Full final build passes; 1,091 resource JSON files decode. Asset inventory still verifies all 944 original non-language assets unchanged in source and output.
- Runtime mixin report: 41 configured, 41 applied, zero missing; MixinEnvironment.audit() completes. Title-screen smoke is stable for 100 ticks, captures a visibly rendered title screen, exits normally, and rejects resource-pack recovery as a failure.
- Commands: `py -3 scripts/build.py build`; `py -3 scripts/smoke_client.py --log client-smoke-final.log`; `py -3 scripts/runtime_mixin_report.py client-smoke-final.log`; `py -3 scripts/asset_inventory.py`.
- No real launcher profile/save was modified, no world was created, and nothing was published. Remaining gameplay verification is listed above.
'''
p.write_bytes(s.encode('utf-8'))
