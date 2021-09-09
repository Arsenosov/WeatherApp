package com.arsenosov.weatherapp.mainactivity

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.searchactivity.SearchActivity
import com.arsenosov.weatherapp.searchactivity.SearchActivity.Companion.SEARCH_RESULT_CITY
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Statable {

    private lateinit var myMainViewModel: MainViewModel
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
            refreshUI(it)
        })
        myMainViewModel.stateLive.observe(this, {
            state = it
        })
        if (city == null)
            myMainViewModel.getLocation(this)

    }

    private fun refreshUI(cityItem: CityItem) {
        if (cityItem != city) {
            TODO("refresh")
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

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                city = it.getParcelableExtra(SEARCH_RESULT_CITY)
                Toast.makeText(this, city.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
    }
}