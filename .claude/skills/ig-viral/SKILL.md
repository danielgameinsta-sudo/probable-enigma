---
name: ig-viral
description: >-
  Vai atrás dos reels que estão funcionando de verdade agora no nicho do
  usuário, ranqueia por quanto cada um superou a própria conta, dá nome à
  fórmula de gancho de cada um e transforma isso num arquivo de referências
  para gravar. Use quando o usuário disser "acha vídeos virais", "o que está
  funcionando agora", "o que estão postando no meu nicho", "analisa essa
  conta", "monta um swipe file", "por que esse reel bombou", ou perguntar o que
  fazer em seguida sem ter nenhuma prova para responder.
---

# ig-viral

**Idioma:** escreva os relatórios e as versões do usuário em português do
Brasil. Os ganchos coletados ficam nas palavras originais de quem postou.

A skill de pesquisa. Todo o resto do pacote escreve; esta vai olhar. Rode uma
vez por mês, não todo dia. Fórmulas duram uma temporada.

Uma ferramenta fica nesta pasta e ela funciona:

```bash
python3 swipe.py coletados.tsv --out instagram/swipe.md
```

## A ideia que faz isso valer a pena

**Views brutas não são prova.** Uma conta com dois milhões de seguidores
fazendo 400 mil views teve uma terça-feira fraca. Uma conta com quatro mil
seguidores fazendo 400 mil views achou alguma coisa, e essa coisa dá para
copiar.

Então tudo aqui é ranqueado pelo **múltiplo**: views divididas pela mediana
recente da própria conta. Acima de 3x é sinal. Abaixo de 1,5x é o dia normal
daquela conta e não ensina nada, por maior que o número pareça.

Colete contas **até umas 10x o tamanho do usuário**. Uma fórmula que funciona
com 2 milhões de seguidores muitas vezes funciona porque a conta tem 2 milhões.

## Passo 1: escolha as contas

Peça ao usuário de 6 a 12 contas, ou proponha e peça aprovação:

- **4 diretas** - mesmo nicho, mesma oferta, um pouco à frente.
- **4 vizinhas** - outro nicho, mesmo público. É daqui que formatos são
  emprestados antes de alguém do nicho ter.
- **2 a 4 gigantes** - contas muito maiores, só para formato, nunca para
  frequência ou tom.

Peça também para ele abrir a própria **coleção de Salvos**. É o material mais
rápido e relevante que existe e já vem filtrado pelo gosto dele.

## Passo 2: vá olhar

Use a ferramenta de navegação que esta sessão realmente tiver: um navegador no
app, uma extensão conectada ao Chrome do próprio usuário, ou uma ferramenta de
uso do computador. Não existe API para isso e nem precisa, porque o volume é
pequeno o bastante para ler.

**Regras inegociáveis:**

- **Nunca faça login no Instagram em nome do usuário e nunca peça senha.** Se
  uma página exige login, quem já está logado é o usuário. Use o navegador dele
  com ele presente, ou peça para ele colar.
- **Isto é leitura, não raspagem.** Dez contas, uma dúzia de reels cada, em
  velocidade humana. Coleta automática em volume viola os Termos de Uso do
  Instagram e faz contas serem bloqueadas. Não construa um robô, não use
  serviço de scraping, e não deixe isso rodando em segundo plano.
- **Copie a fórmula, nunca o vídeo.** O formato do gancho, a estrutura, a
  duração, o padrão de cortes. Não o roteiro, não a voz, não a edição. Atribua
  cada linha do arquivo à conta de onde ela veio.

**O que anotar de cada reel**, nas palavras do criador:

| campo | observações |
| --- | --- |
| conta | o @ |
| seguidores | do perfil |
| mediana | olhe os últimos 12 reels e pegue a contagem do meio |
| views | deste reel |
| gancho | a primeira frase, falada ou na tela, literal, com erro e tudo |
| na tela | a primeira cartela de texto, se for diferente |
| duração | segundos |
| pedido | o que pediram no final |

A mediana é a mais importante. Sem ela você volta a ranquear por seguidores,
que é exatamente o que esta skill existe para evitar.

