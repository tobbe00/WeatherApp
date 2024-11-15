package com.example.weather.models

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
