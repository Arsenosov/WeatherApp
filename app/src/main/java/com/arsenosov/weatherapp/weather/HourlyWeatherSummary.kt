package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class HourlyWeatherSummary (
        @SerializedName("dt")
        var dt: Long,
        @SerializedName("temp")
        var temp: Double,
        @SerializedName("feels_like")
        var feelsTemp: Double,
        @SerializedName("wind_speed")
        var wind: Double,
        @SerializedName("weather")
        var weather: List<Weather>
)
