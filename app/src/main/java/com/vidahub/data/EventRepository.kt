package com.vidahub.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/** Banco SQLite local. Nada sai do aparelho. */
private class EventDb(context: Context) : SQLiteOpenHelper(context, "vidahub.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dedupe_key TEXT NOT NULL UNIQUE,
                package_name TEXT NOT NULL,
                app_name TEXT NOT NULL,
                category TEXT NOT NULL,
                title TEXT NOT NULL,
                text TEXT NOT NULL,
                amount_cents INTEGER,
                counterpart TEXT,
                detail TEXT,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_events_time ON events(timestamp DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
}

class EventRepository private constructor(context: Context) {
    private val db = EventDb(context.applicationContext)
    private val version = MutableStateFlow(0L)

    /** Lista reativa com os eventos mais recentes (atualiza a cada inserção). */
    val events: Flow<List<LifeEvent>> = version
        .map { query(limit = 2000) }
        .flowOn(Dispatchers.IO)

    /** Insere o evento; retorna false se for duplicado. */
    fun insert(event: LifeEvent): Boolean {
        val values = ContentValues().apply {
            put("dedupe_key", event.dedupeKey)
            put("package_name", event.packageName)
            put("app_name", event.appName)
            put("category", event.category.name)
            put("title", event.title)
            put("text", event.text)
            put("amount_cents", event.amountCents)
            put("counterpart", event.counterpart)
            put("detail", event.detail)
            put("timestamp", event.timestamp)
        }
        val id = db.writableDatabase.insertWithOnConflict("events", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        if (id != -1L) changed()
        return id != -1L
    }

    fun updateCategory(id: Long, category: Category) {
        val values = ContentValues().apply { put("category", category.name) }
        db.writableDatabase.update("events", values, "id = ?", arrayOf(id.toString()))
        changed()
    }

    fun delete(id: Long) {
        db.writableDatabase.delete("events", "id = ?", arrayOf(id.toString()))
        changed()
    }

    fun deleteAll() {
        db.writableDatabase.delete("events", null, null)
        changed()
    }

    private fun changed() {
        version.value = version.value + 1
    }

    private fun query(limit: Int): List<LifeEvent> =
        db.readableDatabase.query("events", null, null, null, null, null, "timestamp DESC", limit.toString())
            .use { c -> buildList { while (c.moveToNext()) add(c.toEvent()) } }

    private fun Cursor.toEvent() = LifeEvent(
        id = getLong(getColumnIndexOrThrow("id")),
        dedupeKey = getString(getColumnIndexOrThrow("dedupe_key")),
        packageName = getString(getColumnIndexOrThrow("package_name")),
        appName = getString(getColumnIndexOrThrow("app_name")),
        category = Category.fromName(getString(getColumnIndexOrThrow("category"))),
        title = getString(getColumnIndexOrThrow("title")),
        text = getString(getColumnIndexOrThrow("text")),
        amountCents = getColumnIndexOrThrow("amount_cents").let { if (isNull(it)) null else getLong(it) },
        counterpart = getString(getColumnIndexOrThrow("counterpart")),
        detail = getString(getColumnIndexOrThrow("detail")),
        timestamp = getLong(getColumnIndexOrThrow("timestamp")),
    )

    companion object {
        @Volatile private var instance: EventRepository? = null
        fun get(context: Context): EventRepository =
            instance ?: synchronized(this) { instance ?: EventRepository(context).also { instance = it } }
    }
}
