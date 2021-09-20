package com.arsenosov.weatherapp.mainactivity

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.databinding.FutureWeatherItemBinding
import com.arsenosov.weatherapp.mainactivity.MainActivity.Companion.BASE_IMG_URL
import com.arsenosov.weatherapp.weather.FutureWeatherSummary
import com.bumptech.glide.Glide
import java.util.*
import kotlin.math.roundToInt

class MainFutureRecyclerViewAdapter(private var weatherList: List<FutureWeatherSummary>
    //, private val listener: (FutureWeatherSummary) -> (Unit)
    ): RecyclerView.Adapter<MainFutureRecyclerViewAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.future_weather_item, parent, false))

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) = holder.bind(weatherList[position])

    override fun getItemCount(): Int = weatherList.size

    fun refreshItems(list: List<FutureWeatherSummary>) {
        weatherList = list
        notifyDataSetChanged()
    }

    class MainViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val binding = FutureWeatherItemBinding.bind(view)
        fun bind(weather: FutureWeatherSummary) {
            Glide.with(view)
                .load("$BASE_IMG_URL${weather.weather[0].icon}.png")
                .into(binding.ivItemWeather)
            binding.tvItemDate.text = DateFormat.format("dd.MM.yyyy", Date(weather.dt*1000))
            binding.tvItemWeather.text = weather.weather[0].description.capitalize(Locale.getDefault())
            binding.tvItemTemperature.text = view.context.resources.getString(R.string.weather_temperature, weather.temp.temp.roundToInt())
            binding.tvItemWind.text = view.context.resources.getString(R.string.weather_wind, weather.wind.roundToInt())
        }
    }
}