package com.arsenosov.weatherapp.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/forecast")
    suspend fun getWeatherById(@Query("id") id: String,
                               @Query("units") units: String = "metric",
                               @Query("lang") lang: String = "en",
                               @Query("appId") apiKey: String) : WeatherRequestResult
}