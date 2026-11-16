#!/usr/bin/env python3
"""
beats.py - transforma o roteiro de um Reel numa tabela de tempos antes de gravar.

Estima quanto tempo cada frase leva para ser dita, empilha tudo em minutagem e
aponta as quatro coisas que matam um Reel na edição: um gancho que passa dos
três segundos, um trecho longo o bastante para o espectador sair, uma sequência
de frases sem nada concreto, e uma duração total diferente da que você queria.

Os tempos são uma estimativa pelo número de palavras numa velocidade em
palavras por minuto. Servem para planejar a edição, não substituem gravar.
Ajuste a sua velocidade com --wpm depois de cronometrar você mesmo lendo um
roteiro em voz alta: a maioria das pessoas fica entre 140 e 190, e o padrão
aqui é 155 (português costuma ter palavras mais longas que o inglês).

Uso
  python3 beats.py roteiro.txt
  python3 beats.py roteiro.txt --target 30
  python3 beats.py roteiro.txt --wpm 175 --target 45
  pbpaste | python3 beats.py -
  python3 beats.py roteiro.txt --json
"""

import argparse
import json
import re
import sys
import unicodedata

WORD_RE = re.compile(r"[A-Za-zÀ-ÖØ-öø-ÿ0-9$%'’-]+")
SENT_RE = re.compile(r"[^.!?]+[.!?]*")
CONCRETE_RE = re.compile(r"\$\s?\d|\b\d[\d,.]*\b|(?<!^)\b[A-ZÀ-Ö][a-zà-ÿ]{2,}\b", re.MULTILINE)
STOPWORDS = {
    "o", "a", "os", "as", "um", "uma", "uns", "umas", "e", "ou", "mas", "se",
    "de", "do", "da", "dos", "das", "em", "no", "na", "nos", "nas", "para",
    "pra", "pro", "por", "pelo", "pela", "com", "sem", "que", "isso", "isto",
    "esse", "essa", "este", "esta", "aquilo", "ele", "ela", "eles", "elas",
    "é", "são", "foi", "era", "ser", "está", "estar", "tá", "você", "vc",
    "seu", "sua", "eu", "meu", "minha", "me", "mim", "nós", "nosso", "a gente",
    "te", "lhe", "só", "não", "sim", "já", "tem", "ter", "tinha", "vai",
    "vou", "pode", "como", "mais", "muito", "ao", "à", "aos", "às", "tudo",
    "todo", "toda", "quando", "onde", "lá", "aqui", "então",
}

HOOK_WINDOW = 3.0        # segundos. Depois disso, o dedão já decidiu.
MAX_BEAT = 4.0           # segundos numa ideia sem mudança na tela.
ABSTRACT_RUN = 3         # trechos seguidos sem nada verificável.


def words(text):
    # "R$ 18.000" é uma palavra quando é falado, então é uma palavra aqui também.
    t = re.sub(r"(?<=\d)[.,](?=\d{3})", "", text)
    t = re.sub(r"(R|US)\$\s+(?=\d)", r"\1$", t)
    return WORD_RE.findall(t)


def pretty(token):
    """Devolve o separador de milhar para exibição."""
    token = re.sub(r"^(r|us)\$", lambda m: m.group(1).upper() + "$ ", token)
    return re.sub(r"(\d)(?=(\d{3})+$)", r"\1.", token)


def tc(seconds):
    m, s = divmod(seconds, 60)
    return f"{int(m)}:{s:04.1f}"


def split_beats(raw, wps):
    """Uma linha é um trecho, a não ser que seja longa demais para ser um só."""
    beats = []
    for line in [l.strip() for l in raw.splitlines()]:
        if not line:
            continue
        if len(words(line)) / wps <= MAX_BEAT * 1.5:
            beats.append(line)
            continue
        # Parágrafo longo: quebra no fim das frases para os tempos fazerem
        # sentido.
        parts = [p.strip() for p in SENT_RE.findall(line) if p.strip()]
        buf = ""
        for part in parts:
            candidate = (buf + " " + part).strip()
            if buf and len(words(candidate)) / wps > MAX_BEAT:
                beats.append(buf)
                buf = part
            else:
                buf = candidate
        if buf:
            beats.append(buf)
    return beats


