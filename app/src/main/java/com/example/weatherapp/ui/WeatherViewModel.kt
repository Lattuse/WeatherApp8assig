package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.repository.WeatherRepository
import java.io.IOException
import java.net.SocketTimeoutException

class WeatherViewModel(
    private val repo: WeatherRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.unitFlow.collect { unit ->
                _state.update { it.copy(unit = unit) }
            }
        }
        viewModelScope.launch {
            dataStore.historyFlow.collect { list ->
                _state.update { it.copy(history = list) }
            }
        }
        // При запуске попробуем показать кэш сразу
        viewModelScope.launch {
            val cached = repo.readCachedWeather()
            if (cached != null) {
                _state.update { it.copy(weather = cached, error = null) }
            }
        }
    }

    fun onQueryChange(newValue: String) {
        _state.update { it.copy(query = newValue) }
    }

    fun search(cityOverride: String? = null) {
        val city = (cityOverride ?: state.value.query).trim()
        if (city.isBlank()) {
            _state.update { it.copy(error = "City name is empty.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val weather = repo.fetchWeatherOnline(city, state.value.unit)
                _state.update { it.copy(isLoading = false, weather = weather, error = null) }
            } catch (e: IllegalArgumentException) {
                val msg = when (e.message) {
                    "EMPTY_INPUT" -> "City name is empty."
                    "CITY_NOT_FOUND" -> "City not found."
                    else -> "Invalid input."
                }
                fallbackToCache(msg)
            } catch (e: SocketTimeoutException) {
                fallbackToCache("Network timeout.")
            } catch (e: IOException) {
                fallbackToCache("No internet connection.")
            } catch (e: Exception) {
                fallbackToCache("API error.")
            }
        }
    }

    private suspend fun fallbackToCache(baseMessage: String) {
        val cached = repo.readCachedWeather()
        if (cached != null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    weather = cached.copy(isOffline = true),
                    error = "$baseMessage Showing cached data."
                )
            }
        } else {
            _state.update {
                it.copy(isLoading = false, error = "$baseMessage No cached data available.")
            }
        }
    }

    fun toggleUnit() {
        viewModelScope.launch {
            val newUnit = if (state.value.unit == "celsius") "fahrenheit" else "celsius"
            dataStore.setUnit(newUnit)

            if (state.value.query.isNotBlank()) search()
        }
    }
}