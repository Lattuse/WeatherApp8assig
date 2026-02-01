package com.example.weatherapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.weatherapp.ui.WeatherViewModel

@Composable
fun AppNav(vm: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                vm = vm,
                onGoWeather = { navController.navigate("weather") },
                onGoSettings = { navController.navigate("settings") }
            )
        }
        composable("weather") {
            WeatherScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onGoSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}