#!/usr/bin/env python3
"""
hookscore.py - dá nota à primeira frase de um Reel nas cinco coisas que os
ganchos fortes têm em comum, e ranqueia um lote de ganchos entre si.

O que isto é:  cinco heurísticas locais, calculadas na sua máquina só a partir
do texto. Elas medem propriedades que os ganchos que seguram a atenção costumam
ter - um tamanho que dá para falar em menos de três segundos, um marcador
concreto, algo em jogo, o conteúdo principal na frente e não no fim, e um
espectador para quem a frase é dirigida.

O que isto NÃO é:  um previsor de views. A versão original (em inglês) separava
bem um gancho real de um gancho propositalmente ruim (AUC 0,83), mas mal
separava os acertos de um bom criador dos erros desse mesmo criador (AUC 0,56,
onde 0,50 é cara ou coroa). Esta versão foi adaptada para o português com as
mesmas regras, sem uma nova validação.

Então use para o que ela mede bem. Ela pega cumprimentos, enrolação, ganchos
sem nada concreto e ganchos que levam cinco segundos para serem ditos. Ela não
diz qual de dois ganchos decentes vai viralizar, e nada que só lê texto
consegue, porque isso depende do seu rosto, da edição, do áudio e de para quem
o Instagram mostra o vídeo. Confie mais no gráfico de retenção do que neste
script.

Cada verificação vai de 0 a 100. Quanto maior, melhor.

Uso
  python3 hookscore.py ganchos.txt               # um gancho por linha, ranqueados
  python3 hookscore.py --hook "Perdi R$ 18 mil por causa de um contrato."
  pbpaste | python3 hookscore.py -
  python3 hookscore.py ganchos.txt --json
"""

import argparse
import json
import re
import statistics
import sys
import unicodedata

LETRAS = "A-Za-zÀ-ÖØ-öø-ÿ"
WORD_RE = re.compile(rf"[{LETRAS}0-9$%'’-]+")
NUMBER_RE = re.compile(
    r"R\$\s?\d[\d.]*(?:,\d+)?"                          # dinheiro em reais
    r"|US\$\s?\d[\d.]*(?:,\d+)?|\$\s?\d[\d.,]*"         # dólares
    r"|\b\d[\d.]*(?:,\d+)?\s?"                          # um número, com ou
    r"(?:%|k\b|mil\b|mi\b|x\b|h\b|horas?\b|min\b|minutos?\b"  # sem unidade
    r"|dias?\b|semanas?\b|m[eê]s\b|meses\b|anos?\b|reais\b)?",
    re.IGNORECASE)
PROPER_RE = re.compile(r"(?<!^)\b[A-ZÀ-Ö][a-zà-ÿ]{2,}\b")
HASHTAG_RE = re.compile(r"(?:^|\s)#\w+")
EMOJI_RE = re.compile(r"[\U0001F300-\U0001FAFF☀-➿]")


def sem_acento(s):
    return "".join(c for c in unicodedata.normalize("NFD", s)
                   if unicodedata.category(c) != "Mn")


def norm(w):
    return sem_acento(w.lower().strip("'’"))


# Ganchos falados dizem os números em voz alta. "Zero reais" e "vinte mil" são
# tão concretos quanto "R$ 0" e "R$ 20.000". "Um" e "primeiro" ficaram de fora
# de propósito: são enchimento muito mais vezes do que quantidade.
SPOKEN_NUMBERS = {norm(w) for w in {
    "zero", "dois", "duas", "três", "quatro", "cinco", "seis", "sete", "oito",
    "nove", "dez", "onze", "doze", "quinze", "vinte", "trinta", "quarenta",
    "cinquenta", "sessenta", "setenta", "oitenta", "noventa", "cem", "cento",
    "duzentos", "trezentos", "quinhentos", "mil", "milhão", "milhões",
    "bilhão", "bilhões", "dúzia", "metade", "meio", "dobro", "triplo",
}}
MONEY_WORDS = {norm(w) for w in {
    "reais", "real", "dólares", "dólar", "conto", "contos", "pila", "pilas",
    "centavos", "porcento", "milionário", "milionária", "bilionário",
    "faturamento", "faturei", "faturou", "lucro", "salário", "aluguel",
    "dívida", "dívidas", "prejuízo",
}}

