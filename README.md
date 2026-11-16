# Vida Hub 📱

Aplicativo Android que funciona como um **gerenciador geral da sua vida a partir das notificações**.
Ele lê as notificações que chegam no celular e organiza automaticamente:

| Categoria | Exemplos detectados |
|---|---|
| 💰 Pix recebido | "Você recebeu um Pix de R$ 250,00 de Maria" |
| 💸 Pix enviado | "Pix de R$ 45,00 enviado para João" |
| 🛒 Compras | "Compra de R$ 89,90 aprovada em Mercado Extra" |
| 🧾 Contas | "Sua fatura de R$ 1.432,18 vence amanhã" |
| 📦 Entregas | "Seu pedido saiu para entrega", "Objeto entregue" |
| 💬 Mensagens | WhatsApp, Telegram, Instagram, SMS... |
| 🔔 Outros | Todo o resto |

Na tela inicial aparecem: total recebido no mês, total gasto no mês, entregas em andamento e mensagens do dia,
além de uma linha do tempo por dia, busca e filtros por categoria. Tocando num evento dá para corrigir a categoria
ou excluí-lo.

## Privacidade

- **Sem permissão de internet**: nada sai do aparelho; tudo fica num banco SQLite local.
- **Códigos de verificação/tokens são mascarados** (`••••••`) antes de serem salvos.
- `allowBackup=false`: os dados não vão para o backup na nuvem.

## Como funciona

- `service/NotificationCaptureService.kt` — um `NotificationListenerService` que recebe cada notificação
  (ignora notificações fixas como música/downloads e resumos de grupo).
- `classifier/NotificationClassifier.kt` — regras em português que identificam a categoria, extraem o valor
  em R$, quem enviou/recebeu, a loja e o status da entrega. Também reconhece apps de bancos, lojas e mensageiros.
- `data/EventRepository.kt` — armazenamento local com remoção de duplicados.
- `ui/` — interface em Jetpack Compose (Material 3, tema dinâmico e modo escuro).

> ⚠️ Só funciona no **Android**. O iOS não permite que um app leia as notificações de outros apps.

## Como compilar e instalar

Requisitos: Android Studio (ou JDK 17+ e Android SDK 35).

```bash
./gradlew assembleDebug          # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest      # roda os testes do classificador
```

Instale o APK no celular, abra o app e toque em **Conceder acesso** → ative o *Vida Hub* em
"Acesso a notificações". No menu (⋮) há a opção **Inserir exemplos** para ver o app funcionando na hora.

## Ideias para próximas versões

- Lista de apps ignorados / escolher quais apps monitorar
- Agrupar atualizações da mesma entrega por código de rastreio
- Gráficos de gastos por mês e por loja
- Alertas (ex.: "fatura vence amanhã") e widget na tela inicial
- Exportar para planilha (CSV)
