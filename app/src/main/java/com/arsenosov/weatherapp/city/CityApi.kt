package com.arsenosov.weatherapp.city

import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {
    @GET("geo/1.0/direct")
    suspend fun getCitiesList(@Query("q") cityName: String,
                              @Query("appId") apiKey: String,
                              @Query("limit") count: String = "15"): List<CityItem>

    @GET("geo/1.0/reverse")
    suspend fun getCityByCoords(@Query("lat") lat: Double,
                                @Query("lon") lon: Double,
                                @Query("appId") appId: String,
                                @Query("limit") limit: Int = 1): List<CityItem>
}