# Palavras que colocam algo em jogo. Um gancho sem nenhuma delas é uma
# afirmação; um gancho com uma delas é um motivo para continuar assistindo.
STAKES = {norm(w) for w in {
    "pare", "parei", "nunca", "jamais", "errado", "errada", "erro",
    "erros", "errei", "perdi", "perder", "perdendo", "perdeu", "custou",
    "custa", "custo", "quebrei", "quebrado", "falido", "faliu", "falhei",
    "falha", "fracasso", "fracassei", "ninguém", "não", "nem", "nada",
    "larguei", "desisti", "demitido", "demitida", "apaguei", "apague",
    "matou", "mata", "substituiu", "substitui", "cortei", "corte", "venci",
    "grátis", "de graça", "paguei", "cobrei", "cobrado", "contratei",
    "economizei", "primeiro", "primeira",
    "proibido", "proibida", "ilegal", "pior", "piores", "odeio", "odiei",
    "desperdicei", "desperdício", "golpe", "mentira", "mentiram", "menti",
    "verdade", "segredo", "escondido", "escondem", "roubou", "roubado",
    "antes", "até", "em vez", "mas", "porém", "exceto", "só", "somente",
    "apenas", "problema", "risco", "perigo", "cuidado", "atenção",
    "arrependo", "arrependimento", "queria", "deveria", "devia", "ainda",
    "já", "sem", "versus", "vs", "realmente", "na verdade",
}}

# Aberturas que gastam o primeiro segundo sem dizer nada.
WEAK_OPENERS = [sem_acento(w) for w in [
    "então", "entao", "ok", "oi", "olá", "ola", "e aí", "e ai", "fala",
    "galera", "gente", "pessoal", "bom dia", "boa tarde", "boa noite",
    "bem-vindo", "bem-vinda", "bem vindo", "hoje", "basicamente",
    "sinceramente", "olha", "escuta", "tipo", "é", "bom", "bem", "vamos",
    "vem", "eu queria", "eu quero", "eu vou", "um dos", "uma das", "você já",
    "voce ja", "vocês", "voces", "neste", "nesse", "no vídeo", "no video",
    "a coisa", "muita gente", "existe", "existem", "tem", "isso é", "isto é",
    "esse é", "como um", "como uma", "quando se", "se você já", "sabe",
    "você sabia", "voce sabia",
]]

# Imperativos que merecem a primeira posição.
IMPERATIVES = {norm(w) for w in {
    "pare", "roube", "copie", "apague", "delete", "tente", "assista",
    "veja", "leia", "salve", "use", "construa", "faça", "faca", "escreva",
    "mande", "envie", "pegue", "comece", "largue", "nunca", "sempre", "não",
    "coloque", "rode", "confira", "teste", "esqueça", "esqueca", "aprenda",
}}

DEALBREAKERS = [
    (re.compile(r"(?i)^\s*(?:para de rolar|pare de rolar|n[aã]o (?:role|rola|pule|passa))"),
     "Abre com \"pare de rolar\". Pedir atenção prova que você ainda não a conquistou."),
    (re.compile(r"(?i)\b(?:n(?:est|ess)e v[ií]deo|no v[ií]deo de hoje|no reel de hoje|"
                r"vou (?:te )?mostrar|vou ensinar|hoje eu vou)\b"),
     "Enrolação de vídeo. Apague isso e abra direto no resultado."),
    (re.compile(r"(?i)^\s*(?:oi\b|ol[aá]\b|e a[ií]\b|fala\b|salve\b|bom dia|boa tarde|"
                r"boa noite|bem[- ]vind[oa]s?|galera|pessoal)"),
     "Cumprimento. Ninguém abriu o feed para ser cumprimentado."),
    (HASHTAG_RE,
     "Hashtag no gancho. Hashtags ficam no fim da legenda, se é que ficam em algum lugar."),
    (EMOJI_RE,
     "Emoji no gancho. O texto na tela em tamanho de gancho tem espaço para palavras ou para um emoji, não os dois."),
]


def clamp(n):
    return max(0.0, min(100.0, n))


def words(text):
    # "R$ 18.000" é uma palavra quando é falado, então é uma palavra aqui também.
    t = re.sub(r"(?<=\d)[.,](?=\d{3})", "", text)
    t = re.sub(r"(R|US)\$\s+(?=\d)", r"\1$", t)
    return WORD_RE.findall(t)


