# Epic Fight personal-use Fabric 26.3 port — incomplete

This is an unfinished source port. It does not yet compile into a usable mod. See [PORT_NOTES.md](PORT_NOTES.md) for implemented work, validation, behavior adaptations and the remaining work. Do not publish this tree or its assets.

From this directory, run `py -3 scripts/build.py build` to compile with the supplied Java 25 installation and save diagnostics in latest-build.log. Direct PowerShell build instructions are in PORT_NOTES.md.

The full build compiles combat, skills, animation, renderer and GUI sources. Only nested optional compatibility modules, the top-level NeoForge platform directory and unused recipe/tag data generators are excluded. The old client platform bridges are still in the compile backlog. Passing the separately named component verification tasks does not mean the full mod is playable.

Reports: [COMPILE_BACKLOG.md](COMPILE_BACKLOG.md), [MIXIN_AUDIT.txt](MIXIN_AUDIT.txt), [ACCESS_MIGRATION.txt](ACCESS_MIGRATION.txt), [ASSET_INVENTORY.json](ASSET_INVENTORY.json).