def analyse(raw, wpm=155, target=None):
    wps = wpm / 60.0
    beats = split_beats(raw, wps)
    if not beats:
        return None

    rows, clock = [], 0.0
    for i, text in enumerate(beats):
        n = len(words(text))
        dur = n / wps
        rows.append({
            "n": i + 1,
            "start": round(clock, 2),
            "dur": round(dur, 2),
            "words": n,
            "text": text,
            "concrete": len(CONCRETE_RE.findall(text)),
            "label": "",
            "flags": [],
        })
        clock += dur
    total = clock

    # Marca as posições estruturais em torno das quais um Reel é montado.
    for r in rows:
        if r["n"] == 1 or r["start"] + r["dur"] <= HOOK_WINDOW:
            r["label"] = "GANCHO"
    rows[-1]["label"] = "CTA" if rows[-1]["label"] != "GANCHO" else "GANCHO+CTA"
    half = total / 2
    for r in rows:
        if not r["label"] and r["start"] <= half < r["start"] + r["dur"]:
            r["label"] = "MEIO"

    notes = []
    if rows[0]["dur"] > HOOK_WINDOW:
        rows[0]["flags"].append(f"gancho leva {rows[0]['dur']:.1f}s, passa dos {HOOK_WINDOW:.0f}s")
        notes.append(f"O trecho 1 leva {rows[0]['dur']:.1f}s para ser dito. Corte para "
                     f"{int(HOOK_WINDOW * wps)} palavras ou menos, senão o gancho chega depois "
                     "que a decisão já foi tomada.")
    if rows[0]["concrete"] == 0:
        notes.append("O trecho 1 não tem nenhum número nem nome. Ganchos sem algo "
                     "verificável são os que são pulados.")

    for r in rows:
        if r["dur"] > MAX_BEAT:
            r["flags"].append(f"{r['dur']:.1f}s num trecho só")
    long_beats = [r["n"] for r in rows if r["dur"] > MAX_BEAT]
    if long_beats:
        notes.append(f"Trecho(s) {', '.join(map(str, long_beats))} passam de {MAX_BEAT:.0f}s. "
                     "Divida a frase ou mude o que aparece na tela no meio dela. "
                     "Imagem parada é onde as pessoas saem.")

    run, start = 0, None
    for r in rows:
        if r["concrete"] == 0:
            run += 1
            start = start if start is not None else r["n"]
            if run == ABSTRACT_RUN:
                notes.append(f"Os trechos {start}-{r['n']} não têm nada concreto. "
                             "Coloque um número, um nome ou um preço em um deles.")
        else:
            run, start = 0, None

    # A última frase leva de volta à primeira?
    first = {w.lower() for w in words(rows[0]["text"]) if w.lower() not in STOPWORDS}
    last = {w.lower() for w in words(rows[-1]["text"]) if w.lower() not in STOPWORDS}
    # Também conta como loop a mesma palavra sem acento ("vídeo"/"video").
    strip = lambda w: "".join(c for c in unicodedata.normalize("NFD", w)
                              if unicodedata.category(c) != "Mn")
    last |= {w for w in first if strip(w) in {strip(x) for x in last}}
    loop = sorted(pretty(w) for w in first & last)
    if loop:
        notes.append(f"Tem loop: o último trecho repete \"{', '.join(loop[:3])}\" do gancho. "
                     "Segunda visualização é alcance de graça.")
    else:
        notes.append("Sem loop. O último trecho não repete nenhuma palavra do gancho, então o "
                     "vídeo termina chapado. Repetir uma palavra do trecho 1 é o replay mais barato que existe.")

    if target:
        delta = total - target
        if abs(delta) <= target * 0.1:
            notes.append(f"Duração no alvo ({total:.1f}s para {target:g}s).")
        elif delta > 0:
            notes.append(f"{delta:.1f}s acima do alvo. Corte umas {int(delta * wps)} palavras.")
        else:
            notes.append(f"{-delta:.1f}s abaixo do alvo. Acrescente {int(-delta * wps)} palavras "
                         "ou grave curto mesmo. Curto costuma ser o certo.")

    return {
        "wpm": wpm, "target": target,
        "total_seconds": round(total, 2),
        "total_words": sum(r["words"] for r in rows),
        "beats": rows,
        "notes": notes,
    }


def render(a, out=sys.stdout):
    head = (f"TABELA DE TEMPOS  ·  {a['total_words']} palavras  ·  ~{a['total_seconds']:.1f}s "
            f"a {a['wpm']:g} ppm" + (f"  ·  alvo {a['target']:g}s" if a["target"] else ""))
    print("\n" + head, file=out)
    print("=" * max(len(head), 72), file=out)
    for r in a["beats"]:
        label = f"{r['label']:<11}" if r["label"] else " " * 11
        print(f"  {tc(r['start'])}  {r['dur']:4.1f}s  {label}{r['text']}", file=out)
        for f in r["flags"]:
            print(f"  {'':>6}  {'':>5}  {'':<11}^ {f}", file=out)
    print("-" * max(len(head), 72), file=out)
    for n in a["notes"]:
        print(f"  - {n}", file=out)
    print("", file=out)


def main():
    ap = argparse.ArgumentParser(description="Cronometra o roteiro de um Reel numa tabela de tempos.")
    ap.add_argument("input", nargs="?", default="-", help="arquivo do roteiro, ou - para stdin")
    ap.add_argument("--wpm", type=float, default=155, help="velocidade da fala em palavras por minuto (padrão 155)")
    ap.add_argument("--target", type=float, help="duração alvo em segundos")
    ap.add_argument("--json", action="store_true")
    args = ap.parse_args()

    raw = sys.stdin.read() if args.input == "-" else open(args.input, encoding="utf-8").read()
    a = analyse(raw, wpm=args.wpm, target=args.target)
    if not a:
        print("roteiro vazio", file=sys.stderr)
        sys.exit(2)
    if args.json:
        print(json.dumps(a, indent=2, ensure_ascii=False))
    else:
        render(a)
    sys.exit(0)


if __name__ == "__main__":
    main()
