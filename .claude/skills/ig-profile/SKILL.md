---
name: ig-profile
description: >-
  Dá nota de 0 a 100 a um perfil do Instagram com uma régua de 12 itens e
  reescreve as partes que perdem pontos - campo do nome, bio, link, destaques,
  os três fixados, grade. Use quando o usuário disser "otimiza meu perfil",
  "arruma minha bio", "dá nota pro meu Instagram", "por que ninguém me segue",
  ou colar o perfil e perguntar como ele está.
---

# ig-profile

**Idioma:** escreva em português do Brasil. As reescritas de bio e nome usam
as palavras que o público brasileiro do usuário digita na busca.

Quase todo mundo otimiza a coisa errada aqui. O perfil não é uma vitrine que as
pessoas ficam olhando. É uma **tela de decisão**, à qual se chega por um reel,
e ela tem uns três segundos para responder uma pergunta: tem mais disso aqui, e
é para mim?

## Entrada

Peça ao usuário para colar ou mandar print de: o campo do nome, o @, a bio,
para onde vai o link, os nomes dos destaques, o que está fixado e as nove
primeiras capas da grade. Um print do topo do perfil mais as duas primeiras
linhas da grade bastam para uma primeira avaliação.

Não faça login no Instagram em nome dele.

## Dê a nota

Leia o `rubric.json` nesta pasta. Doze itens, 100 pontos, cada um com como é a
nota máxima e como ele costuma falhar. Dê nota a todos os itens, mostre a
tabela, dê o total. Seja honesto. A maioria dos perfis fica entre 30 e 49 na
primeira avaliação, e nota generosa não serve para nada.

```
NOTA DO PERFIL  38/100

  campo do nome       2/12   só o nome, nenhuma palavra que alguém busque
  1ª linha da bio     3/12   três substantivos e um emoji de café
  três fixados        0/10   nada fixado
  destaques           2/8    "Aleatório", "Vida", "2023"
  legibilidade grade  4/8    seis de nove capas são um rosto no meio da frase
  ...
```

## Depois reescreva, nesta ordem

Corrija em ordem decrescente de pontos perdidos. Não reescreva tudo de uma vez:
o usuário precisa ir lá e mudar cada uma dessas coisas.

**1. O campo do nome (30 caracteres).** A linha em negrito embaixo da foto, não
o @. É o campo que a busca do Instagram compara, e a maioria das contas coloca
só o nome. Formato que funciona: `{Nome} | {o que faz, com palavras buscadas}`.
Dê três opções.

**2. A primeira linha da bio.** Para quem é e o que muda. Não é cargo, não são
adjetivos, não é lista de identidades separada por barras. O resto dos 150
caracteres traz uma prova ou uma oferta simples.

**3. Os três fixados.** Três espaços, três funções diferentes: a melhor prova, a
explicação mais clara da oferta, a melhor apresentação da pessoa. É a correção
com mais impacto do perfil inteiro e leva quatro toques. Uma grade sem fixados
mostra o que foi postado por último, que é cara ou coroa.

**4. Destaques.** De quatro a seis, com os nomes das perguntas que um comprador
faz: Preços, Resultados, Como funciona, Sobre. Não "Aleatório". Apague o resto.

**5. O link.** Um destino que bate com o que a bio acabou de prometer. São
permitidos cinco; dois já é um cardápio, e cardápio converte pior que uma
porta.

**6. Capas da grade.** As nove primeiras em tamanho miniatura. Capas de reel são
escolhidas, não deixadas no primeiro quadro. Quatro palavras de texto numa capa
tornam a grade legível de relance.

## Saída

A tabela de notas, depois as reescritas em blocos prontos para copiar, na ordem
de correção, cada um passado pelo `/ig-human`. Dê a nota de novo no final e
mostre a diferença com honestidade. Se a reescrita chega a 84 e não a 98, diga
84, e diga o que falta, que normalmente é uma grade, o hábito de postar stories
e um post fixado que ainda não existe. Nada disso é reescrita.

Esta skill não salva nada no Instagram. O usuário edita cada campo.
