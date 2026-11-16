---
name: ig-caption
description: >-
  Escreve a legenda do Instagram - a linha que sobrevive ao corte do "... mais",
  o corpo, o único pedido, os termos de busca e as hashtags - e confere tudo
  antes de sair. Use quando o usuário disser "escreve a legenda", "faz a
  legenda disso", "o que eu coloco na descrição", tiver um reel ou carrossel
  pronto e precisar do texto, ou perguntar sobre hashtags.
---

# ig-caption

**Idioma:** escreva em português do Brasil, na voz do usuário
(`instagram/voice.md`).

Uma ferramenta fica nesta pasta e ela funciona:

```bash
python3 caption.py legenda.txt
python3 caption.py legenda.txt --keywords "proposta para cliente,preço de agência"
```

Ela mostra a legenda do jeito que o feed mostra: os primeiros 125 caracteres
numa caixa, o resto atrás do toque. Leia essa caixa antes de ler qualquer outra
coisa que você escreveu.

## Primeiro, decida qual é a função desta legenda

É essa decisão que estraga legendas quando é pulada.

**Função A: o vídeo já prendeu a pessoa.** Um Reel tem o próprio gancho nos
dois primeiros segundos, falado e na tela. A legenda não é um segundo gancho, e
competir com o vídeo é o jeito de perder os dois. A função dela é o pedido, o
contexto que faz o pedido fazer sentido, e as palavras que as pessoas buscam.

**Função B: a legenda é o conteúdo.** Uma foto, uma imagem única, uma capa de
carrossel que abre um suspense. Aqui a primeira linha é o gancho e funciona
igual ao gancho de um Reel: concreta, curta, e cortada num momento de suspense,
não no meio de uma explicação.

Pergunte qual das duas você está escrevendo. Se o usuário tem um Reel com
gancho forte, escreva a A e explique por quê.

## O formato

```
Linha 1     125 caracteres visíveis. Função A: o pedido, direto.
            Função B: o gancho.
            Nunca um cumprimento, nunca uma hashtag, nunca um emoji como
            primeiro caractere.
Corpo       parágrafos curtos, uma linha em branco entre eles. De dois a seis.
            É aqui que ficam os termos de busca.
O pedido    um. Comentar uma palavra-chave, salvar ou mandar DM. Um.
Hashtags    até cinco, numa linha só no final, ou nenhuma.
```

O limite é 2.200 caracteres e quase nada precisa disso. Uma legenda que ganha o
toque e entrega 600 caracteres vale mais que uma que entrega 1.800.

## Hashtags, com honestidade

Hashtag não é mais alavanca de alcance, e a plataforma já disse isso com uma
mudança no produto. **O Instagram limitou as hashtags a cinco por post em 18 de
dezembro de 2025**, antes eram trinta, dizendo aos criadores que usar menos
hashtags (até 5), mais específicas, funciona melhor que muitas genéricas. Adam
Mosseri já tinha dito em fevereiro de 2025 que hashtag não aumenta alcance e é
um rótulo, não uma forma de distribuição.

Então: até cinco, específicas, como rótulos de tema. Se o usuário tem um bloco
de vinte salvo no bloco de notas, esse bloco agora é peso morto e o verificador
vai reprovar.

`#viral`, `#fyp`, `#explorar`, `#reelsbrasil`, `#sucesso` não descrevem nada.
Corte.

## Termos de busca importam mais que hashtags agora

A busca do Instagram lê o texto da legenda. Então a expressão pela qual o
usuário quer ser encontrado entra na legenda como uma expressão que uma pessoa
digitaria, numa frase normal. "Proposta para cliente" como palavras na linha
três, não "#propostaparacliente" num bloco no fim.

Peça dois ou três desses termos e passe para o verificador:

```bash
python3 caption.py rascunho.txt --keywords "proposta para cliente,preço de agência"
```

## Regras

- **Nada de link na legenda.** Legenda não é clicável. Um link no texto é texto
  morto que diz "eu não uso esta rede". Bio ou DM.
- **Um pedido.** Dois pedidos é o mesmo que nenhum. O `caption.py` conta.
- **O pedido de palavra-chave precisa de uma palavra fácil de digitar.** Uma
  palavra, sem espaço, sem emoji, sem acento se der, e fale ela no vídeo
  também. `Comente CONTRATO` funciona. `Comente "o guia do contrato"` não.
- **Escreva o primeiro comentário separado** se tiver um link. Diga isso no
  resumo.
- **Emoji como pontuação, não enfeite.** O verificador aponta mais de 4 a cada
  100 caracteres.
- **Texto alternativo vale 20 segundos.** Em carrosséis e fotos, escreva. Ele é
  lido por leitores de tela e pelo Instagram.

## O passo a passo

1. Decida Função A ou B e diga qual.
2. Escreva o rascunho.
3. Rode o `/ig-human` nele. Legendas são curtas, então o vocabulário de IA
   aparece mais aqui do que em qualquer outro lugar do pacote.
4. Rode o `caption.py` com os termos de busca do usuário. Corrija toda FALHA.
   Decida em voz alta sobre cada ATENÇÃO, em vez de ignorar em silêncio.
5. Mostre o bloco pronto para copiar, depois o resumo:

```
LEGENDA PRONTA
função:       A - o reel tem o gancho
visível:      118 de 125 caracteres usados antes do corte
pedido:       um, comentar CONTRATO
hashtags:     3
busca:        "proposta para cliente" na linha 3, "preço de agência" na linha 5
verificador:  PRONTA
```

Nada é publicado. O usuário cola.
