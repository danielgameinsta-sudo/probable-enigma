package com.vidahub

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vidahub.ui.HomeScreen
import com.vidahub.ui.MainViewModel
import com.vidahub.ui.VidaHubTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VidaHubTheme {
                val state by vm.state.collectAsStateWithLifecycle()
                val hasAccess by vm.hasAccess.collectAsStateWithLifecycle()
                HomeScreen(
                    state = state,
                    hasAccess = hasAccess,
                    onOpenAccessSettings = {
                        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onFilter = vm::setFilter,
                    onQuery = vm::setQuery,
                    onRecategorize = vm::recategorize,
                    onDelete = vm::delete,
                    onDeleteAll = vm::deleteAll,
                    onLoadSamples = vm::loadSamples,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.hasAccess.value = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }
}
