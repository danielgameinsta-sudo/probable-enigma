---
name: ig-human
description: >-
  Tira a marca de máquina de qualquer rascunho - travessões, palavras típicas
  de IA, caracteres invisíveis - e dá nota com um painel de cinco verificações
  antes de sair. Use sempre que um texto precisar soar humano, quando o usuário
  disser "humaniza", "isso parece IA?", "tira os travessões", "tá com cara de
  ChatGPT", "deixa mais natural", ou antes de mostrar ao usuário qualquer
  legenda, roteiro, comentário, resposta ou DM.
---

# ig-human

**Idioma:** esta versão foi adaptada para textos em português do Brasil. O
léxico e os padrões procuram os vícios de IA em português.

Duas ferramentas ficam nesta pasta e as duas funcionam de verdade. Use as duas.
Não avalie no olho.

```bash
python3 humanize.py rascunho.txt --report       # limpa e mostra o que mudou
python3 detect.py rascunho.txt                   # dá nota, cinco verificações
python3 detect.py antes.txt depois.txt           # prova a diferença
```

As duas leem o `slop.json`: cerca de 220 palavras e expressões prontas com
substituições simples, 18 classes de caracteres invisíveis, 11 trocas
tipográficas e 16 vícios de estrutura. Ele foi feito para ser editado. Se o
usuário tem uma palavra que sempre usa e o léxico remove, tire do arquivo.

## Por que isso importa mais no Instagram do que parece

Legendas são curtas e roteiros são falados em voz alta. Uma frase com cara de
texto escrito numa legenda de 600 caracteres é uma parte maior do texto do que
a mesma frase numa redação, e uma locução que ninguém falaria naturalmente fica
óbvia no primeiro take. O problema aqui não é um detector marcar o post. É uma
pessoa passar direto por algo que parece propaganda, ou um criador tropeçar no
próprio roteiro.

## O que é corrigido automaticamente

**1. Caracteres invisíveis.** Espaços e junções de largura zero, hífens
ocultos, marcas de ordem de bytes, caracteres de tag Unicode, separadores
invisíveis, espaços rígidos e estreitos. Um teclado não produz isso. Eles
sobrevivem ao copiar e colar, são invisíveis em qualquer editor e são a coisa
mais mecânica de um texto gerado. O `humanize.py` apaga todos.

**2. Tipografia.** Travessão vira vírgula, meia-risca vira hífen, aspas curvas
viram retas, reticências de um caractere viram três pontos, marcador vira
hífen. A passada do travessão é a que importa: ela troca o travessão por
vírgula e depois limpa a pontuação dupla que sobra.

**3. O vocabulário de IA.** potencializar, alavancar, jornada, crucial,
robusto, "no cenário atual", "vale ressaltar que", "em suma", "sair da zona de
conforto", "conteúdo de valor". Cada um é trocado por uma palavra simples ou
apagado, mantendo as maiúsculas e sem mexer em links. Depois da troca, confira
a concordância (gênero e número) da frase: a regex não entende gramática.

## O que NÃO é corrigido automaticamente

Vícios de estrutura são **apontados, não reescritos**, porque mudar o formato
de uma frase exige julgamento:

- "Não é apenas X, é Y" e "não só X, mas também Y"
- "Não é sobre X. É sobre Y."
- Trios de três itens
- Linha só com pergunta retórica: "O resultado?"
- A enrolação de vídeo: "neste vídeo eu vou te mostrar"
- Listas com emoji como marcador
- Três ou mais palavras gritadas seguidas
- Muro de hashtags
- Iscas automáticas: "me segue para mais", "marque alguém que", "e você, o que
  acha?"

Essa lista é trabalho seu. Reescreva cada frase apontada à mão, mantendo o
sentido, e rode o `detect.py` de novo. É essa parte que leva a nota de REVISAR
para APROVADO, e é a parte que um script não faz.

## As cinco verificações

O `detect.py` dá nota de 0 a 100 a cinco sinais; quanto maior, mais humano:

| verificação | o que mede | cara de máquina |
| --- | --- | --- |
| VARIAÇÃO | variação no tamanho das frases | toda frase do mesmo tamanho |
| CONCRETUDE | números, nomes e marcadores concretos a cada 100 palavras | substantivos abstratos, nenhum número |
| TERMOS DE IA | ocorrências do léxico a cada 100 palavras | vocabulário pronto |
| TIPOGRAFIA | invisíveis, travessões e aspas curvas a cada 1.000 caracteres | tipografia perfeita demais |
| VOZ | coloquialismos (pra, tá, né...), pessoa do discurso, vícios de estrutura | nada coloquial, revelações encenadas |

O veredito pesa a média em 60% e a **verificação mais fraca** em 40%, porque um
sinal basta. APROVADO exige nota geral 70+ e nenhuma verificação abaixo de 55.

## Diga isso com honestidade

São cinco heurísticas locais inspiradas nos sinais em que os detectores
públicos se baseiam. Elas rodam inteiramente na máquina do usuário e nada é
enviado. Elas **não** são GPTZero, Originality, Copyleaks, Winston nem
Turnitin, não chamam essas APIs e não garantem esses vereditos. Corrigir o que
elas medem costuma mexer nesses números, porque medem as mesmas coisas. Essa é
a promessa. Não faça uma maior em nome do usuário, e nunca diga a ele que o
texto é indetectável.

## Ordem das etapas

1. `humanize.py rascunho.txt -o limpo.txt --report`
2. Leia os vícios de estrutura apontados. Reescreva essas frases você mesmo.
3. `detect.py rascunho.txt limpo.txt` para mostrar o antes e depois.
4. Se o veredito não for APROVADO, corrija a verificação mais fraca indicada na
   saída e rode de novo. Duas rodadas é normal. Cinco significa que o rascunho
   foi escrito por fórmula, e a solução é outro rascunho, não mais passadas.
5. Mostre ao usuário o texto limpo e a nota. Nunca só a nota.
