package com.arsenosov.weatherapp.mainactivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arsenosov.weatherapp.App
import com.arsenosov.weatherapp.App.Companion.API_KEY
import com.arsenosov.weatherapp.BuildConfig
import com.arsenosov.weatherapp.city.CityApi
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.util.State
import com.arsenosov.weatherapp.weather.WeatherApi
import com.arsenosov.weatherapp.weather.WeatherRequestResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel(app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var cityApi: CityApi
    @Inject
    lateinit var weatherApi: WeatherApi

    val cityLive: MutableState<CityItem?> = mutableStateOf(null)
    val stateLive: MutableState<State> = mutableStateOf(State.LOADING)
    val errorLive: MutableState<String> = mutableStateOf("Unknown")
    val weatherLive: MutableState<WeatherRequestResult?> = mutableStateOf(null)

    init {
        (app as App).appComponent.inject(this)
    }

    fun checkWeatherActuality() {
        val now = Date()
        val weatherLoaded = weatherLive.value?.currentWeather?.dt?.times(1000)?.let { Date(it) }
        //if more than 10 minutes passed the data is updated
        if (weatherLoaded != null && (now.time - weatherLoaded.time) / 60000 >= 10) {
            Toast.makeText(getApplication<Application>().applicationContext, "The data is outdated, loading new data...", Toast.LENGTH_LONG).show()
            //TODO("replace text in toast with translatable")
            stateLive.value = State.LOADING
            cityLive.value?.let { loadWeather(it) }
        }
    }

    fun requestLocationPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.PERMISSIONS_REQUEST_CODE
            )
        } else {
            getLocation(activity)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation(activity: Activity) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener {
            viewModelScope.launch {
                try {
                    val result = cityApi.getCityByCoords(it.latitude, it.longitude, API_KEY)
                    cityLive.value = result[0]
                    loadWeather(result[0])
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                    stateLive.value = State.ERROR
                    errorLive.value = e.message.toString()
                }
            }
        }
    }

    fun loadWeather(city: CityItem) {
        viewModelScope.launch {
            try {
                cityLive.value = city
                val result = weatherApi.getWeatherByCoords(city.lat, city.lon, API_KEY)
                weatherLive.value = result
                stateLive.value = State.SUCCESSFUL
            } catch (e: Exception) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace()
                stateLive.value = State.ERROR
                errorLive.value = e.message.toString()
            }
        }
    }
}