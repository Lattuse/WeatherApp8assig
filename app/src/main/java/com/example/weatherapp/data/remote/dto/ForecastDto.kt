package com.example.weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,

    val current: CurrentDto? = null,
    val daily: DailyDto? = null,

    @SerialName("current_units") val currentUnits: CurrentUnitsDto? = null
)

@Serializable
data class CurrentDto(
    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("relative_humidity_2m") val humidity: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed: Double? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    val time: String? = null
)

@Serializable
data class CurrentUnitsDto(
    @SerialName("temperature_2m") val temperatureUnit: String? = null,
    @SerialName("wind_speed_10m") val windUnit: String? = null
)

@Serializable
data class DailyDto(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m_max") val tempMax: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val tempMin: List<Double> = emptyList(),
    @SerialName("weather_code") val weatherCode: List<Int> = emptyList()
)