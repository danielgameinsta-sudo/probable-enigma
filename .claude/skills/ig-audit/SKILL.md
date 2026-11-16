---
name: ig-audit
description: >-
  Análise do que o usuário já postou - quais reels funcionaram de verdade, por
  quê, e o que parar de fazer. Use quando o usuário colar as estatísticas
  (insights) do Instagram ou posts antigos e perguntar "o que está
  funcionando", "por que esse flopou", "analisa minhas métricas", "faz uma
  auditoria do meu conteúdo", ou quiser saber do que fazer mais.
---

# ig-audit

**Idioma:** escreva o relatório em português do Brasil. Números no formato
brasileiro (4.100 views, 18,2x).

A única fonte honesta do que funciona para uma conta é a própria conta. Toda
regra de todo guia de Instagram, incluindo as deste pacote, é um palpite
inicial. Os últimos 30 posts do usuário são a prova.

## Entrada

Peça o que o usuário tiver:

- Estatísticas por post: visualizações, alcance, interações, tempo de
  visualização, salvamentos, compartilhamentos, seguidores ganhos e a parte do
  alcance que veio de não seguidores. Print serve.
- Ou o gráfico de retenção dos melhores e piores reels recentes. Esse print
  vale mais que todo o resto junto.
- Ou só os posts e as views, o que basta para uma primeira análise.

Leia também o `instagram/log.md` se existir, porque ele registra qual fórmula
de gancho cada post usou.

## O que medir de verdade

Views brutas são o número menos útil da tela, porque dependem principalmente
de quantas pessoas já seguem a conta. Calcule estes e mostre a conta:

| métrica | como | o que diz |
| --- | --- | --- |
| **Múltiplo** | views / mediana de views da própria conta | se foi um acerto de verdade ou um dia normal |
| **Alcance em não seguidores** | % do alcance vindo de quem não segue | se o post viajou |
| **Retenção aos 3s** | quem ainda estava aos 3s / quem começou | se o gancho funcionou. É a nota do gancho. |
| **Tempo médio assistido** | direto das estatísticas | se o meio funcionou |
| **Compartilhamentos por alcance** | compartilhamentos / alcance | o sinal mais forte que dá para conquistar. Compartilhar é a pessoa colocar o nome dela naquilo. |
| **Seguidores por alcance** | seguidores ganhos / alcance | se o perfil converteu a atenção |

Ranqueie por múltiplo e compartilhamentos por alcance, não por views. Um reel
com 4 mil views e 90 compartilhamentos ganhou do que teve 60 mil views e 11.

## Depois ache o padrão

Com os cinco melhores e os cinco piores lado a lado, procure o que separa uns
dos outros, e esteja disposto a concluir algo de que o usuário não vai gostar:

- **Retenção aos 3 segundos.** Se os de cima e os de baixo diferem aqui, é o
  gancho e nada mais, e todo o resto é distração.
- **Fórmula de gancho.** Quais números do `ig-reel/hooks.json` estão nos cinco
  melhores?
- **Formato.** Reel, carrossel, imagem única.
- **Duração.** Agrupe em menos de 15s, 15 a 30s, 30 a 60s, mais de 60s.
- **Tema.**
- **Se o usuário respondeu os comentários na primeira hora.**
- **Dia e horário.** Confira isso **por último**, e só se o resto não mostrar
  nada. Quase nunca é a causa, e é onde as pessoas querem que seja.

Apresente a descoberta como uma afirmação com a prova junto, e diga o grau de
confiança. Com 30 posts dá para ver um padrão. Com 6 não dá, e dizer isso é
melhor que inventar um.

## A distinção que economiza meses

**Um reel que tem views e não traz seguidor não é um reel que falhou, é um
problema de perfil.** Um reel sem views é um problema de gancho. Separe os dois
antes de recomendar qualquer coisa. Se o alcance em não seguidores é alto e os
seguidores por alcance são baixos, pare de reescrever ganchos e vá para o
`/ig-profile`.

## Saída

```
AUDITORIA  ·  31 posts  ·  12/06 a 05/09  ·  mediana 4.100 views

5 MELHORES POR MÚLTIPLO
  18,2x  #3  Ninguém Te Conta       74.600 views  62% não seguidores  ret@3s 71%  128 compart.
   6,4x  #1  Confissão de Prejuízo  26.300 views  48% não seguidores  ret@3s 64%   71 compart.
  ...

5 PIORES
   0,3x  #11 Lista Com Favorito      1.200 views   9% não seguidores  ret@3s 31%    2 compart.
  ...

O QUE OS DADOS DIZEM
1. A retenção aos 3 segundos é a história inteira. Os cinco melhores têm média
   de 66%, os cinco piores, 33%. Todo o resto que te preocupa vem depois dos
   dois primeiros segundos.
2. Os posts em que quem se deu mal foi você: média de 8,1x contra 0,9x de todo
   o resto. n=5. É o sinal mais forte aqui, e não tem nem disputa.
3. Lista de ferramentas traz view e mais nada. Alcance alto, nenhum
   compartilhamento, nenhum seguidor. Três dos seus cinco piores.
4. Dia da semana não mostra nada. As médias de terça e sexta estão dentro do
   ruído. Pare de otimizar isso.

PARE: listas de ferramentas.
FAÇA MAIS: os posts com um custo que você pagou e um número junto.
```

Depois passe as conclusões para o `/ig-plan`, para a próxima semana ser montada
com as provas do próprio usuário em vez de padrões, e para o `/ig-viral`, para
o arquivo de referências ser filtrado pelas fórmulas que funcionam nesta conta
especificamente.
