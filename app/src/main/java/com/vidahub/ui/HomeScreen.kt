package com.vidahub.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vidahub.data.Category
import com.vidahub.data.LifeEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    state: UiState,
    hasAccess: Boolean,
    onOpenAccessSettings: () -> Unit,
    onFilter: (Category?) -> Unit,
    onQuery: (String) -> Unit,
    onRecategorize: (LifeEvent, Category) -> Unit,
    onDelete: (LifeEvent) -> Unit,
    onDeleteAll: () -> Unit,
    onLoadSamples: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<LifeEvent?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vida Hub", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, "Menu") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Inserir exemplos") }, onClick = { menuOpen = false; onLoadSamples() })
                        DropdownMenuItem(text = { Text("Acesso a notificações") }, onClick = { menuOpen = false; onOpenAccessSettings() })
                        DropdownMenuItem(text = { Text("Apagar tudo") }, onClick = { menuOpen = false; confirmClear = true })
                    }
                },
            )
        },
    ) { padding ->
        val grouped = remember(state.events) { state.events.groupBy { localDate(it.timestamp) } }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!hasAccess) item { AccessCard(onOpenAccessSettings) }
            item { SummaryGrid(state.summary, onFilter) }
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQuery,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Buscar (nome, app, valor...)") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (state.query.isNotEmpty()) IconButton(onClick = { onQuery("") }) { Icon(Icons.Default.Close, "Limpar") }
                    },
                )
            }
            item { FilterRow(state.filter, onFilter) }

            if (state.events.isEmpty()) {
                item { EmptyState(hasAccess, onLoadSamples) }
            }
            grouped.forEach { (date, list) ->
                stickyHeader(key = "h-$date") {
                    Text(
                        dayHeader(date),
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 6.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                items(list, key = { it.id }) { e -> EventRow(e) { selected = e } }
            }
        }
    }

    selected?.let { e ->
        EventDialog(
            event = e,
            onDismiss = { selected = null },
            onRecategorize = { c -> onRecategorize(e, c); selected = null },
            onDelete = { onDelete(e); selected = null },
        )
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Apagar todo o histórico?") },
            text = { Text("Isso remove todos os eventos salvos no aparelho.") },
            confirmButton = { TextButton(onClick = { confirmClear = false; onDeleteAll() }) { Text("Apagar") } },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun AccessCard(onOpen: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ative o acesso às notificações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "O Vida Hub lê as notificações para organizar Pix, compras, entregas e mensagens. " +
                    "Tudo fica salvo só no seu celular — o app nem tem permissão de internet.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(onClick = onOpen) { Text("Conceder acesso") }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SummaryGrid(s: Summary, onFilter: (Category?) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
    ) {
        val m = Modifier.weight(1f)
        StatCard(m, Category.PIX_RECEBIDO, "Recebido no mês", formatMoney(s.pixReceivedMonthCents)) { onFilter(Category.PIX_RECEBIDO) }
        StatCard(m, Category.COMPRA, "Gasto no mês", formatMoney(s.spentMonthCents)) { onFilter(Category.COMPRA) }
        StatCard(m, Category.ENTREGA, "Entregas em andamento", s.activeDeliveries.toString()) { onFilter(Category.ENTREGA) }
        StatCard(m, Category.MENSAGEM, "Mensagens hoje", s.messagesToday.toString()) { onFilter(Category.MENSAGEM) }
    }
}

@Composable
private fun StatCard(modifier: Modifier, c: Category, label: String, value: String, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp)) {
            Text("${c.emoji}  $label", style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = c.color(), maxLines = 1)
        }
    }
}

@Composable
private fun FilterRow(current: Category?, onFilter: (Category?) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = current == null, onClick = { onFilter(null) }, label = { Text("Tudo") })
        Category.entries.forEach { c ->
            FilterChip(
                selected = current == c,
                onClick = { onFilter(if (current == c) null else c) },
                label = { Text("${c.emoji} ${c.label}") },
            )
        }
    }
}

@Composable
private fun EventRow(e: LifeEvent, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(e.category.color().copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) { Text(e.category.emoji, fontSize = 20.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(headline(e), fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(e.text, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    listOfNotNull(e.appName, e.detail, formatTime(e.timestamp)).joinToString(" • "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            e.amountCents?.let { cents ->
                Spacer(Modifier.width(8.dp))
                val sign = when (e.category) {
                    Category.PIX_RECEBIDO -> "+ "
                    Category.COMPRA, Category.PIX_ENVIADO -> "− "
                    else -> ""
                }
                Text(sign + formatMoney(cents), fontWeight = FontWeight.Bold, color = e.category.color())
            }
        }
    }
}

private fun headline(e: LifeEvent): String = when (e.category) {
    Category.PIX_RECEBIDO -> e.counterpart?.let { "Pix de $it" } ?: e.title
    Category.PIX_ENVIADO -> e.counterpart?.let { "Pix para $it" } ?: e.title
    Category.COMPRA -> e.counterpart?.let { "Compra em $it" } ?: e.title
    Category.MENSAGEM -> e.counterpart ?: e.title
    else -> e.title.ifBlank { e.appName }
}

@Composable
private fun EmptyState(hasAccess: Boolean, onLoadSamples: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("📭", fontSize = 40.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            if (hasAccess) "Nada por aqui ainda. Os eventos aparecem conforme as notificações chegam."
            else "Conceda o acesso às notificações para começar.",
            style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = onLoadSamples) { Text("Ver com dados de exemplo") }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun EventDialog(
    event: LifeEvent,
    onDismiss: () -> Unit,
    onRecategorize: (Category) -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${event.category.emoji} ${event.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(event.text)
                event.amountCents?.let { Text("Valor: ${formatMoney(it)}", fontWeight = FontWeight.Bold) }
                Text("${event.appName} • ${dayHeader(localDate(event.timestamp))} ${formatTime(event.timestamp)}", style = MaterialTheme.typography.labelMedium)
                HorizontalDivider()
                Text("Classificou errado? Mude a categoria:", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Category.entries.forEach { c ->
                        FilterChip(selected = c == event.category, onClick = { onRecategorize(c) }, label = { Text("${c.emoji} ${c.label}") })
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } },
        dismissButton = { TextButton(onClick = onDelete) { Text("Excluir") } },
    )
}
