package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.local.AppDataStore
import com.example.weatherapp.data.remote.RetrofitProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.screens.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = AppDataStore(this)
        val repo = WeatherRepository(
            geocodingApi = RetrofitProvider.geocodingApi(),
            forecastApi = RetrofitProvider.forecastApi(),
            dataStore = dataStore
        )

        setContent {
            val vm: WeatherViewModel = viewModel(factory = SimpleVmFactory { WeatherViewModel(repo, dataStore) })
            AppNav(vm)
        }
    }
}