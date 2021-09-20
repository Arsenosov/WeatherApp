package com.arsenosov.weatherapp.mainactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.databinding.ActivityMainBinding
import com.arsenosov.weatherapp.databinding.LayoutMainBinding
import com.arsenosov.weatherapp.searchactivity.SearchActivity
import com.arsenosov.weatherapp.searchactivity.SearchActivity.Companion.SEARCH_RESULT_CITY
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State
import com.arsenosov.weatherapp.weather.WeatherRequestResult
import com.bumptech.glide.Glide
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), Statable {

    private lateinit var myMainViewModel: MainViewModel
    private lateinit var adapterFuture: MainFutureRecyclerViewAdapter
    private lateinit var adapterHourly: MainHourlyRecyclerViewAdapter
    private lateinit var bindingActivityMain: ActivityMainBinding
    private lateinit var bindingLayoutMain: LayoutMainBinding
    private var city: CityItem? = null

    override var state: State = State.LOADING
        set(_state) {
            field = _state
            changeUI(field)
        }

    override fun changeUI(state: State) {
        when (state) {
            State.LOADING -> {
                bindingActivityMain.pbMain.visibility = View.VISIBLE
                bindingLayoutMain.mainLayoutGroup.visibility = View.GONE
                bindingActivityMain.tvMainError.visibility = View.GONE
            }
            State.ERROR -> {
                bindingActivityMain.pbMain.visibility = View.GONE
                bindingLayoutMain.mainLayoutGroup.visibility = View.GONE
                bindingActivityMain.tvMainError.visibility = View.VISIBLE
            }
            State.SUCCESSFUL -> {
                bindingActivityMain.pbMain.visibility = View.GONE
                bindingLayoutMain.mainLayoutGroup.visibility = View.VISIBLE
                bindingActivityMain.tvMainError.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingActivityMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingActivityMain.root)
        bindingLayoutMain = bindingActivityMain.mainViewGroup

        myMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        myMainViewModel.cityLive.observe(this, {
            checkRefreshUI(it)
        })
        myMainViewModel.stateLive.observe(this, {
            state = it
        })
        myMainViewModel.weatherLive.observe(this, {
            refreshUI(it)
        })

        adapterFuture = MainFutureRecyclerViewAdapter(emptyList())
        bindingLayoutMain.rvFutureWeather.layoutManager = LinearLayoutManager(this)
        bindingLayoutMain.rvFutureWeather.adapter = adapterFuture
        bindingLayoutMain.rvFutureWeather.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapterHourly = MainHourlyRecyclerViewAdapter(emptyList())
        bindingLayoutMain.rvHourlyWeather.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bindingLayoutMain.rvHourlyWeather.adapter = adapterHourly
        bindingLayoutMain.rvHourlyWeather.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))

        myMainViewModel.requestLocationPermission(this)
    }

    private fun checkRefreshUI(cityItem: CityItem) {
        if (cityItem != city) {
            myMainViewModel.loadWeather(cityItem)
            city = cityItem
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Choosing Moscow, RU as a default location", Toast.LENGTH_LONG).show()
                checkRefreshUI(Moscow)
            } else {
                myMainViewModel.getLocation(this)
            }
    }

    private fun refreshUI(request: WeatherRequestResult) {
        adapterFuture.refreshItems(request.futureWeather)
        adapterHourly.refreshItems(request.hourlyWeather)
        val current = request.currentWeather
        Glide.with(this)
            .load("$BASE_IMG_URL${current.weather[0].icon}.png")
            .into(bindingLayoutMain.ivMainWeather)
        bindingLayoutMain.tvCurrentCity.text = city.toString()
        bindingLayoutMain.tvMainTemperature.text = resources.getString(R.string.weather_temperature, current.temp.roundToInt())
        bindingLayoutMain.tvMainFeelsTemperature.text = resources.getString(R.string.weather_feels_temperature, current.feelsTemp.roundToInt())
        bindingLayoutMain.tvMainWind.text = resources.getString(R.string.weather_wind, current.wind.roundToInt())
        bindingLayoutMain.tvMainWeather.text = current.weather[0].description.capitalize(Locale.getDefault())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Change city")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> resultLauncher.launch(Intent(this, SearchActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                checkRefreshUI(it.getParcelableExtra(SEARCH_RESULT_CITY)!!)
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
        const val BASE_IMG_URL = "https://openweathermap.org/img/wn/"
        val Moscow = CityItem(55.7617, 37.6067, "Moscow", "RU")
    }
}