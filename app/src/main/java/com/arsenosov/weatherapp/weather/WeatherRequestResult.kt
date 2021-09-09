package com.arsenosov.weatherapp.weather

import com.arsenosov.weatherapp.city.CityItem
import com.google.gson.annotations.SerializedName

data class WeatherRequestResult(
    @SerializedName("city")
    val city: CityItem,
    @SerializedName("list")
    val list: List<WeatherSummary>
)
