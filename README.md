# OpenFight

**OpenFight** is a Fabric port of [Epic Fight](https://github.com/Epic-Fight/epicfight/) for Minecraft 26.3. It brings Epic Fight's combat, skills and animations to Fabric while keeping the `epicfight` resource and registry namespace, so existing datapacks, resource packs and worlds keep working with their IDs.

<p align="center">
  <a href="https://github.com/KaianGuedes/OpenFight">
    <img src="https://img.shields.io/badge/Minecraft-26.3-62b47a?style=flat-square" alt="Minecraft 26.3">
  </a>
  <a href="https://fabricmc.net/">
    <img src="https://img.shields.io/badge/Loader-Fabric-dbd0b4?style=flat-square" alt="Fabric">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-GPL--3.0--or--later-4183c4?style=flat-square" alt="License GPL-3.0-or-later">
  </a>
</p>

> OpenFight is an independent project and is not affiliated with or endorsed by the Epic Fight team.

---

## 📦 Assets
The repository contains **no** Epic Fight All Rights Reserved assets. At build time, Gradle assembles assets from the pinned `upstream/` submodule and applies the small `overlay/assets/` set of port additions and translation changes.

Do not commit or redistribute Epic Fight assets. Public forks must replace all original assets with original work.

## 🔧 Building
1. Install JDK 25.
2. Clone the repository with its pinned asset source:
   ```sh
   git clone --recursive https://github.com/KaianGuedes/OpenFight.git
   cd OpenFight
   ./gradlew build
   ```
   On Windows, run `gradlew.bat build`.
3. Gradle writes the development jar under `build/libs/`.

No jar is published by this project; build it locally from source.

## 📢 Planned / Coming Soon
- Model preview in the datapack editor screens.
- GPU (compute shader) skinning, if Minecraft 26.3 exposes a compute API.
- Open replacement assets, so a ready-to-use jar can be published.

See [PROGRESS.md](PROGRESS.md) for the full status and known limitations.

## 🤝 Need Help or Want to Contribute?
Found a bug? Have a suggestion? Want to help improve it?
You can:
- Open [issues](https://github.com/KaianGuedes/OpenFight/issues) for bugs, ideas, or feature requests.
- Submit [pull requests](https://github.com/KaianGuedes/OpenFight/pulls) to improve the code, docs, or features.

> ⭐ Star the repo to support the project!

## 🛠️ Developer Info
Built with:
- Java 25
- Gradle with Fabric Loom
- Fabric Loader and Fabric API for Minecraft 26.3

## 🙏 Credits and Fork Terms
Epic Fight is created by [Yesman](https://github.com/Yesssssman) and its contributors. OpenFight is based on Epic Fight source code and credits the original project at [github.com/Epic-Fight/epicfight](https://github.com/Epic-Fight/epicfight/).

Epic Fight's README permits third-party redistribution of code forks when:
- the original assets are removed and replaced with original assets,
- the fork uses a distinct name,
- attribution and a project link are provided, and
- the Epic Fight project receives a non-exclusive, worldwide, royalty-free license to use, copy, modify, and distribute the fork's modifications with appropriate attribution.

The upstream Epic Fight project and its contributors are granted that additional fork license for the modifications made in this repository.

## 🤖 AI Assistance
AI coding tools assisted with porting, rebranding, and repository maintenance. Human review and testing remain ongoing.

## 📃 License
OpenFight's code is open source under the [GNU GPL-3.0-or-later](LICENSE).
