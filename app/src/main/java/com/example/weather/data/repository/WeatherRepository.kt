package com.example.weather.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.example.weather.models.WeatherResponse

class WeatherRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWeatherData(data: WeatherResponse) {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString("weather_data", json).apply()
        Log.d("WeatherRepository", "Data Saved: $json")
    }

    fun loadWeatherData(): WeatherResponse? {
        val json = sharedPreferences.getString("weather_data", null) ?: return null
        Log.d("WeatherRepository", "Data Loaded: $json")
        return gson.fromJson(json, WeatherResponse::class.java)
    }
}
