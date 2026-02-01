package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weather_prefs")

class AppDataStore(private val context: Context) {

    private object Keys {
        val UNIT = stringPreferencesKey("unit") // "celsius" / "fahrenheit"
        val CACHED_CITY = stringPreferencesKey("cached_city")
        val CACHED_JSON = stringPreferencesKey("cached_json")
        val CACHED_AT = longPreferencesKey("cached_at")
        val HISTORY = stringPreferencesKey("history")
    }

    val unitFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.UNIT] ?: "celsius"
    }

    suspend fun setUnit(unit: String) {
        context.dataStore.edit { it[Keys.UNIT] = unit }
    }

    fun cachedJsonFlow(): Flow<String?> = context.dataStore.data.map { it[Keys.CACHED_JSON] }

    suspend fun saveCache(city: String, json: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CACHED_CITY] = city
            prefs[Keys.CACHED_JSON] = json
            prefs[Keys.CACHED_AT] = System.currentTimeMillis()
        }
    }

    val historyFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.HISTORY].orEmpty()
        if (raw.isBlank()) emptyList() else raw.split("|").filter { it.isNotBlank() }
    }

    suspend fun addToHistory(city: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.HISTORY].orEmpty()
            val items = if (current.isBlank()) emptyList() else current.split("|")
            val updated = (listOf(city) + items.filter { it.equals(city, ignoreCase = true).not() })
                .take(5)
            prefs[Keys.HISTORY] = updated.joinToString("|")
        }
    }
}