package com.arsenosov.weatherapp.util

interface Statable {
    var state: State

    fun changeUI(state: State)
}