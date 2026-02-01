package com.example.weatherapp.ui

import com.example.weatherapp.domain.model.WeatherInfo

data class WeatherUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val weather: WeatherInfo? = null,
    val unit: String = "celsius", // "celsius" / "fahrenheit"
    val history: List<String> = emptyList()
)