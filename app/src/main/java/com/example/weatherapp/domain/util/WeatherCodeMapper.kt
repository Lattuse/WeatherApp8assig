package com.example.weatherapp.domain.util

fun weatherCodeToText(code: Int?): String {
    return when (code) {
        0 -> "Clear"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm (hail)"
        else -> "Unknown"
    }
}