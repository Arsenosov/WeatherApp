package com.arsenosov.weatherapp.weather

import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("temp")
    val temp: Double
)
