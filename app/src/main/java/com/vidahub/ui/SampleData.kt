package com.vidahub.ui

import com.vidahub.classifier.NotificationClassifier
import com.vidahub.data.LifeEvent

/** Exemplos para testar o app sem esperar notificações reais. */
object SampleData {
    private val samples = listOf(
        Triple("com.nu.production", "Nubank", "Pix recebido" to "Você recebeu um Pix de R$ 250,00 de Maria Souza."),
        Triple("com.nu.production", "Nubank", "Compra aprovada" to "Compra de R$ 89,90 aprovada em Mercado Extra."),
        Triple("br.com.intermedium", "Inter", "Pix enviado" to "Pix de R$ 45,00 enviado para João Pereira."),
        Triple("com.mercadolibre", "Mercado Livre", "Seu pacote está a caminho" to "Seu pedido de Fone Bluetooth saiu para entrega hoje."),
        Triple("br.com.correios.srocorreios", "Correios", "Objeto entregue" to "Seu objeto QB123456789BR foi entregue ao destinatário."),
        Triple("com.whatsapp", "WhatsApp", "Mãe" to "Filho, vem almoçar domingo?"),
        Triple("com.itau", "Itaú", "Fatura disponível" to "Sua fatura de R$ 1.432,18 vence amanhã."),
        Triple("br.com.brainweb.ifood", "iFood", "Pedido a caminho" to "O entregador está a caminho com seu pedido!"),
        Triple("com.picpay", "PicPay", "Dinheiro na conta!" to "Você recebeu R$ 1.200,00 de Empresa XYZ LTDA."),
    )

    fun events(): List<LifeEvent> {
        val now = System.currentTimeMillis()
        return samples.mapIndexed { i, (pkg, app, msg) ->
            val (title, text) = msg
            val c = NotificationClassifier.classify(pkg, title, text)
            LifeEvent(
                dedupeKey = "sample-$i-$now",
                packageName = pkg,
                appName = app,
                category = c.category,
                title = title,
                text = c.safeText,
                amountCents = c.amountCents,
                counterpart = c.counterpart,
                detail = c.detail,
                timestamp = now - i * 47L * 60_000,
            )
        }
    }
}
