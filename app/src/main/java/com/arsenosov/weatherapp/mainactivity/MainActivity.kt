package com.arsenosov.weatherapp.mainactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModelProvider
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.compose.ui.theme.WeatherAppTheme
import com.arsenosov.weatherapp.searchactivity.SearchActivity
import com.arsenosov.weatherapp.searchactivity.SearchActivity.Companion.SEARCH_RESULT_CITY
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State
import com.arsenosov.weatherapp.weather.FutureWeatherSummary
import com.arsenosov.weatherapp.weather.HourlyWeatherSummary
import com.skydoves.landscapist.glide.GlideImage
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), Statable {

    private lateinit var myMainViewModel: MainViewModel
    private var city: CityItem? = null

    override var state: State = State.LOADING

    @Composable
    fun HourlyList(modifier: Modifier) {
        val list = myMainViewModel.weatherLive.value
        LazyRow(
            modifier = modifier,
        ) {
            items(
                items = list?.hourlyWeather as List<HourlyWeatherSummary>,
                itemContent = {
                    HourlyListItem(weather = it)
                },
            )
        }
    }

    @Composable
    fun HourlyListItem(weather: HourlyWeatherSummary) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PaddingValues(2.dp)
            Text(
                text = if (Date(weather.dt * 1000).after(Date())) DateFormat.format("HH:mm", Date(weather.dt * 1000)).toString()
                else stringResource(id = R.string.hourly_now),
                fontSize = 16.sp,
            )
            GlideImage(
                imageModel = "$BASE_IMG_URL${weather.weather[0].icon}.png",
                modifier = Modifier.size(50.dp, 50.dp),
            )
            Text(
                text = stringResource(id = R.string.weather_temperature, weather.temp.roundToInt()),
                fontSize = 14.sp,
            )
        }
    }

    @Composable
    fun FutureList(modifier: Modifier) {
        val list = myMainViewModel.weatherLive.value
        LazyColumn(
            modifier = modifier
        ) {
            items(
                items = list?.futureWeather as List<FutureWeatherSummary>,
                itemContent = {
                    FutureListItem(weather = it)
                }
            )
        }
    }

    @Composable
    fun FutureListItem(weather: FutureWeatherSummary) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (dateText, tempText, descText, iconImg) = createRefs()
            PaddingValues(30.dp)
            Text(
                text = DateFormat.format("dd.MM.yyyy", Date(weather.dt*1000)).toString(),
                fontSize = 14.sp,
                modifier = Modifier.constrainAs(dateText) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, 10.dp)
                }
            )
            GlideImage(
                imageModel = "$BASE_IMG_URL${weather.weather[0].icon}.png",
                modifier = Modifier.constrainAs(iconImg) {
                    top.linkTo(dateText.bottom, 20.dp)
                    end.linkTo(dateText.end)
                    width = Dimension.value(80.dp)
                    height = Dimension.value(80.dp)
                }
            )
            Text(
                text = stringResource(id = R.string.weather_temperature, weather.temp.temp.roundToInt()),
                fontSize = 52.sp,
                modifier = Modifier.constrainAs(tempText) {
                    top.linkTo(dateText.top)
                    start.linkTo(parent.start, 10.dp)
                }
            )
            Text(
                text = weather.weather[0].description.replaceFirstChar { it.uppercase() },
                fontSize = 24.sp,
                modifier = Modifier.constrainAs(descText) {
                    bottom.linkTo(iconImg.bottom)
                    start.linkTo(tempText.start)
                }
            )
        }
    }

    @Composable
    fun MainScreen() {
        val state = myMainViewModel.stateLive.value
        val error = myMainViewModel.errorLive.value
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (state) {
                State.ERROR -> Text(
                    text = error,
                    fontSize = 50.sp,
                    textAlign = TextAlign.Center,
                )
                State.LOADING -> CircularProgressIndicator(
                    modifier = Modifier.size(180.dp, 180.dp),
                )
                State.SUCCESSFUL -> WeatherScreen()
            }
        }
    }

    @Composable
    fun WeatherScreen() {
        val city = myMainViewModel.cityLive.value
        val weather = myMainViewModel.weatherLive.value
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (locationString, locationText, tempText, feelsTempText, descText, windText, iconImg,
                hourlyList, futureList) = createRefs()
            Text(
                text = stringResource(id = R.string.current_location),
                fontSize = 20.sp,
                modifier = Modifier.constrainAs(locationString) {
                    top.linkTo(parent.top, 5.dp)
                    centerHorizontallyTo(parent)
                }
            )
            Text(
                text = city.toString(),
                fontSize = 24.sp,
                modifier = Modifier.constrainAs(locationText) {
                    top.linkTo(locationString.bottom)
                    centerHorizontallyTo(parent)
                }
            )
            GlideImage(
                imageModel = "$BASE_IMG_URL${weather?.currentWeather?.weather?.get(0)?.icon}.png",
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .constrainAs(iconImg) {
                        top.linkTo(locationText.bottom, 30.dp)
                        end.linkTo(parent.end, 30.dp)
                    }
            )
            Text(
                text = weather?.currentWeather?.temp?.roundToInt()?.let {
                    stringResource(id = R.string.weather_temperature, it)
                }.toString(),
                fontSize = 52.sp,
                modifier = Modifier.constrainAs(tempText) {
                    top.linkTo(iconImg.top)
                    start.linkTo(parent.start, 50.dp)
                }
            )
            Text(
                text = weather?.currentWeather?.feelsTemp?.roundToInt()?.let {
                    stringResource(id = R.string.weather_feels_temperature, it)
                }.toString(),
                fontSize = 24.sp,
                modifier = Modifier.constrainAs(feelsTempText) {
                    start.linkTo(tempText.start)
                    bottom.linkTo(iconImg.bottom)
                }
            )
            Text(
                text = weather?.currentWeather?.weather?.get(0)?.description?.let { desc -> desc.replaceFirstChar { it.uppercase() }}.toString(),
                fontSize = 24.sp,
                modifier = Modifier.constrainAs(descText) {
                    start.linkTo(feelsTempText.start)
                    top.linkTo(feelsTempText.bottom)
                }
            )
            Text(
                text = weather?.currentWeather?.wind?.roundToInt()?.let {
                    stringResource(id = R.string.weather_wind, it)
                }.toString(),
                fontSize = 24.sp,
                modifier = Modifier.constrainAs(windText) {
                    start.linkTo(descText.start)
                    top.linkTo(descText.bottom)
                }
            )
            HourlyList(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(hourlyList) {
                        top.linkTo(windText.bottom, 20.dp)
                        centerHorizontallyTo(parent)
                    }
            )
            FutureList(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(futureList) {
                        top.linkTo(hourlyList.bottom, 20.dp)
                        bottom.linkTo(parent.bottom)
                        centerHorizontallyTo(parent)
                        height = Dimension.fillToConstraints
                    }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        WeatherAppTheme {
            MainScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            MainScreen()
        }
        myMainViewModel.requestLocationPermission(this)
    }

    private fun checkCityChange(cityItem: CityItem) {
        if (cityItem != city) {
            myMainViewModel.loadWeather(cityItem)
            city = cityItem
        }
    }

    override fun onResume() {
        super.onResume()
        myMainViewModel.checkWeatherActuality()
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
                //TODO("replace text in toast with translatable")
                checkCityChange(Moscow)
            } else {
                myMainViewModel.getLocation(this)
            }
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
                checkCityChange(it.getParcelableExtra(SEARCH_RESULT_CITY)!!)
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
        const val BASE_IMG_URL = "https://openweathermap.org/img/wn/"
        val Moscow = CityItem(55.7617, 37.6067, "Moscow", "RU")
    }
}