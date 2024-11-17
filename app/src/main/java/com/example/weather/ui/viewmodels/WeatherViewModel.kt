package com.example.weather.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.models.RetrofitInstance
import com.example.weather.models.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data class to hold weekly forecast data
data class WeeklyMinMax(
    val date: String,
    val minTemp: Float,
    val maxTemp: Float,
    val maxWeatherCode: Int
)

class WeatherViewModel(
    private val repository: WeatherRepository // Använd repository istället för direkt SharedPreferences
) : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _todayHourlyData = MutableStateFlow<List<Triple<String, Float, Int>>>(emptyList())
    val todayHourlyData = _todayHourlyData.asStateFlow()

    private val _weeklyMinMaxData = MutableStateFlow<List<WeeklyMinMax>>(emptyList())
    val weeklyMinMaxData = _weeklyMinMaxData.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline = _isOffline.asStateFlow()


    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeather(latitude: Float, longitude: Float, context: Context) {
        viewModelScope.launch {
            if (!isInternetAvailable(context)) {
                // Ingen internetanslutning, använd cachad data och visa offline-status
                _isOffline.value = true
                loadOldWeatherData()
            } else {
                try {
                    val response = RetrofitInstance.api.getWeatherForecast(latitude, longitude)
                    _weatherData.value = response
                    repository.saveWeatherData(response)
                    processWeatherData(response)

                    // Internet är tillgängligt, ta bort offline-status
                    _isOffline.value = false
                } catch (e: Exception) {
                    _isOffline.value = true
                    loadOldWeatherData()
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun processWeatherData(data: WeatherResponse?) {
        if (data == null) return

        // Extract hourly data
        val timeList = data.hourly?.time ?: emptyList()
        val temperatureList = data.hourly?.temperature_2m ?: emptyList()
        val weatherCodeList = data.hourly?.weather_code ?: emptyList()

        if (timeList.isEmpty() || temperatureList.isEmpty() || weatherCodeList.isEmpty()) return

        // Combine hourly data into Triples
        val hourlyData = timeList.zip(temperatureList).zip(weatherCodeList) { (time, temp), code ->
            Triple(time, temp, code)
        }

        // Filter today's hourly data
        val currentTime = LocalDateTime.now()
        val filteredTodayHourlyData = hourlyData.take(24).filter {
            val entryTime = LocalDateTime.parse(it.first, DateTimeFormatter.ISO_DATE_TIME)
            entryTime.isAfter(currentTime) || entryTime.isEqual(currentTime)
        }
        _todayHourlyData.value = filteredTodayHourlyData

        // Process remaining data for daily min/max temperatures
        val remainingData = hourlyData.drop(24)
        val groupedByDay = remainingData.groupBy { it.first.substring(0, 10) }

        // Create weekly forecast data
        val weeklyMinMax = groupedByDay.map { (date, dayData) ->
            val minTemp = dayData.minOfOrNull { it.second } ?: 0f
            val maxTemp = dayData.maxOfOrNull { it.second } ?: 0f
            val maxWeatherCode = dayData.filter { it.second == maxTemp }.firstOrNull()?.third ?: 0
            WeeklyMinMax(date, minTemp, maxTemp, maxWeatherCode)
        }

        _weeklyMinMaxData.value = weeklyMinMax
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadOldWeatherData() {
        viewModelScope.launch {
            val cachedData = repository.loadWeatherData() // Ladda sparad data från repository
            _weatherData.value = cachedData // Uppdatera väderdata
            processWeatherData(cachedData) // Bearbeta väderdata för UI
        }
    }


    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}
