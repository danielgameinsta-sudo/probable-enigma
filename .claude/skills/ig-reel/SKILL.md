---
name: ig-reel
description: >-
  Escreve um Reel do Instagram a partir de uma ideia crua - opções de gancho
  com base em 26 fórmulas, o roteiro falado, o texto da tela e uma tabela de
  tempos - na voz do próprio usuário e com nota antes de gravar. Use sempre que
  o usuário quiser um Reel, roteiro de vídeo curto, gancho, locução, "faz um
  reel sobre X", "o que eu falo nesse vídeo", "roteiro pra reels", ou estiver
  prestes a gravar sem ter a primeira frase.
---

# ig-reel

Transforma uma ideia crua num Reel que as pessoas assistem até o fim.

**Idioma:** escreva tudo em português do Brasil, no registro falado do usuário
(pode usar "pra", "tá", "a gente" se for como ele fala). Valores em reais
(R$), a não ser que o usuário use outra moeda.

Duas ferramentas ficam nesta pasta e as duas funcionam de verdade. Use as duas.
Não avalie o gancho no olho e não chute a duração.

```bash
python3 hookscore.py ganchos.txt             # ranqueia as opções de gancho
python3 hookscore.py --hook "uma frase"      # avalia um gancho só
python3 beats.py roteiro.txt --target 30     # tabela de tempos antes de gravar
```

## Antes de escrever

1. Leia `instagram/voice.md` se existir. É o perfil de voz do usuário: como
   ele fala na câmera, o que ele nunca diz, com quem ele está falando. Se não
   existir, peça **três reels dele mesmo**, transcreva ou leia, deduza a voz e
   escreva o arquivo. Roteiro na voz errada não serve, porque ele vai ter que
   falar aquilo em voz alta.
2. Leia `hooks.json` nesta pasta. São 26 fórmulas, cada uma com modelo,
   exemplo preenchido, versão para a tela, para que serve e como costuma ser
   estragada.
3. Se a ideia estiver rala, não encha linguiça. Faça uma única pergunta
   agrupada: o que aconteceu, com quem, e quanto custou ou rendeu. Um Reel
   precisa de uma coisa específica e verdadeira. Consiga isso antes de escrever.
4. Se `instagram/swipe.md` existir, leia. É o `/ig-viral` que escreve esse
   arquivo, e ele é a prova do próprio usuário sobre quais fórmulas estão
   funcionando no nicho dele agora. Ele vale mais que os padrões deste arquivo.

## O formato

Um Reel é decidido nos dois primeiros segundos e mantido nos cinco seguintes.

```
0:00 - 0:02   GANCHO      a afirmação. Frase falada e frase da tela, escritas
                          separadas. Movimento no primeiro quadro, não um rosto parado.
0:02 - 0:07   O QUE ESTÁ  por que isso importa para quem está assistindo. Uma frase.
              EM JOGO
0:07 - ...    O CORPO     uma ideia por trecho, e a imagem muda a cada trecho.
ÚLTIMOS 3s    A ENTREGA   entrega o que o gancho prometeu, depois o único pedido.
ÚLTIMA FRASE  O LOOP      repete uma palavra do gancho para o replay encaixar.
```

Duração: de 15 a 45 segundos é a faixa que funciona. Reels vão até 3 minutos e
quase ninguém deveria usar isso. Abaixo de 7 segundos, o número de loops infla
e nada mais melhora.

## O passo a passo

**1. Escolha três ganchos, não um.** Passe a ideia pelo `hooks.json`, escolha
três fórmulas que realmente encaixam e escreva a frase falada e a frase da tela
de cada uma. Fórmulas diferentes, não três versões da mesma.

**2. Dê nota.** Coloque as três frases faladas num arquivo, uma por linha, e
rode o `hookscore.py`. Mostre o ranking ao usuário. Se o primeiro tiver menos
de 50, você ainda não tem o gancho, e nenhuma edição resolve isso.

**3. Escreva o roteiro** com o gancho vencedor. Linguagem falada simples, do
jeito que o usuário fala de verdade. Frases curtas. Nenhuma frase que ele
precise ensaiar.

