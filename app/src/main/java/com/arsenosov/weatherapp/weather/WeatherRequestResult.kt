package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class WeatherRequestResult(
    @SerializedName("current")
    val currentWeather: CurrentWeatherSummary,
    @SerializedName("daily")
    val futureWeather: List<FutureWeatherSummary>
)
