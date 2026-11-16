package com.vidahub.ui

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val BR = Locale("pt", "BR")
private val currency = NumberFormat.getCurrencyInstance(BR)
private val timeFmt = DateTimeFormatter.ofPattern("HH:mm", BR)
private val dayFmt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", BR)

fun formatMoney(cents: Long): String = currency.format(cents / 100.0)

fun formatTime(ts: Long): String = timeFmt.format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))

fun localDate(ts: Long): LocalDate = Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDate()

fun dayHeader(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Hoje"
        today.minusDays(1) -> "Ontem"
        else -> dayFmt.format(date).replaceFirstChar { it.uppercase() }
    }
}
