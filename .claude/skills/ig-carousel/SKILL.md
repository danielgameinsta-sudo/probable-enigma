---
name: ig-carousel
description: >-
  Monta um carrossel do Instagram - a capa que faz a pessoa arrastar, o texto
  slide a slide e os arquivos 1080x1350 para subir. Use quando o usuário disser
  "carrossel", "slides", "post de arrastar", "transforma isso num carrossel",
  ou tiver uma ideia em formato de lista ou de passos que morreria como imagem
  única.
---

# ig-carousel

**Idioma:** escreva em português do Brasil, na voz do usuário
(`instagram/voice.md`).

Carrossel é o formato com mais tempo de permanência na grade, porque arrastar é
uma interação e rolar não é. Ele também ganha uma segunda chance: o Instagram
pode mostrar o carrossel de novo, a partir de um slide mais à frente, para quem
não interagiu da primeira vez, então o slide dois também precisa funcionar
sozinho.

O formato premia uma ideia dividida em passos. Ele pune uma legenda cortada em
pedaços.

## Quando usar em vez de um Reel

Use carrossel quando a ideia tem **sequência e precisa ser relida**: passos, um
método com partes, um antes e depois, uma lista que vale print. Use Reel quando
a ideia tem movimento, um rosto, ou um resultado que precisa ser visto
acontecendo.

Se a ideia é uma afirmação só, não é nenhum dos dois. Passe para o `/ig-reel` e
diga isso.

## Estrutura

De 6 a 10 slides. O limite é 20, e 20 é quase sempre um livro que ninguém
termina. Abaixo de 5, o arrastar nem começa.

```
1         CAPA      o gancho. 6 palavras ou menos, num tamanho que dá para ler
                    na grade em miniatura. Uma linha de promessa embaixo.
2         O QUE ESTÁ por que isso importa, numa frase. Este slide também é uma
          EM JOGO   segunda capa, então não pode ser preparação.
3 a N     UMA IDEIA POR SLIDE. Título de 3 a 7 palavras, no máximo 25 palavras
                    embaixo. Se um slide precisa de um parágrafo, são dois slides.
N+1       RESUMO    tudo numa lista. É o slide do print.
ÚLTIMO    PEDIDO    uma ação. Salvar, comentar uma palavra-chave ou seguir. Uma.
```

## Regras do texto dos slides

- **A capa é 80% do resultado.** Seis palavras. Grandes. Nada no resto salva
  uma capa que ninguém arrasta.
- **Pense no corte da grade.** A grade do perfil corta num retângulo em pé, e a
  proporção exata já mudou mais de uma vez. Monte em 1080x1350 e deixe o texto
  da capa bem no meio, longe dos 120 pixels de cada borda, e o corte deixa de
  importar.
- **Numere os slides** (3/8). As pessoas vão até o fim quando conseguem ver o
  fim.
- **Nenhum slide é um parágrafo.** Se não cabe em 25 palavras, divida.
- **O slide de resumo é o que as pessoas printam e mandam.** Compartilhamento é
  o sinal mais forte que você consegue. Faça ele funcionar sozinho, sem
  contexto.
- **O @ em todo slide**, pequeno, no canto de baixo. Print viaja sem você.
- **Texto alternativo pelo menos na capa.** É lido por leitores de tela e pelo
  Instagram.

## Montando os arquivos

O Instagram quer 1080x1350 (4:5), JPEG ou PNG, até 20 itens. Monte em HTML e
exporte cada slide:

```bash
# uma <section> por slide, 1080x1350, page-break-after: always
# depois Chrome headless --print-to-pdf, ou qualquer HTML-para-imagem que você já use
```

Escreva o HTML com `width:1080px; height:1350px`, uma única cor de destaque, e
fonte de no mínimo 32px, porque isso é lido num celular a um terço do tamanho
real. Se o projeto tiver uma skill de marca ou um design system, use e não
invente uma paleta. Confira se os acentos aparecem certo na fonte escolhida.

## Saída

Primeiro o texto slide a slide, como uma lista numerada que o usuário consegue
ler em dez segundos e editar antes de qualquer coisa ser gerada. Depois a
**legenda**, que num carrossel é a Função B do `/ig-caption`: a legenda
trabalha aqui, porque a capa já usou suas seis palavras.

Passe os dois pelo `/ig-human`. Gere os arquivos só depois que o usuário
aprovar o texto.

```
CARROSSEL  ·  8 slides

1  CAPA      A CLÁUSULA DE R$ 18 MIL
             Uma linha que agora vai em todo contrato meu.
2  EM JOGO   Eu aprovei o trabalho. Nove dias depois pediram o dinheiro de volta.
3            O QUE ELA DIZ
             Pagamento na entrega, não na aprovação.
...
7  RESUMO    As quatro linhas, em ordem.
8  PEDIDO    Comente CONTRATO que eu te mando a cláusula completa.

Legenda: Função B, gancho na linha 1, um pedido, 3 hashtags.
```

Nada é enviado. O usuário posta.
