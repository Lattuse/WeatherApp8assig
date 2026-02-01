package com.example.weatherapp.domain.model

data class WeatherInfo(
    val city: String,
    val updatedAt: String,
    val temperature: Double,
    val tempUnit: String,
    val conditionText: String,
    val minTemp: Double,
    val maxTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windUnit: String,
    val isOffline: Boolean,
    val forecastDays: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val min: Double,
    val max: Double,
    val conditionText: String
)
