package com.vidahub.classifier

import com.vidahub.data.Category
import java.text.Normalizer

/** Resultado da classificação de uma notificação. */
data class Classification(
    val category: Category,
    val amountCents: Long? = null,
    val counterpart: String? = null,
    val detail: String? = null,
    /** Texto já higienizado (códigos de verificação mascarados). */
    val safeText: String,
)

/**
 * Entende notificações em português do Brasil usando regras simples.
 * Não usa internet: tudo é processado localmente.
 */
object NotificationClassifier {

    private val MESSAGING_APPS = setOf(
        "com.whatsapp", "com.whatsapp.w4b", "org.telegram.messenger", "org.thoughtcrime.securesms",
        "com.instagram.android", "com.facebook.orca", "com.google.android.apps.messaging",
        "com.samsung.android.messaging", "com.discord", "com.microsoft.teams", "com.Slack",
    )

    private val BANK_APPS = setOf(
        "com.nu.production", "br.com.intermedium", "com.itau", "br.com.itau", "com.bradesco",
        "br.com.bb.android", "com.santander.app", "com.picpay", "com.mercadopago.wallet",
        "br.com.c6bank.app", "com.c6bank.app", "br.com.gabba.Caixa", "com.btg.pactual.banking",
        "br.com.original.bank", "com.neon", "br.com.uol.ps.myaccount", "com.next",
    )

    private val DELIVERY_APPS = setOf(
        "br.com.brainweb.ifood", "com.mercadolibre", "com.amazon.mShop.android.shopping",
        "com.shopee.br", "br.com.correios.srocorreios", "com.alibaba.aliexpresshd",
        "com.contextlogic.wish", "com.einnovation.temu", "com.zzkko", "com.magazineluiza.app",
        "com.rappi.android", "com.ubercab.eats", "com.loggi.client",
    )

    private val MONEY = Regex(
        """R\$\s*(\d{1,3}(?:\.\d{3})+(?:,\d{1,2})?|\d+(?:,\d{1,2})?)"""
    )
    private val OTP = Regex("""(?<!\d)\d{4,8}(?!\d)""")
    private val FROM_NAME = Regex(
        """\b(?:de|da|do|por)\s+([A-ZÀ-Ý][\p{L}'.]+(?:\s+(?:d[aeo]s?\s+)?[A-ZÀ-Ý][\p{L}'.]+){0,4})"""
    )
    private val TO_NAME = Regex(
        """\b(?:para|pra)\s+([A-ZÀ-Ý][\p{L}'.]+(?:\s+(?:d[aeo]s?\s+)?[A-ZÀ-Ý][\p{L}'.]+){0,4})"""
    )
    private val STORE_NAME = Regex(
        """\b(?:em|no|na)\s+([A-ZÀ-Ý0-9*][\p{L}0-9*'&.\- ]{1,30}?)(?=\s+(?:no valor|de R\$|R\$|foi|aprovad)|[.,!]|$)"""
    )

    private val DELIVERY_STATUS = listOf(
        "saiu para entrega" to "Saiu para entrega",
        "saiu pra entrega" to "Saiu para entrega",
        "foi entregue" to "Entregue",
        "pedido entregue" to "Entregue",
        "entregue" to "Entregue",
        "chega hoje" to "Chega hoje",
        "chegara hoje" to "Chega hoje",
        "a caminho" to "A caminho",
        "em transito" to "Em trânsito",
        "objeto postado" to "Postado",
        "foi postado" to "Postado",
        "pedido enviado" to "Enviado",
        "foi enviado" to "Enviado",
        "aguardando retirada" to "Aguardando retirada",
        "disponivel para retirada" to "Aguardando retirada",
        "tentativa de entrega" to "Tentativa de entrega",
        "pedido confirmado" to "Pedido confirmado",
        "preparando" to "Preparando",
    )

