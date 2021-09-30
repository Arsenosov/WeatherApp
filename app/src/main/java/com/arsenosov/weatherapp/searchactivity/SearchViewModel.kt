package com.arsenosov.weatherapp.searchactivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arsenosov.weatherapp.App
import com.arsenosov.weatherapp.App.Companion.API_KEY
import com.arsenosov.weatherapp.BuildConfig
import com.arsenosov.weatherapp.city.CityApi
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.util.State
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel(app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var cityApi: CityApi

    private val _stateLive: MutableLiveData<State> = MutableLiveData()
    private val _errorLive: MutableLiveData<String> = MutableLiveData()
    private val _citiesLive: MutableLiveData<List<CityItem>> = MutableLiveData()

    val stateLive: LiveData<State> get() = _stateLive
    val errorLive: LiveData<String> get() = _errorLive
    val citiesLive: LiveData<List<CityItem>> get() = _citiesLive

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            try {
                val result = cityApi.getCitiesList(cityName, API_KEY)
                _citiesLive.value = result
                _stateLive.value = State.SUCCESSFUL
            } catch (e: Exception) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
                _stateLive.value = State.ERROR
                _errorLive.value = "An error has occurred. Check your connection or try again later."
            }
        }
    }

    init {
        (app as App).appComponent.inject(this)
        _errorLive.value = "Unknown"
        _stateLive.value = State.SUCCESSFUL
    }
}