package com.example.weather.models

import retrofit2.http.GET
import retrofit2.http.Query

typealias GeocodeResponse = List<GeocodeResponseItem>

interface GeocodeApi {
    @GET("search")
    suspend fun getCoordinates(
        @Query("q") placeName: String
    ): GeocodeResponse
}