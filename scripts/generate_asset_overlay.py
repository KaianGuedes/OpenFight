#!/usr/bin/env python3
"""Copy only port-owned asset additions and translation changes into overlay/assets."""

from __future__ import annotations

import argparse
import hashlib
import json
import shutil
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("port_assets", type=Path)
    parser.add_argument("upstream_assets", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()

    shutil.rmtree(args.output, ignore_errors=True)
    args.output.mkdir(parents=True, exist_ok=True)
    upstream_hashes = {
        hashlib.sha256(path.read_bytes()).digest()
        for path in args.upstream_assets.rglob("*")
        if path.is_file()
    }
    for source in sorted(args.port_assets.rglob("*")):
        if not source.is_file():
            continue
        relative = source.relative_to(args.port_assets)
        baseline = args.upstream_assets / relative
        if relative.parent.name == "lang" and source.suffix == ".json":
            port_values = json.loads(source.read_text(encoding="utf-8-sig"))
            base_values = json.loads(baseline.read_text(encoding="utf-8-sig")) if baseline.exists() else {}
            changes = {key: value for key, value in port_values.items() if key not in base_values or base_values[key] != value}
            changes = {
                key: value.replace("Epic Fight", "OpenFight").replace("EpicFight", "OpenFight") if isinstance(value, str) else value
                for key, value in changes.items()
            }
            if changes:
                destination = args.output / relative
                destination.parent.mkdir(parents=True, exist_ok=True)
                destination.write_text(json.dumps(changes, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        elif not baseline.exists() or source.read_bytes() != baseline.read_bytes():
            if hashlib.sha256(source.read_bytes()).digest() in upstream_hashes:
                continue
            destination = args.output / relative
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, destination)


if __name__ == "__main__":
    main()
