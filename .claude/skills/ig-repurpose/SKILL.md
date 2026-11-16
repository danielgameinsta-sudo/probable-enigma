---
name: ig-repurpose
description: >-
  Transforma um conteúdo longo - vídeo do YouTube, podcast, live, newsletter,
  post de blog ou reunião com cliente - numa semana de reels e carrosséis. Use
  quando o usuário disser "reaproveita isso", "transforma em reels", "tenho um
  vídeo/podcast/transcrição", "corta isso em pedaços", ou colar algo longo e
  quiser levar para o Instagram.
---

# ig-repurpose

**Idioma:** escreva os posts em português do Brasil, na voz do usuário, mesmo
que o material de origem esteja em outro idioma (avise se estiver).

Um bom conteúdo longo tem de quatro a seis posts dentro dele. A maioria das
pessoas tira um e joga o resto fora.

## Entrada

Uma transcrição, um artigo, uma newsletter, um roteiro, um resumo de reunião,
uma live. Se o usuário mandar um link e esta sessão tiver uma ferramenta de
transcrição, use; senão, peça para colar. Leia tudo antes de extrair qualquer
coisa.

Se a origem é um vídeo do próprio usuário, peça o arquivo também. Um Reel feito
com as imagens dele ganha de um feito com as palavras dele lidas de novo.

## Extraia, não resuma

O resumo de um vídeo não é um Reel. Ninguém quer o resumo. Percorra o material
e tire as coisas que funcionam sozinhas:

| tirar | o que é |
| --- | --- |
| **Afirmações** | toda frase que começaria uma discussão |
| **Números** | todo valor, custo, duração, porcentagem |
| **Histórias** | todo momento com uma pessoa, uma cena e um custo |
| **Mecanismos** | todo "o jeito que isso funciona de verdade é..." |
| **Erros** | toda admissão de algo que deu errado |
| **Frases** | toda frase que já dá para citar do jeito que está |

Liste o que você achou, com contagens, antes de escrever qualquer coisa. Se o
material rende menos de quatro itens, ele é fraco, e quatro posts espremidos
dele vão ser fracos também. Diga isso.

## Depois escolha o formato de cada item

Nem tudo é Reel.

- **Afirmação, erro, história** viram Reel. Precisam de voz e rosto.
- **Mecanismo, lista numerada** viram carrossel. Precisam ser relidos.
- **Uma frase citável** vira um quadro de story, não um post.

## Depois monte a semana

Cada item vira um post, e cada post funciona completamente sozinho. O
espectador não viu a origem e nunca vai ver. Nunca escreva "como eu falei no
meu último vídeo". O post é a coisa.

Atribua uma fórmula de gancho do `ig-reel/hooks.json` a cada um e varie. Cinco
posts da mesma origem com o mesmo formato de gancho parecem fábrica de
conteúdo, porque são.

Se a origem é um vídeo do próprio usuário, **use as imagens reais**. O trecho
em que ele disse a coisa, com a reação de verdade, ganha de uma regravação
sempre. Corte na frase, não na respiração.

Ordene a semana para a afirmação mais forte ir primeiro, a história cair no
meio da semana e o mecanismo ir por último, quando quem gostou dos anteriores
está esperando por ele.

## Saída

```
ORIGEM: "Por que a gente acabou com as reuniões de diagnóstico" (podcast de 42 min, 8.900 palavras)

ACHEI  5 afirmações, 9 números, 3 histórias, 4 mecanismos, 2 erros, 7 frases citáveis

SEMANA
TER  REEL       #2  Ordem Negativa       Pare de fazer reunião de diagnóstico
                                         use o trecho de 14:20, ele ri no final
QUA  CARROSSEL  legenda Função B         O formulário de 4 perguntas que substituiu a reunião
SEX  REEL       #21 Começo no Meio       "...e nove dias depois ele pediu reembolso"
DOM  REEL       #5  Tempo Encolhido      Seis horas por semana de volta, um link apagado

Diga "escreve a terça" que eu faço o rascunho.
```

Depois escreva sob pedido, um de cada vez, cada um passando pelo `/ig-reel` e
pelo `/ig-human`. Não entregue quatro roteiros prontos de uma vez. Eles vão
soar todos iguais e o usuário não vai gravar nenhum.
