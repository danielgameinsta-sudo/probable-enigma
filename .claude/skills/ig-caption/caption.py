#!/usr/bin/env python3
"""
caption.py - confere uma legenda do Instagram e mostra exatamente o que o feed
mostra antes do "... mais".

O Instagram mostra uns 125 caracteres da legenda no feed e esconde o resto
atrás de um toque. Quase toda legenda que falha, falha ali: o gancho está na
terceira frase, a primeira linha é um cumprimento, ou tudo começa com uma
hashtag. Isto mostra a parte visível numa caixa, para você ler do jeito que um
desconhecido rolando o feed lê, e depois confere as oito coisas que valem
conferir.

O corte em 125 caracteres é aproximado. O número real muda com o aparelho, o
tamanho da fonte e onde caem as quebras de linha, e é por isso que você quer
uma margem em vez de uma legenda calculada para terminar no 125. Mude com
--truncate se quiser testar um corte mais apertado.

Uso
  python3 caption.py legenda.txt
  python3 caption.py legenda.txt --keywords "contrato de cliente,precificação"
  pbpaste | python3 caption.py -
  python3 caption.py legenda.txt --json
"""

import argparse
import json
import re
import sys
import textwrap

LIMIT = 2200             # Limite máximo de uma legenda no Instagram.
TRUNCATE = 125           # Mais ou menos onde o feed corta para "... mais".
HASHTAG_LIMIT = 5        # Limite do Instagram por post ou reel desde 18/12/2025,
                         # antes eram 30. Anunciado pela conta @Creators:
                         # usar menos hashtags (até 5), mais específicas, em vez
                         # de muitas genéricas, melhora o desempenho do conteúdo
                         # e a experiência das pessoas.

HASHTAG_RE = re.compile(r"(?:^|\s)(#[A-Za-zÀ-ÖØ-öø-ÿ0-9_]+)")
MENTION_RE = re.compile(r"(?:^|\s)(@[A-Za-z0-9_.]+)")
LINK_RE = re.compile(r"https?://\S+|\bwww\.\S+|\b[a-z0-9-]+\.(?:com|com\.br|br|co|io|net|org|ai|app)/\S*",
                     re.IGNORECASE)
EMOJI_RE = re.compile(r"[\U0001F300-\U0001FAFF☀-➿←-⇿️]")
CONCRETE_RE = re.compile(r"\$\s?\d|\b\d[\d,.]*\b|(?<!^)\b[A-ZÀ-Ö][a-zà-ÿ]{2,}\b", re.MULTILINE)

ASKS = [
    (re.compile(r"\b(?:[Cc]omente|[Cc]omenta|[Ee]screva|[Dd]igite)\s+(?:a palavra\s+|\"|“)?[A-ZÀ-Ö0-9]{2,}\b"), "comentar uma palavra-chave"),
    (re.compile(r"(?i)\b(?:me (?:manda|mande|chama|chame)|manda (?:uma )?dm|mande (?:uma )?dm|chama na dm|no direct|no direct)\b"), "mandar DM"),
    (re.compile(r"(?i)\b(?:salva|salve|salvar) (?:esse|este|isso|esse post|pra|para)\b"), "salvar"),
    (re.compile(r"(?i)\b(?:compartilh[ae]|manda (?:pra|para)|mande (?:pra|para)|envi[ae] (?:pra|para))\b"), "compartilhar"),
    (re.compile(r"(?i)\b(?:me segue|me siga|siga (?:o|a|para|pra)|segue (?:o|a|para|pra))\b"), "seguir"),
    (re.compile(r"(?i)\blink na (?:minha )?bio\b"), "link na bio"),
    (re.compile(r"(?i)\b(?:arrasta|arraste|desliza|deslize|toca|toque) (?:pro|para o|pra|para|aqui)\b"), "arrastar ou tocar"),
    (re.compile(r"(?i)\bme (?:conta|diz|fala)\b|\bo que voc[eê] (?:faria|acha)\b|\bqual (?:dos|das|voc[eê])\b"), "responder uma pergunta"),
]

