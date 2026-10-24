"""Small helpers for regex-assisted Java source rewriting."""
import re

def split_args(s):
    """split top-level comma separated args of s (without outer parens)."""
    args=[];depth=0;cur=[];i=0;instr=None
    while i<len(s):
        ch=s[i]
        if instr:
            cur.append(ch)
            if ch=='\\': cur.append(s[i+1]);i+=1
            elif ch==instr: instr=None
        elif ch in '"\'':
            instr=ch;cur.append(ch)
        elif ch in '([{<' and not (ch=='<' and not _generic_open(s,i)):
            depth+=1;cur.append(ch)
        elif ch in ')]}' or (ch=='>' and depth>0 and _in_generic(cur)):
            depth-=1;cur.append(ch)
        elif ch==',' and depth==0:
            args.append(''.join(cur).strip());cur=[]
        else: cur.append(ch)
        i+=1
    if ''.join(cur).strip() or args: args.append(''.join(cur).strip())
    return args

def _generic_open(s,i):
    # treat '<' as generic opener only if preceded by identifier char and followed by uppercase/identifier and a matching '>' soon
    if i==0 or not (s[i-1].isalnum() or s[i-1]=='_'): return False
    j=i+1
    while j<len(s) and (s[j].isalnum() or s[j] in '_?, .<>[]&'):
        if s[j]=='>': return True
        j+=1
    return False

def _in_generic(cur):
    txt=''.join(cur)
    return txt.count('<')>txt.count('>')

def find_close(s,i):
    """s[i]=='(' -> index of matching ')'"""
    depth=0;instr=None
    j=i
    while j<len(s):
        ch=s[j]
        if instr:
            if ch=='\\': j+=1
            elif ch==instr: instr=None
        elif ch in '"\'': instr=ch
        elif ch=='(': depth+=1
        elif ch==')':
            depth-=1
            if depth==0: return j
        j+=1
    return -1

def rewrite_calls(text, pattern, fn):
    """pattern: regex matching up to and including the opening '(' (must end with \\()).
    fn(args:list[str], m) -> replacement string for the whole call (without trailing ')') or None to skip."""
    out=[];pos=0
    rx=re.compile(pattern)
    while True:
        m=rx.search(text,pos)
        if not m: break
        op=m.end()-1
        cl=find_close(text,op)
        if cl<0: break
        args=split_args(text[op+1:cl])
        rep=fn(args,m)
        if rep is None:
            out.append(text[pos:m.end()]);pos=m.end();continue
        out.append(text[pos:m.start()]);out.append(rep+')');pos=cl+1
    out.append(text[pos:])
    return ''.join(out)

def add_import(text, imp):
    line=f'import {imp};'
    if line in text: return text
    m=re.search(r'^import .*;$',text,re.M)
    if m: return text[:m.start()]+line+'\n'+text[m.start():]
    m=re.search(r'^package .*;$',text,re.M)
    return text[:m.end()]+'\n\n'+line+text[m.end():]

def body_start(text, sig_end):
    """index just after the '{' that opens a method body starting search at sig_end"""
    return text.index('{',sig_end)+1

def expr_before(s, idx):
    """start index of the primary expression (identifier/call/index chain) that ends right before s[idx] (a '.' position)."""
    j=idx
    while j>0:
        ch=s[j-1]
        if ch in ')]':
            op='(' if ch==')' else '['
            depth=0;k=j-1
            while k>=0:
                if s[k] in ')]': depth+=1
                elif s[k] in '([':
                    depth-=1
                    if depth==0: break
                k-=1
            j=k
        elif ch.isalnum() or ch in '_.$':
            j-=1
        else: break
    return j
