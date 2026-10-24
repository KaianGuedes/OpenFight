"""Capture Gradle diagnostics without Windows PowerShell's native stderr reformatting."""
import os
from pathlib import Path
import subprocess
import sys

root = Path(__file__).resolve().parents[1]
env = dict(os.environ, JAVA_HOME=str(root.parent/'tools/jdk-25.0.4.1+1'))
name = os.environ.get('PORT_BUILD_LOG', 'latest-build.log')
with (root/name).open('wb') as f:
    result = subprocess.run([str(root/'gradlew.bat'), '--no-daemon', '--console=plain', *sys.argv[1:]],cwd=root,env=env,stdout=f,stderr=subprocess.STDOUT)
print((root/name).read_text(encoding='utf-8',errors='replace')[-2200:])
sys.exit(result.returncode)
