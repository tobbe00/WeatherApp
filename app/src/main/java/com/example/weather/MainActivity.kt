package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.weather.ui.screens.WeatherScreen
import com.example.weather.ui.viewmodels.WeatherViewModel
import com.example.weather.ui.viewmodels.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = WeatherViewModelFactory(applicationContext)
        val weatherViewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        setContent {
            WeatherScreen(weatherViewModel = weatherViewModel)
        }
    }
}
