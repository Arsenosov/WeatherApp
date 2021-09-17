package com.arsenosov.weatherapp

import android.app.Application
import com.arsenosov.weatherapp.dagger.AppComponent
import com.arsenosov.weatherapp.dagger.CityModule
import com.arsenosov.weatherapp.dagger.DaggerAppComponent
import com.arsenosov.weatherapp.dagger.WeatherModule

class App: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .weatherModule(WeatherModule())
            .cityModule(CityModule())
            .build()
    }

    companion object {
        const val API_KEY = "8c5e9949a99843587a06dfa4c5696d93"
    }
}