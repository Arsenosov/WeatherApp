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
import com.arsenosov.weatherapp.searchactivity.SearchActivity
import com.arsenosov.weatherapp.searchactivity.SearchActivity.Companion.SEARCH_RESULT_CITY
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State
import com.arsenosov.weatherapp.weather.WeatherRequestResult
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), Statable {

    private lateinit var myMainViewModel: MainViewModel
    private lateinit var adapter: MainRecyclerViewAdapter
    private var city: CityItem? = null

    override var state: State = State.LOADING
        set(_state) {
            field = _state
            changeUI(field)
        }

    override fun changeUI(state: State) {
        when (state) {
            State.LOADING -> {
                pbMain.visibility = View.VISIBLE
                mainViewGroup.visibility = View.GONE
                tvMainError.visibility = View.GONE
            }
            State.ERROR -> {
                pbMain.visibility = View.GONE
                mainViewGroup.visibility = View.GONE
                tvMainError.visibility = View.VISIBLE
            }
            State.SUCCESSFUL -> {
                pbMain.visibility = View.GONE
                mainViewGroup.visibility = View.VISIBLE
                tvMainError.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        adapter = MainRecyclerViewAdapter(emptyList())
        rvFutureWeather.layoutManager = LinearLayoutManager(this)
        rvFutureWeather.adapter = adapter
        rvFutureWeather.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        //if (city == null)
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
        adapter.refreshItems(request.futureWeather)
        val current = request.currentWeather
        Glide.with(this)
            .load("$BASE_IMG_URL${current.weather[0].icon}.png")
            .into(ivMainWeather)
        tvCurrentCity.text = city.toString()
        tvMainTemperature.text = resources.getString(R.string.weather_temperature, current.temp.roundToInt())
        tvMainFeelsTemperature.text = resources.getString(R.string.weather_feels_temperature, current.feelsTemp.roundToInt())
        tvMainWind.text = resources.getString(R.string.weather_wind, current.wind.roundToInt())
        tvMainWeather.text = current.weather[0].description.capitalize(Locale.getDefault())
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