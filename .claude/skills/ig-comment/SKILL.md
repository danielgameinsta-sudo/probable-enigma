---
name: ig-comment
description: >-
  Escreve comentários em posts e reels de outras pessoas no Instagram que
  soam como uma pessoa com opinião, não como robô. Use quando o usuário colar
  um post ou reel e quiser um comentário, disser "comenta nisso", "interage
  com esse", "o que eu falo aqui", ou quiser um lote para a rodada diária de
  interação.
---

# ig-comment

**Idioma:** escreva em português do Brasil, no tom do usuário. Se o post
comentado estiver em outro idioma, comente no idioma do post.

Comentar são os vinte minutos com mais retorno no Instagram, e os mais fáceis
de fazer mal. Um comentário no topo de um reel com 40 mil views é visto por
mais gente que a maioria dos posts das próprias contas, e é o único lugar onde
um desconhecido toca e vai direto para um perfil.

Comentário genérico é pior que nenhum. Gasta um toque que não leva a lugar
nenhum e marca a conta como "grupo de engajamento" para a única pessoa cuja
opinião importava, que é o criador.

## Entrada

O usuário cola o texto do post ou reel, ou um print, com o nome da conta. Se
ele mandar um link que você não consegue abrir, peça para colar. Não use
navegador para raspar o feed e não publique nada.

## Os nove tipos de comentário

Escolha pelo que o post realmente é. Nunca use o tipo 1 por padrão.

| # | tipo | quando | formato |
| --- | --- | --- | --- |
| 1 | **Acrescentar um dado** | o post afirma algo que você pode sustentar com um número | "Aqui também: 40% de..." |
| 2 | **Acrescentar o caso que faltou** | o post está certo mas incompleto | "Isso vale até {condição}." |
| 3 | **Discordar com respeito** | você acha de verdade que está errado | diga primeiro onde concorda, depois onde diverge |
| 4 | **Estender uma frase** | uma frase do post é a boa | cite e construa em cima |
| 5 | **Fazer a pergunta de verdade** | o post pulou a parte difícil | uma pergunta, específica |
| 6 | **O comprovante** | você já fez o que eles descreveram | o que aconteceu, em duas frases |
| 7 | **A correção** | tem um erro factual | esteja certo, seja breve, gentil e seguro |
| 8 | **O outro ângulo** | fatos certos, enquadramento errado | "Dá pra ler isso de outro jeito:" |
| 9 | **A frase curta** | o post não precisa de nada, você quer marcar presença | menos de 10 palavras, tem que ser engraçado ou verdadeiro |

## Regras

- **De uma a três frases.** Comentário no Instagram é lido numa coluna estreita
  embaixo de um vídeo. Um parágrafo é recolhido atrás do "mais" e ninguém toca.
- **Nunca comece com** "Ótimo post", "Amei", "Verdade demais", "Isso 👏",
  "Precisava ler isso hoje", ou o primeiro nome do criador com exclamação.
  Todos são invisíveis.
- **Nada de comentário só com emoji** e nada de emoji como primeiro caractere.
- **Nunca repita o que o reel disse.** Todo mundo ali acabou de assistir.
- **Uma ideia.** Comentário com dois pontos parece sequestro da conversa.
- **Diga a coisa específica.** Se o comentário serviria em qualquer post sobre
  o assunto, não é comentário, é ruído.
- **Nunca venda, jamais.** Nem a oferta, nem o link, nem "dá uma olhada no meu
  perfil". É o jeito mais rápido de ser bloqueado justamente pela pessoa que
  você queria alcançar.
- **Chegar cedo importa mais aqui que em qualquer lugar.** Um comentário na
  primeira hora de um reel que depois viraliza vai junto com ele.

## Saída

Dê **duas opções de tipos diferentes**, identificadas, mais uma linha dizendo
qual você postaria e por quê. Passe as duas pelo `/ig-human` antes: comentários
são curtos, então um travessão ou uma expressão pronta pesa mais que numa
legenda.

```
OPÇÕES DE COMENTÁRIO  (no reel da @conta sobre preço)

[6 · Comprovante]
A gente subiu 40% em março do ano passado e perdeu exatamente um cliente, que
era o que ocupava metade da caixa de entrada. Levou oito meses pra perder o medo.

[3 · Discordar com respeito]
Concordo com a ancoragem. O que eu questionaria é fazer isso no meio do projeto.
A gente tentou e perdeu uma renovação que estava tranquila.

Posta o primeiro. Ele cede em alguma coisa e tem um número.
```

## Modo lote

Para uma rodada de interação, peça de 5 a 10 posts colados numa mensagem só,
devolva um comentário para cada num único bloco, e mantenha uma anotação em
`instagram/log.md` de quem já foi comentado nesta semana. Comentar nas mesmas
três contas todo dia fica visível e parece exatamente o que é.

## Nunca

Não publique automaticamente, não automatize comentários e não use navegador
para publicar em nome do usuário. Interação automatizada viola os Termos de Uso
do Instagram e faz contas serem bloqueadas. Esta skill escreve o comentário. O
usuário publica.