def check_length(text):
    """O gancho tem que chegar antes do dedão mexer. Uns dois segundos."""
    w = words(text)
    n = len(w)
    secs = n / 2.6                       # ~155 palavras por minuto, falado
    chars = len(text.strip())
    if 5 <= n <= 12:
        score = 100.0
    elif n < 5:
        score = clamp(100 - (5 - n) * 20)
    else:
        score = clamp(100 - (n - 12) * 11)
    if chars > 65:                       # duas linhas de texto grande na tela
        score -= 12
    return clamp(score), f"{n} palavras, {chars} caracteres, ~{secs:.1f}s falado (ideal: 5-12 palavras)"


def check_specificity(text):
    """Uma coisa concreta vale mais que três abstratas."""
    nums = [n.strip() for n in NUMBER_RE.findall(text) if n.strip()]
    propers = set(PROPER_RE.findall(text))
    low = [norm(w) for w in words(text)]
    spoken = [w for w in low if w in SPOKEN_NUMBERS or w in MONEY_WORDS]
    hits = len(nums) + len(propers) + len(spoken)
    score = 15.0 if hits == 0 else clamp(45 + hits * 30)
    found = ", ".join(nums[:2] + sorted(propers)[:2] + spoken[:2])
    return score, (f"{hits} marcador(es) concreto(s)" + (f": {found}" if found else
                   " - nenhum número, nenhum nome, nada verificável"))


def _stake_hits(text):
    low = [norm(x) for x in words(text)]
    hits = {x for x in low if x in STAKES}
    joined = " ".join(low)
    for multi in ("de graca", "em vez", "na verdade"):
        if multi in joined:
            hits.add(multi)
    return hits


def check_stakes(text):
    """Tensão, custo, negação. Algo que o espectador pode perder."""
    markers = sorted(_stake_hits(text))
    if re.search(r"(?:R\$|US\$|\$)\s?\d", text):
        markers.append("um valor em dinheiro")
    n = len(markers)
    score = {0: 20.0, 1: 70.0}.get(n, 100.0)
    detail = f"{n} marcador(es) de tensão" + (f": {', '.join(markers[:4])}" if markers else
                                              " - nada está em jogo nesta frase")
    return clamp(score), detail


def check_frontload(text):
    """A palavra interessante não pode estar na posição nove."""
    w = words(text)
    if not w:
        return 0.0, "vazio"
    low = [norm(x) for x in w]
    opener = " ".join(low[:3])
    penalty = 0
    hit_opener = None
    for weak in WEAK_OPENERS:
        if opener == weak or opener.startswith(weak + " ") or low[0] == weak:
            penalty, hit_opener = 30, weak
            break
    payload = None
    for i, token in enumerate(low):
        if (token in STAKES or token in SPOKEN_NUMBERS or token in MONEY_WORDS
                or NUMBER_RE.match(w[i]) or (i and PROPER_RE.match(w[i]))):
            payload = i
            break
    if payload is None:
        base = 30.0
        where = "nenhuma palavra de impacto na frase"
    elif payload <= 3:
        base = 100.0
        where = f"impacto na palavra {payload + 1}"
    elif payload <= 6:
        base = 70.0
        where = f"impacto na palavra {payload + 1}, dá para trazer para frente"
    else:
        base = 40.0
        where = f"impacto na palavra {payload + 1}, tarde demais"
    detail = where + (f"; abertura fraca \"{hit_opener}\"" if hit_opener else "")
    return clamp(base - penalty), detail


def check_address(text):
    """Dirigido a um espectador, ou solto no ar."""
    low = sem_acento(text.lower())
    w = [norm(x) for x in words(text)]
    if re.search(r"\b(voce|vc|seu|sua|seus|suas|te|ti|contigo|tu|teu|tua)\b", low):
        return 100.0, "fala com o espectador"
    if w and w[0] in IMPERATIVES:
        return 90.0, f"abre com imperativo (\"{w[0]}\")"
    if re.search(r"\b(eu|meu|minha|meus|minhas|me|mim|comigo|nos|nosso|nossa|a gente)\b", low):
        return 70.0, "primeira pessoa, sem falar com o espectador"
    return 35.0, "terceira pessoa, ninguém na sala"


