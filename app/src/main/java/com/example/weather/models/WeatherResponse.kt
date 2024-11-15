package com.example.weather.models

data class WeatherResponse(
    val hourly: HourlyWeather?
)

data class HourlyWeather(
    val time: List<String>?,
    val temperature_2m: List<Float>?,
    val weather_code: List<Int>?
)
