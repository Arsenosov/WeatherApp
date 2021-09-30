package com.arsenosov.weatherapp.di

import com.arsenosov.weatherapp.weather.WeatherApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class WeatherModule {

    private val BASE_URL = "https://api.openweathermap.org/"

    @Provides
    fun provideWeatherApi() : WeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

}