package com.vidahub.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.vidahub.classifier.NotificationClassifier
import com.vidahub.data.EventRepository
import com.vidahub.data.LifeEvent
import java.security.MessageDigest
import java.util.concurrent.Executors

/**
 * Recebe todas as notificações do aparelho (após o usuário conceder
 * "Acesso a notificações") e transforma em eventos da vida.
 */
class NotificationCaptureService : NotificationListenerService() {

    private val worker = Executors.newSingleThreadExecutor()
    private val repo by lazy { EventRepository.get(this) }

    override fun onListenerConnected() {
        // Aproveita as notificações que já estão na barra quando o serviço conecta.
        val current = runCatching { activeNotifications }.getOrNull() ?: return
        current.forEach { handle(it) }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) = handle(sbn)

    private fun handle(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val n = sbn.notification ?: return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        if (n.flags and Notification.FLAG_ONGOING_EVENT != 0) return // música, downloads, navegação...
        if (n.category == Notification.CATEGORY_PROGRESS || n.category == Notification.CATEGORY_TRANSPORT) return

        val extras = n.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()?.trim().orEmpty()
        val text = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
            ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString()?.trim().orEmpty()
        if (title.isBlank() && text.isBlank()) return

        val pkg = sbn.packageName
        val postTime = sbn.postTime
        val key = sbn.key
        worker.execute {
            val result = NotificationClassifier.classify(pkg, title, text)
            repo.insert(
                LifeEvent(
                    dedupeKey = sha1("$key|$title|${result.safeText}"),
                    packageName = pkg,
                    appName = appLabel(pkg),
                    category = result.category,
                    title = title,
                    text = result.safeText,
                    amountCents = result.amountCents,
                    counterpart = result.counterpart,
                    detail = result.detail,
                    timestamp = postTime,
                )
            )
        }
    }

    private fun appLabel(pkg: String): String = runCatching {
        val pm = packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    }.getOrDefault(pkg)

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }

    private fun sha1(s: String): String =
        MessageDigest.getInstance("SHA-1").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }
}
