package com.arsenosov.weatherapp.forecast

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arsenosov.weatherapp.weather.FutureWeatherSummary

class ForecastFragmentAdapter(val list: List<FutureWeatherSummary>, fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }
}