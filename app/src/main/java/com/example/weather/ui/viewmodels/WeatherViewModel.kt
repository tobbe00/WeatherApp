package com.example.weather.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.models.WeatherResponse
import com.example.weather.models.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Data class to hold weekly forecast data
data class WeeklyMinMax(
    val date: String,
    val minTemp: Float,
    val maxTemp: Float,
    val maxWeatherCode: Int
)

class WeatherViewModel(private val context: Context) : ViewModel() {
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _todayHourlyData = MutableStateFlow<List<Triple<String, Float, Int>>>(emptyList())
    val todayHourlyData = _todayHourlyData.asStateFlow()

    private val _weeklyMinMaxData = MutableStateFlow<List<WeeklyMinMax>>(emptyList())  // Updated to use the new data class
    val weeklyMinMaxData = _weeklyMinMaxData.asStateFlow()

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeather(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getWeatherForecast(latitude, longitude)
                _weatherData.value = response
                saveWeatherData(response)
                processWeatherData(response)
            } catch (e: Exception) {
                val cachedData = loadWeatherData()
                _weatherData.value = cachedData
                processWeatherData(cachedData)
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

        // Current time for comparison
        val currentTime = java.time.LocalDateTime.now()

        // Filter today's hourly data to exclude past times
        val filteredTodayHourlyData = hourlyData.take(24).filter {
            val entryTime = java.time.LocalDateTime.parse(it.first, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
            entryTime.isAfter(currentTime) || entryTime.isEqual(currentTime)
        }
        _todayHourlyData.value = filteredTodayHourlyData

        // Process remaining data for daily min/max temperatures
        val remainingData = hourlyData.drop(24) // Exclude the first 24 entries
        val groupedByDay = remainingData.groupBy { it.first.substring(0, 10) } // Group by date (YYYY-MM-DD)

        // Create weekly forecast data
        val weeklyMinMax = groupedByDay.map { (date, dayData) ->
            val minTemp = dayData.minOfOrNull { it.second } ?: 0f
            val maxTemp = dayData.maxOfOrNull { it.second } ?: 0f
            val maxWeatherCode = dayData.filter { it.second == maxTemp }.firstOrNull()?.third ?: 0 // Get the weather code for max temp
            WeeklyMinMax(date, minTemp, maxTemp, maxWeatherCode) // Use the new data class
        }

        _weeklyMinMaxData.value = weeklyMinMax
    }

    private fun saveWeatherData(data: WeatherResponse) {
        val json = gson.toJson(data)
        sharedPrefs.edit().putString("weather_data", json).apply()
    }

    private fun loadWeatherData(): WeatherResponse? {
        val json = sharedPrefs.getString("weather_data", null) ?: return null
        return gson.fromJson(json, WeatherResponse::class.java)
    }

    @Composable
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }
}