**4. Cronometre.** Rode `beats.py roteiro.txt --target {duração}`. Corrija todo
alerta: gancho passando de 3 segundos, qualquer trecho acima de 4 segundos,
sequência de trechos sem nada concreto, falta de loop. Rode de novo até ficar
limpo. Se o usuário fala rápido ou devagar, ajuste com `--wpm`.

**5. Humanize.** Passe o roteiro pelo `/ig-human` antes de mostrar. Uma frase
com cara de texto escrito fica óbvia no momento em que alguém fala em voz alta.

**6. Mostre o bloco.** O roteiro num bloco de código, o texto da tela como uma
lista separada com os tempos, e depois:

```
REEL PRONTO
gancho:       #3 Ninguém Te Conta, nota 86 FORTE
duração:      28,4s em 9 trechos a 155 ppm
na tela:      6 cartelas
humanizador:  4 marcas removidas, nota humana 81 APROVADO
legenda:      rode /ig-caption em seguida

Responda "sim" para registrar, ou me diga o que mudar.
```

**7. Nunca publique.** Esta skill produz um roteiro. O usuário grava e posta.
Com o "sim", acrescente ao `instagram/log.md` a data, a fórmula de gancho usada
e a primeira frase, para que o `/ig-audit` tenha um histórico depois.

## O texto da tela é um roteiro separado

Escreva separado, sempre. Ele é lido antes de ser ouvido.

- **Seis palavras ou menos por cartela.** Está sendo lido à distância de um
  braço por alguém que ainda não está ouvindo.
- **A cartela do gancho aparece no quadro 1**, não depois de um silêncio.
- **Fique dentro da área segura.** Num quadro de 1080x1920, nada acima de
  y=230 ou abaixo de y=1440, e deixe livres os 230 pixels da direita. A
  interface fica por cima de tudo que está fora dessa caixa: a legenda, os
  botões laterais, a faixa do áudio.
- **Nunca coloque o gancho onde fica a legenda.** É a parte de baixo do quadro
  e ela fica coberta.
- **Coloque legenda fixa no corpo do vídeo.** A maioria assiste sem som
  primeiro.

## Regras que fazem a diferença

- **Uma ideia por Reel.** Se o roteiro tem duas, são dois Reels. Diga isso.
- **Números em vez de adjetivos.** "R$ 4.200" ganha de "muito dinheiro". Se o
  usuário não deu um número, peça um em vez de escrever em volta do buraco.
- **Corte a introdução.** Sem cumprimento, sem "neste vídeo", sem nome, sem
  vinheta. O vídeo começa na frase que você normalmente diria no segundo seis.
- **Mude a imagem a cada trecho.** Plano parado por 8 segundos é onde as
  pessoas saem, e o `beats.py` vai apontar isso.
- **Um pedido no final.** Comentar uma palavra-chave, salvar ou seguir. Um.
- **Nunca invente.** Nada de métricas, clientes, faturamento ou resultados
  inventados em nome do usuário, nem como exemplo provisório. Se precisa de um
  número e você não sabe, deixe `{{seu número}}` no roteiro e avise.
- **Não escreva roteiro em cima de um áudio em alta que o usuário não pode
  usar.** Se a ideia precisa da voz do próprio usuário, diga isso.

## Exemplo

```
/ig-reel a gente reduziu o tempo de fazer proposta de 5 horas para 20 minutos com um modelo
```

```
GANCHOS  (com nota)
  86  FORTE  #5  Tempo Encolhido        "Proposta me levava cinco horas. Hoje leva vinte minutos."
                                        na tela: 5 HORAS -> 20 MIN
  74  FORTE  #1  Confissão de Prejuízo  "Cobrei quatro horas por semana só de formatação. Por dois anos."
                                        na tela: 2 ANOS PERDIDOS
  54  OK     #9  Pode Roubar            "Rouba o modelo de proposta que fez isso."
                                        na tela: PODE ROUBAR

Gravando o #5: a proporção é crível, dá para ler de relance na tela,
e o número é seu.
```
