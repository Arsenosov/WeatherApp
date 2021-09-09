package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class WeatherSummary(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: Weather,
    @SerializedName("wind")
    val wind: Wind
)