FILLER_TAGS = {"#viral", "#fyp", "#explore", "#explorepage", "#foryou", "#foryoupage",
               "#trending", "#instagood", "#love", "#follow", "#like4like", "#reels",
               "#reelsinstagram", "#viralreels", "#instadaily", "#viralizar",
               "#explorar", "#brasil", "#reelsbrasil", "#instabrasil", "#sigam",
               "#curtir", "#tbt", "#bomdia", "#foco", "#sucesso", "#motivacao",
               "#motivação", "#empreendedorismo"}


def visible_window(text, cut):
    """O que o feed mostra. O Instagram corta no meio da palavra, então isto também."""
    flat = text.strip()
    return flat if len(flat) <= cut else flat[:cut]


def render_box(window, truncated, out=sys.stdout, width=52):
    print("\n  O QUE O FEED MOSTRA", file=out)
    print("  +" + "-" * (width + 2) + "+", file=out)
    lines = []
    for raw in window.split("\n"):
        lines.extend(textwrap.wrap(raw, width) or [""])
    for line in lines[:8]:
        print(f"  | {line:<{width}} |", file=out)
    tail = "... mais" if truncated else "(a legenda inteira cabe)"
    print("  +" + "-" * (width + 2 - len(tail) - 2) + f" {tail} " + "+", file=out)


def analyse(text, cut=TRUNCATE, keywords=None):
    text = text.rstrip()
    stripped = text.strip()
    chars = len(stripped)
    lines = [l for l in stripped.split("\n")]
    first_line = lines[0].strip() if lines else ""
    tags = HASHTAG_RE.findall(stripped)
    mentions = MENTION_RE.findall(stripped)
    links = LINK_RE.findall(stripped)
    emoji = EMOJI_RE.findall(stripped)
    window = visible_window(stripped, cut)
    truncated = chars > cut
    asks = [name for pattern, name in ASKS if pattern.search(stripped)]
    filler = [t for t in tags if t.lower() in FILLER_TAGS]
    keywords = [k.strip() for k in (keywords or []) if k.strip()]

    checks = []

    def add(name, status, detail):
        checks.append({"check": name, "status": status, "detail": detail})

    add("TAMANHO", "FAIL" if chars > LIMIT else "PASS",
        f"{chars} / {LIMIT} caracteres" + (f", {chars - LIMIT} acima do limite"
                                           if chars > LIMIT else ""))

    if not first_line:
        add("1ª LINHA", "FAIL", "a legenda começa com uma linha em branco")
    elif first_line.startswith("#") or first_line.startswith("@"):
        add("1ª LINHA", "FAIL",
            "começa com hashtag ou menção, na única posição que merece uma frase")
    elif len(first_line) > cut:
        add("1ª LINHA", "WARN",
            f"{len(first_line)} caracteres, então é cortada no {cut} no meio da ideia. "
            "Tudo bem se o corte gera suspense, ruim se corta uma frase explicativa")
    else:
        add("1ª LINHA", "PASS", f"{len(first_line)} caracteres, aparece inteira")

    add("GANCHO CONCRETO", "PASS" if CONCRETE_RE.search(window) else "WARN",
        f"{len(CONCRETE_RE.findall(window))} número(s) ou nome(s) na parte visível"
        + ("" if CONCRETE_RE.search(window) else " - nada verificável antes do toque"))

    if len(tags) > HASHTAG_LIMIT:
        add("HASHTAGS", "FAIL", f"{len(tags)} hashtags, acima do limite de {HASHTAG_LIMIT} do Instagram. "
                                "As que passam da quinta não contam e o bloco parece ultrapassado")
    elif len(tags) == HASHTAG_LIMIT and filler:
        add("HASHTAGS", "WARN", f"{len(tags)} hashtags, no limite, e "
                                f"{len(filler)} delas genéricas. Use as cinco em temas")
    elif filler:
        add("HASHTAGS", "WARN", f"{len(tags)} hashtags, {len(filler)} delas genéricas "
                                f"({', '.join(filler[:3])}). Essas não descrevem nada")
    else:
        add("HASHTAGS", "PASS", f"{len(tags)} hashtag(s)" + (f": {' '.join(tags)}" if tags else ""))

    if not tags:
        add("POSIÇÃO TAGS", "PASS", "nenhuma hashtag para posicionar")
    elif any(re.search(r"(?:^|\s)" + re.escape(t) + r"\b", window) for t in tags):
        add("POSIÇÃO TAGS", "WARN", "tem hashtag na parte visível, "
                                     "gastando espaço do feed com um rótulo")
    else:
        add("POSIÇÃO TAGS", "PASS", "hashtags ficam depois do corte")

    add("LINKS", "WARN" if links else "PASS",
        f"{len(links)} link(s) na legenda, e legenda não é clicável. "
        f"Leve para a bio ou para a DM" if links else "nenhum link morto no texto")

    if len(asks) == 1:
        add("UM PEDIDO", "PASS", f"uma chamada para ação: {asks[0]}")
    elif not asks:
        add("UM PEDIDO", "WARN", "nenhuma chamada para ação. Decida para que serve este post")
    else:
        add("UM PEDIDO", "WARN", f"{len(asks)} pedidos ({', '.join(asks)}). "
                               "Dois pedidos é o mesmo que nenhum")

    density = len(emoji) * 100 / max(chars, 1)
    add("EMOJI", "WARN" if density > 4 else "PASS",
        f"{len(emoji)} emoji, {density:.1f} a cada 100 caracteres"
        + (" - parece enfeite" if density > 4 else ""))

    if keywords:
        low = stripped.lower()
        found = [k for k in keywords if k.lower() in low]
        missing = [k for k in keywords if k.lower() not in low]
        in_window = [k for k in found if k.lower() in window.lower()]
        status = "PASS" if not missing else ("WARN" if found else "FAIL")
        add("TERMOS BUSCA", status,
            f"{len(found)}/{len(keywords)} presentes"
            + (f", {len(in_window)} na parte visível" if found else "")
            + (f". Faltando: {', '.join(missing)}" if missing else ""))

    fails = sum(1 for c in checks if c["status"] == "FAIL")
    warns = sum(1 for c in checks if c["status"] == "WARN")
    verdict = "CORRIGIR" if fails else ("REVISAR" if warns else "PRONTA")

    return {
        "characters": chars, "limit": LIMIT, "truncate_at": cut,
        "visible": window, "truncated": truncated,
        "first_line_chars": len(first_line),
        "hashtags": tags, "mentions": mentions, "links": links,
        "emoji": len(emoji), "asks": asks,
        "checks": checks, "verdict": verdict,
    }


