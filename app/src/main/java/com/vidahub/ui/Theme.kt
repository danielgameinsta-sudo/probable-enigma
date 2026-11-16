package com.vidahub.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.vidahub.data.Category

@Composable
fun VidaHubTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val ctx = LocalContext.current
    val scheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> darkColorScheme(primary = Color(0xFF9F9CFF))
        else -> lightColorScheme(primary = Color(0xFF3F3D9E))
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

fun Category.color(): Color = when (this) {
    Category.PIX_RECEBIDO -> Color(0xFF1E9E6A)
    Category.PIX_ENVIADO -> Color(0xFFE0703A)
    Category.COMPRA -> Color(0xFFD64545)
    Category.CONTA -> Color(0xFFB8860B)
    Category.ENTREGA -> Color(0xFF3B7DD8)
    Category.MENSAGEM -> Color(0xFF8E5BD0)
    Category.OUTRO -> Color(0xFF7A7A85)
}