    fun classify(packageName: String, title: String, text: String): Classification {
        val full = "$title. $text"
        val n = normalize(full)

        // Códigos de verificação nunca são guardados em claro.
        if (isVerificationCode(n)) {
            return Classification(
                category = Category.OUTRO,
                detail = "Código de verificação",
                safeText = OTP.replace(text) { "•".repeat(it.value.length) },
            )
        }

        val amount = parseAmount(full)

        if (packageName in MESSAGING_APPS) {
            return Classification(
                category = Category.MENSAGEM,
                counterpart = title.takeIf { it.isNotBlank() },
                safeText = text,
            )
        }

        val isPix = "pix" in n || "transferencia" in n || "ted " in n
        if (isPix && containsAny(n, "recebeu", "recebido", "recebida", "caiu", "entrou", "chegou um", "voce ganhou")) {
            return Classification(Category.PIX_RECEBIDO, amount, FROM_NAME.find(full)?.groupValues?.get(1)?.cleanName(), safeText = text)
        }
        if (containsAny(n, "voce recebeu", "transferencia recebida", "deposito recebido", "dinheiro na conta")) {
            return Classification(Category.PIX_RECEBIDO, amount, FROM_NAME.find(full)?.groupValues?.get(1)?.cleanName(), safeText = text)
        }
        if (isPix && containsAny(n, "enviado", "enviou", "realizado", "efetuado", "transferiu", "voce pagou", "agendado", "feito com sucesso")) {
            return Classification(Category.PIX_ENVIADO, amount, TO_NAME.find(full)?.groupValues?.get(1)?.cleanName(), safeText = text)
        }

        if (containsAny(
                n, "compra aprovada", "compra realizada", "compra no cartao", "compra no credito",
                "compra no debito", "voce comprou", "pagamento aprovado", "compra de r$", "compra autorizada",
                "transacao aprovada", "compra efetuada",
            )
        ) {
            return Classification(Category.COMPRA, amount, STORE_NAME.find(full)?.groupValues?.get(1)?.cleanName(), safeText = text)
        }

        if (containsAny(
                n, "fatura", "boleto", "vencimento", "vence hoje", "vence amanha", "conta de luz",
                "conta de agua", "conta de gas", "debito automatico", "mensalidade", "pagamento pendente",
            )
        ) {
            return Classification(Category.CONTA, amount, detail = billDetail(n), safeText = text)
        }

        val deliveryStatus = DELIVERY_STATUS.firstOrNull { it.first in n }?.second
        val looksLikeDelivery = deliveryStatus != null && (
            packageName in DELIVERY_APPS || containsAny(n, "pedido", "encomenda", "pacote", "objeto", "rastreio", "rastreamento", "entrega")
            )
        if (looksLikeDelivery || (packageName in DELIVERY_APPS && containsAny(n, "entrega", "rastreio", "pedido"))) {
            return Classification(Category.ENTREGA, amount, detail = deliveryStatus ?: "Atualização", safeText = text)
        }

        if (packageName in BANK_APPS && amount != null && containsAny(n, "compra", "debito", "pagamento")) {
            return Classification(Category.COMPRA, amount, STORE_NAME.find(full)?.groupValues?.get(1)?.cleanName(), safeText = text)
        }

        return Classification(Category.OUTRO, amount, safeText = text)
    }

    /** Converte o primeiro "R$ 1.234,56" encontrado em centavos. */
    fun parseAmount(text: String): Long? {
        val raw = MONEY.find(text)?.groupValues?.get(1) ?: return null
        val parts = raw.replace(".", "").split(",")
        val reais = parts[0].toLongOrNull() ?: return null
        val cents = parts.getOrNull(1)?.padEnd(2, '0')?.take(2)?.toLongOrNull() ?: 0
        return reais * 100 + cents
    }

    private fun billDetail(n: String): String? = when {
        "vence hoje" in n -> "Vence hoje"
        "vence amanha" in n -> "Vence amanhã"
        "fatura fechou" in n || "fatura fechada" in n -> "Fatura fechada"
        "paga" in n && "fatura" in n -> "Fatura"
        else -> null
    }

    private fun isVerificationCode(n: String): Boolean =
        containsAny(n, "codigo de verificacao", "codigo de seguranca", "seu codigo", "codigo de acesso", "verification code", "nao compartilhe", "token") &&
            OTP.containsMatchIn(n)

    private fun containsAny(text: String, vararg terms: String) = terms.any { it in text }

    fun normalize(s: String): String =
        Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")
}

private fun String.cleanName(): String = trim().trimEnd('.', ',', '!', ';', ':', ' ')
