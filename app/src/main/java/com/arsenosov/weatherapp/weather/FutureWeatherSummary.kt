package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class FutureWeatherSummary(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("temp")
    val temp: Temperature,
    @SerializedName("wind_speed")
    val wind: Double,
    @SerializedName("weather")
    val weather: List<Weather>
)
