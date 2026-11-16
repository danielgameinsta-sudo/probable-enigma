package com.vidahub.data

/** Categorias de "coisas da vida" detectadas a partir das notificações. */
enum class Category(val label: String, val emoji: String) {
    PIX_RECEBIDO("Pix recebido", "💰"),
    PIX_ENVIADO("Pix enviado", "💸"),
    COMPRA("Compras", "🛒"),
    CONTA("Contas", "🧾"),
    ENTREGA("Entregas", "📦"),
    MENSAGEM("Mensagens", "💬"),
    OUTRO("Outros", "🔔");

    companion object {
        fun fromName(name: String?): Category = entries.firstOrNull { it.name == name } ?: OUTRO
    }
}

/** Um evento extraído de uma notificação. */
data class LifeEvent(
    val id: Long = 0,
    val dedupeKey: String,
    val packageName: String,
    val appName: String,
    val category: Category,
    val title: String,
    val text: String,
    /** Valor em centavos, quando a notificação menciona "R$". */
    val amountCents: Long? = null,
    /** Quem enviou / loja / remetente. */
    val counterpart: String? = null,
    /** Status extra, ex.: "Saiu para entrega". */
    val detail: String? = null,
    val timestamp: Long,
)
