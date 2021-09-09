package com.arsenosov.weatherapp.searchactivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arsenosov.weatherapp.App
import com.arsenosov.weatherapp.App.Companion.API_KEY
import com.arsenosov.weatherapp.city.CityApi
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.util.State
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel(app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var cityApi: CityApi

    val stateLive = MutableLiveData<State>()
    val errorLive = MutableLiveData<String>()
    val citiesLive = MutableLiveData<List<CityItem>>()

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            try {
                val result = cityApi.getCitiesList(cityName, API_KEY)
                citiesLive.value = result
                stateLive.value = State.SUCCESSFUL
            } catch (e: Exception) {
                stateLive.value = State.ERROR
                errorLive.value = "An error has occurred. Check your connection or try again later."
            }
        }
    }

    init {
        (app as App).appComponent.inject(this)
        errorLive.value = "Unknown"
        stateLive.value = State.SUCCESSFUL
    }
}