package com.vidahub.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vidahub.data.Category
import com.vidahub.data.EventRepository
import com.vidahub.data.LifeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class Summary(
    val pixReceivedMonthCents: Long = 0,
    val spentMonthCents: Long = 0,
    val activeDeliveries: Int = 0,
    val messagesToday: Int = 0,
    val billsPending: Int = 0,
)

data class UiState(
    val events: List<LifeEvent> = emptyList(),
    val summary: Summary = Summary(),
    val filter: Category? = null,
    val query: String = "",
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EventRepository.get(app)

    val hasAccess = MutableStateFlow(false)
    private val filter = MutableStateFlow<Category?>(null)
    private val query = MutableStateFlow("")

    val state: StateFlow<UiState> = combine(repo.events, filter, query) { all, f, q ->
        val needle = q.trim().lowercase()
        val visible = all.filter { e ->
            (f == null || e.category == f) &&
                (needle.isEmpty() || listOfNotNull(e.title, e.text, e.appName, e.counterpart)
                    .any { it.lowercase().contains(needle) })
        }
        UiState(visible, summarize(all), f, q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun setFilter(c: Category?) { filter.value = c }
    fun setQuery(q: String) { query.value = q }

    fun recategorize(e: LifeEvent, c: Category) = io { repo.updateCategory(e.id, c) }
    fun delete(e: LifeEvent) = io { repo.delete(e.id) }
    fun deleteAll() = io { repo.deleteAll() }
    fun loadSamples() = io { SampleData.events().forEach(repo::insert) }

    private fun io(block: () -> Unit) { viewModelScope.launch(Dispatchers.IO) { block() } }

    private fun summarize(all: List<LifeEvent>): Summary {
        val today = LocalDate.now()
        val monthStart = today.withDayOfMonth(1)
        val month = all.filter { !localDate(it.timestamp).isBefore(monthStart) }
        // Entrega "ativa": última atualização de cada app nos últimos 10 dias que ainda não foi entregue.
        val recentDeliveries = all.filter {
            it.category == Category.ENTREGA && !localDate(it.timestamp).isBefore(today.minusDays(10))
        }
        val active = recentDeliveries
            .groupBy { it.packageName + "|" + it.title }
            .values.count { list -> list.maxBy { it.timestamp }.detail != "Entregue" }
        return Summary(
            pixReceivedMonthCents = month.filter { it.category == Category.PIX_RECEBIDO }.sumOf { it.amountCents ?: 0 },
            spentMonthCents = month.filter { it.category == Category.COMPRA || it.category == Category.PIX_ENVIADO }
                .sumOf { it.amountCents ?: 0 },
            activeDeliveries = active,
            messagesToday = all.count { it.category == Category.MENSAGEM && localDate(it.timestamp) == today },
            billsPending = month.count { it.category == Category.CONTA },
        )
    }
}
