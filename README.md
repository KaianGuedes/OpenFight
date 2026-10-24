# OpenFight

OpenFight is a Fabric port of [Epic Fight](https://github.com/Epic-Fight/epicfight/) for Minecraft 26.3. It preserves Epic Fight's `epicfight` resource and registry namespace so existing datapacks and resource packs can continue to use their IDs. OpenFight is an independent project and is not affiliated with or endorsed by the Epic Fight team.

The repository contains no Epic Fight All Rights Reserved assets. At build time, Gradle assembles assets from the pinned `upstream/` submodule and applies the small `overlay/assets/` set of port additions and translation changes. Do not commit or redistribute Epic Fight assets; public forks must replace all original assets with original work.

## Build

Install JDK 25, then clone the repository with its pinned asset source:

```sh
git clone --recursive https://github.com/KaianGuedes/OpenFight.git
cd OpenFight
./gradlew build
```

On Windows, run `gradlew.bat build`. Gradle writes the development jar under `build/libs/`. No jar is published by this project; build it locally from source.

## Credits and fork terms

Epic Fight is created by [Yesman](https://github.com/Yesssssman) and its contributors. OpenFight is based on Epic Fight source code and credits the original project at [github.com/Epic-Fight/epicfight](https://github.com/Epic-Fight/epicfight/). Epic Fight's README permits third-party redistribution of code forks when the original assets are removed and replaced with original assets, the fork uses a distinct name, attribution and a project link are provided, and the Epic Fight project receives a non-exclusive, worldwide, royalty-free license to use, copy, modify, and distribute the fork's modifications with appropriate attribution.

OpenFight's code is licensed under [GNU GPL-3.0-or-later](LICENSE). The upstream Epic Fight project and its contributors are granted the additional fork license described above for modifications made in this repository.

## AI assistance

AI coding tools assisted with porting, rebranding, and repository maintenance. Human review and testing remain ongoing.
