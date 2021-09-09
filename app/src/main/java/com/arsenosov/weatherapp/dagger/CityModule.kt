package com.arsenosov.weatherapp.dagger

import com.arsenosov.weatherapp.city.CityApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class CityModule {

    private val BASE_URL = "http://api.openweathermap.org"

    @Provides
    fun provideCityApi() : CityApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CityApi::class.java)
    }
}