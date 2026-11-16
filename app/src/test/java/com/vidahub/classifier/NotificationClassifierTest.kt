package com.vidahub.classifier

import com.vidahub.data.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class NotificationClassifierTest {
    private fun c(pkg: String, title: String, text: String) = NotificationClassifier.classify(pkg, title, text)

    @Test fun pixRecebidoNubank() {
        val r = c("com.nu.production", "Pix recebido", "Você recebeu um Pix de R$ 250,00 de Maria Souza.")
        assertEquals(Category.PIX_RECEBIDO, r.category)
        assertEquals(25000L, r.amountCents)
        assertEquals("Maria Souza", r.counterpart)
    }

    @Test fun pixRecebidoValorComMilhar() {
        val r = c("br.com.intermedium", "Transferência recebida", "Você recebeu R$ 1.200,50 de João da Silva")
        assertEquals(Category.PIX_RECEBIDO, r.category)
        assertEquals(120050L, r.amountCents)
        assertEquals("João da Silva", r.counterpart)
    }

    @Test fun pixEnviado() {
        val r = c("br.com.intermedium", "Pix enviado", "Pix de R$ 45,00 enviado para João Pereira.")
        assertEquals(Category.PIX_ENVIADO, r.category)
        assertEquals(4500L, r.amountCents)
        assertEquals("João Pereira", r.counterpart)
    }

    @Test fun compraAprovada() {
        val r = c("com.nu.production", "Compra aprovada", "Compra de R$ 89,90 aprovada em Mercado Extra.")
        assertEquals(Category.COMPRA, r.category)
        assertEquals(8990L, r.amountCents)
        assertEquals("Mercado Extra", r.counterpart)
    }

    @Test fun compraNoCartaoNaoViraConta() {
        val r = c("com.itau", "Itaú", "Compra no cartão final 1234 de R$ 30,00 em PADARIA. Será lançada na fatura.")
        assertEquals(Category.COMPRA, r.category)
    }

    @Test fun fatura() {
        val r = c("com.itau", "Fatura disponível", "Sua fatura de R$ 1.432,18 vence amanhã.")
        assertEquals(Category.CONTA, r.category)
        assertEquals(143218L, r.amountCents)
        assertEquals("Vence amanhã", r.detail)
    }

    @Test fun entregaMercadoLivre() {
        val r = c("com.mercadolibre", "Seu pacote está a caminho", "Seu pedido saiu para entrega hoje.")
        assertEquals(Category.ENTREGA, r.category)
        assertEquals("Saiu para entrega", r.detail)
    }

    @Test fun entregaCorreiosEntregue() {
        val r = c("br.com.correios.srocorreios", "Objeto entregue", "Seu objeto QB123456789BR foi entregue ao destinatário.")
        assertEquals(Category.ENTREGA, r.category)
        assertEquals("Entregue", r.detail)
    }

    @Test fun entregaDeAppDesconhecido() {
        val r = c("com.loja.qualquer", "Atualização do pedido", "Sua encomenda está em trânsito.")
        assertEquals(Category.ENTREGA, r.category)
        assertEquals("Em trânsito", r.detail)
    }

    @Test fun mensagemWhatsAppMesmoFalandoDePix() {
        val r = c("com.whatsapp", "Mãe", "Te mandei um pix de R$ 50,00")
        assertEquals(Category.MENSAGEM, r.category)
        assertEquals("Mãe", r.counterpart)
    }

    @Test fun codigoDeVerificacaoEMascarado() {
        val r = c("com.google.android.apps.messaging", "Banco", "Seu código de verificação é 482913. Não compartilhe.")
        assertEquals(Category.OUTRO, r.category)
        assertFalse(r.safeText.contains("482913"))
    }

    @Test fun outro() {
        assertEquals(Category.OUTRO, c("com.android.vending", "Atualizações", "3 apps atualizados").category)
    }

    @Test fun parseAmount() {
        assertEquals(500L, NotificationClassifier.parseAmount("R$5"))
        assertEquals(1234567L, NotificationClassifier.parseAmount("R$ 12.345,67"))
        assertEquals(990L, NotificationClassifier.parseAmount("valor R$ 9,9"))
    }
}
