package com.arsenosov.weatherapp.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall")
    suspend fun getWeatherByCoords(@Query("lat") lat: Double,
                                   @Query("lon") lon: Double,
                                   @Query("appId") appId: String,
                                   @Query("units") units: String = "metric",
                                   @Query("exclude") exclude: String = "minutely,alerts",
                                   @Query("lang") lang: String = "en"): WeatherRequestResult
}