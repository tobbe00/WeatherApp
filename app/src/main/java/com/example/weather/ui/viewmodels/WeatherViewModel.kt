package com.example.weather.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.WeatherResponse
import com.example.weather.models.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val context: Context) : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun fetchWeather(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getWeatherForecast(latitude, longitude)
                _weatherData.value = response
                saveWeatherData(response)
            } catch (e: Exception) {
                _weatherData.value = loadWeatherData()
            }
        }
    }

    private fun saveWeatherData(data: WeatherResponse) {
        val json = gson.toJson(data)
        sharedPrefs.edit().putString("weather_data", json).apply()
    }

    private fun loadWeatherData(): WeatherResponse? {
        val json = sharedPrefs.getString("weather_data", null) ?: return null
        return gson.fromJson(json, WeatherResponse::class.java)
    }
}
