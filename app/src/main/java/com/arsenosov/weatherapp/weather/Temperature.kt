package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("day")
    val temp: Double
)
