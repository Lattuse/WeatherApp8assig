package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    vm: WeatherViewModel,
    onBack: () -> Unit,
    onGoSettings: () -> Unit
) {
    val state by vm.state.collectAsState()
    val weather = state.weather

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    TextButton(onClick = onGoSettings) { Text("Settings") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (weather == null) {
                Text("No data yet. Search a city.")
                return@Column
            }

            if (weather.isOffline) {
                AssistChip(
                    onClick = {},
                    label = { Text("OFFLINE (cached)") }
                )
            }

            Text(weather.city, style = MaterialTheme.typography.headlineMedium)
            Text("${weather.temperature} ${weather.tempUnit} • ${weather.conditionText}")

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Min/Max: ${weather.minTemp} / ${weather.maxTemp} ${weather.tempUnit}")
                    Text("Humidity: ${weather.humidity}%")
                    Text("Wind: ${weather.windSpeed} ${weather.windUnit}")
                    Text("Last update: ${weather.updatedAt}")
                }
            }

            Text("Forecast (3 days)", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(weather.forecastDays) { day ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(day.date, style = MaterialTheme.typography.titleSmall)
                                Text(day.conditionText, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${day.min} / ${day.max} ${weather.tempUnit}")
                        }
                    }
                }
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}