def render(a, out=sys.stdout):
    head = (f"CONFERÊNCIA DA LEGENDA  ·  {a['characters']} / {a['limit']} caracteres  ·  "
            f"{len(a['hashtags'])} hashtags  ·  {len(a['asks'])} pedido(s)")
    print("\n" + head, file=out)
    print("=" * max(len(head), 62), file=out)
    render_box(a["visible"], a["truncated"], out=out)
    print("", file=out)
    for c in a["checks"]:
        st = {"PASS": "OK", "WARN": "ATENÇÃO", "FAIL": "FALHA"}[c["status"]]
        print(f"  {st:<8} {c['check']:<16} {c['detail']}", file=out)
    print("-" * max(len(head), 62), file=out)
    print(f"  VEREDITO  {a['verdict']}\n", file=out)


def main():
    ap = argparse.ArgumentParser(description="Confere uma legenda do Instagram.")
    ap.add_argument("input", nargs="?", default="-", help="arquivo da legenda, ou - para stdin")
    ap.add_argument("--truncate", type=int, default=TRUNCATE,
                    help=f"caracteres mostrados antes do '... mais' (padrão {TRUNCATE})")
    ap.add_argument("--keywords", default="", help="termos separados por vírgula pelos quais você quer ser encontrado")
    ap.add_argument("--json", action="store_true")
    args = ap.parse_args()

    raw = sys.stdin.read() if args.input == "-" else open(args.input, encoding="utf-8").read()
    a = analyse(raw, cut=args.truncate, keywords=args.keywords.split(","))
    if args.json:
        print(json.dumps(a, indent=2, ensure_ascii=False))
    else:
        render(a)
    sys.exit(0 if a["verdict"] == "PRONTA" else 1)


if __name__ == "__main__":
    main()
