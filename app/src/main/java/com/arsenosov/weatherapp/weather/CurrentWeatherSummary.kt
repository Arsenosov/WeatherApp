package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherSummary(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feels_like")
    val feelsTemp: Double,
    @SerializedName("wind_speed")
    val wind: Double,
    @SerializedName("weather")
    val weather: Weather
)
