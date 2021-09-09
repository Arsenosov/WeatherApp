package com.arsenosov.weatherapp.dagger

import com.arsenosov.weatherapp.mainactivity.MainViewModel
import com.arsenosov.weatherapp.searchactivity.SearchViewModel
import dagger.Component

@Component(modules = [
    CityModule::class,
    WeatherModule::class
])
interface AppComponent {
    fun inject(searchViewModel: SearchViewModel)
    fun inject(mainViewModel: MainViewModel)
}