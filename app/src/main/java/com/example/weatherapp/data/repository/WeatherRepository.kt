package com.example.weatherapp.data.repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.remote.OpenMeteoForecastApi
import com.example.weatherapp.data.remote.OpenMeteoGeocodingApi
import com.example.weatherapp.data.remote.dto.ForecastResponseDto
import com.example.weatherapp.domain.model.ForecastDay
import com.example.weatherapp.domain.model.WeatherInfo
import com.example.weatherapp.domain.util.weatherCodeToText

class WeatherRepository(
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val forecastApi: OpenMeteoForecastApi,
    private val dataStore: AppDataStore
) {
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    suspend fun fetchWeatherOnline(cityQuery: String, unit: String): WeatherInfo {
        val city = cityQuery.trim()
        require(city.isNotBlank()) { "EMPTY_INPUT" }

        return withContext(Dispatchers.IO) {
            val geo = geocodingApi.searchCity(name = city)
            val best = geo.results?.firstOrNull() ?: throw IllegalArgumentException("CITY_NOT_FOUND")

            val forecast = forecastApi.getForecast(
                latitude = best.latitude,
                longitude = best.longitude,
                temperatureUnit = unit
            )

            // cache
            dataStore.saveCache(best.name, json.encodeToString(forecast))
            dataStore.addToHistory(best.name)

            mapToWeatherInfo(best.name, forecast, isOffline = false)
        }
    }

    suspend fun readCachedWeather(): WeatherInfo? {
        val cached = dataStore.cachedJsonFlow().first() ?: return null
        val dto = json.decodeFromString<ForecastResponseDto>(cached)
        // city name мы не храним в dto, но в UI можно показать "Cached city"
        // Для простоты используем "Cached"
        return mapToWeatherInfo(city = "Cached", dto = dto, isOffline = true)
    }

    fun mapToWeatherInfo(city: String, dto: ForecastResponseDto, isOffline: Boolean): WeatherInfo {
        val current = dto.current
        val daily = dto.daily

        val maxToday = daily?.tempMax?.firstOrNull() ?: 0.0
        val minToday = daily?.tempMin?.firstOrNull() ?: 0.0
        val cond = weatherCodeToText(current?.weatherCode)

        val forecastDays = buildList {
            val count = minOf(daily?.time?.size ?: 0, 7)
            for (i in 0 until count) {
                add(
                    ForecastDay(
                        date = daily!!.time[i],
                        min = daily.tempMin.getOrNull(i) ?: 0.0,
                        max = daily.tempMax.getOrNull(i) ?: 0.0,
                        conditionText = weatherCodeToText(daily.weatherCode.getOrNull(i))
                    )
                )
            }
        }.take(3)

        return WeatherInfo(
            city = city,
            updatedAt = current?.time ?: "",
            temperature = current?.temperature ?: 0.0,
            tempUnit = dto.currentUnits?.temperatureUnit ?: "°C",
            conditionText = cond,
            minTemp = minToday,
            maxTemp = maxToday,
            humidity = current?.humidity ?: 0,
            windSpeed = current?.windSpeed ?: 0.0,
            windUnit = dto.currentUnits?.windUnit ?: "km/h",
            isOffline = isOffline,
            forecastDays = forecastDays
        )
    }
}