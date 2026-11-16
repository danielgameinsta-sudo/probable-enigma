---
name: ig-plan
description: >-
  Monta a semana no Instagram - o que postar, em qual formato, quando, e com
  quem interagir. Use quando o usuário disser "planeja minha semana", "o que eu
  posto", "calendário de conteúdo", "cronograma de posts", "não tenho sobre o
  que postar", ou quiser uma agenda de posts e uma lista de interação.
---

# ig-plan

**Idioma:** escreva em português do Brasil. Horários no fuso do público
(normalmente horário de Brasília, se o público é brasileiro).

A sala de controle. Todo o resto do pacote executa; esta decide o que é
executado. Rode uma vez por semana, sempre no mesmo dia.

## Entrada

Se `instagram/voice.md`, `swipe.md` e `log.md` existirem, leia. O arquivo de
referências é a prova do próprio usuário, vinda do `/ig-viral`, sobre quais
fórmulas estão dando certo no nicho dele agora, e ele vale mais que qualquer
coisa deste arquivo. O log impede o plano de repetir um tema das últimas duas
semanas.

Se não existirem, peça quatro coisas e anote:

1. O que o usuário vende, e para quem.
2. Os três ou quatro temas pelos quais ele quer ser conhecido.
3. O que realmente aconteceu nesta semana: uma conversa com cliente, um número,
   um erro, uma coisa que ele construiu, uma discussão. É daí que vêm os posts.
4. Dez contas para as quais vale a pena aparecer.

## O que postar

De quatro a cinco posts por semana, e pelo menos três deles Reels. Reel é o
único formato do Instagram que chega de forma confiável em quem não segue a
conta. Carrossel aprofunda com quem já segue. Stories são diários e são
planejados à parte.

Misture ao longo da semana, nunca dois do mesmo tipo seguidos:

| tipo | quantidade | função |
| --- | --- | --- |
| **Prova** | 1 por semana | algo que aconteceu, com um número. Reel. |
| **Ensino** | 1 a 2 por semana | uma coisa que o espectador pode fazer hoje. Reel ou carrossel. |
| **Opinião** | 1 por semana | uma posição que pode te custar seguidores. Reel. |
| **História** | 1 a cada duas semanas | uma cena com um custo. Reel. |
| **Oferta** | 1 a cada duas semanas | o que você vende, dito com clareza, sem pedir desculpa. Carrossel ou stories. |

Para cada espaço, dê: o tema, o ângulo específico a partir do que aconteceu de
verdade nesta semana, o formato e o número da fórmula de gancho do
`ig-reel/hooks.json`. Não um assunto, um ângulo. "IA" não é um plano. "A
proposta que a gente perdeu porque o rascunho tinha travessão" é um Reel.

## Quando postar

Poste quando o público do usuário está acordado e fora do trabalho. Para a
maioria dos públicos de consumo, isso é começo da noite no horário local; para
público empresarial, começo da manhã.

Mas diga isto com todas as letras: **o horário importa muito menos que os dois
primeiros segundos.** O Instagram continua mostrando um Reel por dias se ele
performa, e enterra um bem cronometrado que não performa. Se o usuário está
otimizando horário antes de os ganchos funcionarem, ele está polindo a coisa
errada, e você deve dizer isso.

Use o fuso do público, não o do usuário, se forem diferentes.

## A rodada de interação, que não é opcional

20 minutos por dia, antes de postar, não depois. Monte uma lista de 10:

- **5 de alcance** - contas com o público que o usuário quer, onde um bom
  comentário é visto. Comente cedo, antes de a conversa ter 200 respostas.
- **3 pares** - mesmo tamanho, mesma área. É o grupo que retribui.
- **2 compradores** - pessoas que realmente poderiam comprar. Comente por
  semanas antes de qualquer DM, e nunca venda num comentário.

Passe a lista para o `/ig-comment`.

## Saída

```
SEMANA DE 15/09

SEG  só interação  (20 min, lista abaixo)
TER  19h30  REEL       PROVA     #5  Tempo Encolhido   - proposta de 5h para 20 min
QUA  só stories + interação
QUI  19h00  CARROSSEL  ENSINO    legenda Função B      - a cláusula em 4 slides
SEX  19h30  REEL       OPINIÃO   #2  Ordem Negativa    - pare de fazer reunião de diagnóstico
SÁB  -
DOM  18h00  REEL       HISTÓRIA  #21 Começo no Meio    - o e-mail do reembolso

STORIES  todo dia, 3 a 5 quadros, caixa de perguntas na quinta.

INTERAÇÃO  (5 alcance / 3 pares / 2 compradores)
  ...

Diga "escreve a terça" que eu faço o rascunho.
```

Grave o plano em `instagram/plan.md` para as outras skills lerem. Nada é
agendado nem publicado. Isto é um plano e quem executa é o usuário.
