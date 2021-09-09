package com.arsenosov.weatherapp.mainactivity

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.arsenosov.weatherapp.App
import com.arsenosov.weatherapp.city.CityApi
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.util.State
import com.arsenosov.weatherapp.weather.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import javax.inject.Inject

class MainViewModel(app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var cityApi: CityApi
    @Inject
    lateinit var weatherApi: WeatherApi

    val cityLive = MutableLiveData<CityItem>()
    val stateLive = MutableLiveData<State>()
    val errorLive = MutableLiveData<String>()

    init {
        (app as App).appComponent.inject(this)
        stateLive.value = State.LOADING
        errorLive.value = "Unknown"
    }

    fun getLocation(activity: Activity) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.PERMISSIONS_REQUEST_CODE
            )
            return
        }

        task.addOnSuccessListener {
            Toast.makeText(activity.baseContext, "${it.latitude}, ${it.longitude} ${stateLive.value}", Toast.LENGTH_LONG).show()
        }
    }

    fun loadWeather(city: CityItem) {

    }
}