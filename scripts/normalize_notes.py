from pathlib import Path
p=Path(__file__).resolve().parents[1]/'PORT_NOTES.md'
s=p.read_bytes().decode('utf-8',errors='surrogateescape')
s=''.join(bytes([ord(c)-0xdc00]).decode('cp1252') if 0xdc80<=ord(c)<=0xdcff else c for c in s)
s=s.replace('`n','\n')
p.write_text(s,encoding='utf-8')