**Quando o Instagram não mostrar o suficiente:** a mesma gramática de gancho
roda no YouTube Shorts, onde as views e as transcrições são públicas e não
precisa de login. É um segundo material legítimo, e o gancho falado é mais
fácil de pegar:

```bash
# views dos shorts de um canal
python3 -m yt_dlp --flat-playlist --playlist-end 40 -J \
  "https://www.youtube.com/@CANAL/shorts" > canal.json

# a primeira frase falada de um short, pelas legendas automáticas
python3 -m yt_dlp --skip-download --write-auto-subs --sub-langs "pt.*" \
  --sub-format json3 -o gancho "https://www.youtube.com/watch?v=ID_DO_VIDEO"
```

Pegue todas as palavras da legenda com marcação abaixo de 3,0 segundos. Esse é
o gancho como foi dito, não como foi escrito.

## Passo 3: ranqueie

Preencha um arquivo separado por tabulação com uma linha de cabeçalho (em
português ou inglês) e rode o script:

```
conta	seguidores	mediana	views	gancho
@alguem	48000	11000	412000	ninguém te conta que os seus primeiros 30 reels vão flopar
```

```bash
python3 swipe.py coletados.tsv --out instagram/swipe.md
```

Ele calcula o múltiplo, dá nome à fórmula de gancho usando as mesmas 26
fórmulas do `/ig-reel`, dá nota a cada gancho com o `hookscore.py`, e mostra o
que separa o terço de cima do terço de baixo.

## Passo 4: diga o que significa, com cuidado

Relate três coisas e mais nada:

1. **Quais fórmulas aparecem demais** no terço de cima, com contagem. Duas
   fórmulas aparecendo quatro vezes cada em seis contas é uma descoberta. Uma
   aparecendo duas vezes não é.
2. **O que o terço de cima tem em comum na estrutura** e o de baixo não tem:
   tamanho do gancho, se a entrega é visual, se o primeiro quadro se mexe, onde
   fica o pedido.
3. **As linhas sem classificação.** Todo gancho que o classificador não
   conseguiu nomear é ruído ou uma fórmula que ainda não está no `hooks.json`.
   Leia na mão. Essa é a coluna mais valiosa da saída, e é por isso que o
   script mostra a contagem.

Depois diga o tamanho da amostra e o grau de confiança com palavras simples.
Quarenta reels de seis contas sustentam uma afirmação. Doze não, e dizer isso é
a diferença entre pesquisa e horóscopo.

## Passo 5: transforme em algo para gravar

Para as três fórmulas de cima, escreva **a versão do usuário**: a história
dele, o número dele, no formato que está funcionando. Passe cada uma para o
`/ig-reel` com o número da fórmula já escolhido.

Nunca devolva "faz um reel igual a esse". Devolva uma frase de gancho que ele
poderia falar amanhã.

## Saída

```
REFERÊNCIAS  ·  38 reels  ·  7 contas  ·  base: mediana da conta

FORA DA CURVA (acima de 3x)
  38.4x  gancho 86  #3  Ninguém Te Conta   @conta_a    412.000  (mediana 10.700)
  11.2x  gancho 79  #9  Pode Roubar        @conta_c    180.000  (mediana 16.100)
  ...

O QUE ESTÁ APARECENDO DEMAIS
  #3 Ninguém Te Conta   x5 no terço de cima, 0 no de baixo
  #2 Ordem Negativa     x4
  tamanho mediano       8 palavras em cima, 19 embaixo

SEM CLASSIFICAÇÃO (6)
  Dois desses têm o mesmo formato e ele não está no hooks.json: um gancho que
  começa lendo em voz alta o comentário de outra pessoa. Vale acrescentar.

A SUA VERSÃO
  #3  "Ninguém te conta que as primeiras 20 propostas são pra perder mesmo."
  ...
```

Grave o arquivo de referências em `instagram/swipe.md`. O `/ig-reel` e o
`/ig-plan` leem esse arquivo, e é esse o objetivo: depois de rodar uma vez, o
resto do pacote trabalha com as provas do próprio usuário em vez de padrões.

Esta skill não publica, não segue, não curte e não manda mensagem. Ela lê.
