package com.example.weather.models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/v1/"
    private const val GEOCODE_BASE_URL = "https://geocode.maps.co/"

    // Initialize WeatherApi only once using lazy initialization
    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    // Initialize GeocodeApi only once using lazy initialization
    val geocodeApi: GeocodeApi by lazy {
        Retrofit.Builder()
            .baseUrl(GEOCODE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodeApi::class.java)
    }
}