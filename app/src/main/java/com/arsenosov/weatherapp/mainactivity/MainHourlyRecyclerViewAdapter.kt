package com.arsenosov.weatherapp.mainactivity

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.databinding.HourlyWeatherItemBinding
import com.arsenosov.weatherapp.mainactivity.MainActivity.Companion.BASE_IMG_URL
import com.arsenosov.weatherapp.weather.HourlyWeatherSummary
import com.bumptech.glide.Glide
import java.util.*
import kotlin.math.roundToInt

class MainHourlyRecyclerViewAdapter(private var listWeather: List<HourlyWeatherSummary>):
        RecyclerView.Adapter<MainHourlyRecyclerViewAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        private val binding = HourlyWeatherItemBinding.bind(view)
	    fun bind(weather: HourlyWeatherSummary) {
            Glide.with(view)
                .load("$BASE_IMG_URL${weather.weather[0].icon}.png")
                .into(binding.ivHourlyWeather)
            binding.tvHourlyTemperature.text = view.context.resources.getString(R.string.weather_temperature, weather.temp.roundToInt())
            binding.tvHourlyTime.text = if (Date(weather.dt * 1000).after(Date())) DateFormat.format("HH:mm", Date(weather.dt * 1000))
            else view.context.resources.getString(R.string.hourly_now)
	    }
    }

    fun refreshItems(list: List<HourlyWeatherSummary>) {
        listWeather = if (list.size > 24) list.subList(0, 24) else list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HourlyViewHolder(LayoutInflater.from(parent.context).
            inflate(R.layout.hourly_weather_item, parent, false))

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) = holder.bind(listWeather[position])

    override fun getItemCount(): Int = listWeather.size
}