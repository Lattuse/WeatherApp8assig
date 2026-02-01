package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: WeatherViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()
    val isCelsius = state.unit == "celsius"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Units", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (isCelsius) "Celsius (°C)" else "Fahrenheit (°F)")
                Switch(
                    checked = !isCelsius,
                    onCheckedChange = { vm.toggleUnit() }
                )
            }

            Text("Current: ${if (isCelsius) "Celsius" else "Fahrenheit"}")
        }
    }
}