CHECKS = ["TAMANHO", "CONCRETUDE", "TENSAO", "IMPACTO", "DIRECAO"]


def run(text):
    results = {
        "TAMANHO": check_length(text),
        "CONCRETUDE": check_specificity(text),
        "TENSAO": check_stakes(text),
        "IMPACTO": check_frontload(text),
        "DIRECAO": check_address(text),
    }
    flags = [msg for pattern, msg in DEALBREAKERS if pattern.search(text)]
    scores = [results[c][0] for c in CHECKS]
    # A propriedade mais fraca limita o gancho, mesma lógica do detect.py:
    # uma propriedade ruim basta para o dedão continuar rolando.
    overall = statistics.mean(scores) * 0.6 + min(scores) * 0.4 - len(flags) * 15
    overall = clamp(overall)
    verdict = "FORTE" if overall >= 70 and min(scores) >= 55 and not flags else (
        "OK" if overall >= 50 else "FRACO")
    return results, overall, verdict, flags


def bar(score, width=24):
    filled = round(score / 100 * width)
    return "#" * filled + "." * (width - filled)


def render_one(text, results, overall, verdict, flags, out=sys.stdout):
    print("\nNOTA DO GANCHO", file=out)
    print("=" * 62, file=out)
    print(f"  \"{text.strip()}\"\n", file=out)
    for name in CHECKS:
        score, detail = results[name]
        print(f"  {name:<13} {bar(score)} {score:5.1f}", file=out)
        print(f"  {'':<13} {detail}", file=out)
    print("-" * 62, file=out)
    print(f"  {'NOTA FINAL':<13} {bar(overall)} {overall:5.1f}   {verdict}", file=out)
    for f in flags:
        print(f"\n  ELIMINATÓRIO  {f}", file=out)
    if verdict != "FORTE":
        weakest = min(CHECKS, key=lambda c: results[c][0])
        print(f"\n  Ponto mais fraco: {weakest}. Corrija esse e rode de novo.", file=out)
    print("", file=out)


def render_table(rows, out=sys.stdout):
    print("\nRANKING DE GANCHOS\n" + "=" * 78, file=out)
    for i, r in enumerate(rows, 1):
        mark = "->" if i == 1 else "  "
        hook = r["hook"] if len(r["hook"]) <= 62 else r["hook"][:59] + "..."
        print(f"{mark} {r['score']:5.1f} {r['verdict']:<7} {hook}", file=out)
        print(f"        mais fraco: {r['weakest']} ({r['checks'][r['weakest']]['score']:.0f})", file=out)
        for f in r["flags"]:
            print(f"        eliminatório: {f}", file=out)
    print("\nGrave o primeiro. Se o primeiro tiver menos de 50, nenhum destes é o gancho.\n",
          file=out)


def main():
    ap = argparse.ArgumentParser(description="Dá nota a um gancho de Reel em cinco propriedades.")
    ap.add_argument("input", nargs="?", default="-", help="arquivo com um gancho por linha, ou -")
    ap.add_argument("--hook", help="avalia um único gancho passado na linha de comando")
    ap.add_argument("--json", action="store_true")
    args = ap.parse_args()

    if args.hook:
        lines = [args.hook]
    else:
        raw = sys.stdin.read() if args.input == "-" else open(args.input, encoding="utf-8").read()
        lines = [l.strip() for l in raw.splitlines() if l.strip()]
    if not lines:
        print("nada para avaliar", file=sys.stderr)
        sys.exit(2)

    payload = []
    for line in lines:
        results, overall, verdict, flags = run(line)
        payload.append({
            "hook": line,
            "checks": {k: {"score": round(v[0], 1), "detail": v[1]} for k, v in results.items()},
            "weakest": min(CHECKS, key=lambda c: results[c][0]),
            "flags": flags,
            "score": round(overall, 1),
            "verdict": verdict,
        })

    if args.json:
        print(json.dumps(payload if len(payload) > 1 else payload[0], indent=2, ensure_ascii=False))
        return

    if len(payload) == 1:
        results, overall, verdict, flags = run(lines[0])
        render_one(lines[0], results, overall, verdict, flags)
    else:
        render_table(sorted(payload, key=lambda r: -r["score"]))

    sys.exit(0 if max(p["score"] for p in payload) >= 70 else 1)


if __name__ == "__main__":
    main()
