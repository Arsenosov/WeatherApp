package com.arsenosov.weatherapp.searchactivity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsenosov.weatherapp.R
import com.arsenosov.weatherapp.city.CityItem
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), Statable, SearchView.OnQueryTextListener {

    private lateinit var myViewModel: SearchViewModel
    private lateinit var adapterSearch: SearchRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        myViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        myViewModel.stateLive.observe(this, {
            state = it
        })
        myViewModel.citiesLive.observe(this, {
            refreshRecyclerView(it)
        })
        myViewModel.errorLive.observe(this, {
            tvSearchError.text = it
        })

        adapterSearch = SearchRecyclerViewAdapter(emptyList()) {
            returnItem(it)
        }
        rvSearchCities.layoutManager = LinearLayoutManager(this)
        rvSearchCities.adapter = adapterSearch
    }

    private fun returnItem(item: CityItem) {
        val intent = Intent()
        intent.putExtra(SEARCH_RESULT_CITY, item)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun refreshRecyclerView(list: List<CityItem>?) {
        list?.let { adapterSearch.refreshItems(it) }
    }

    override var state: State = State.LOADING
        set(_state) {
            field = _state
            changeUI(field)
        }

    override fun changeUI(state: State) {
        when (state) {
            State.ERROR -> {
                tvSearchError.visibility = View.VISIBLE
                rvSearchCities.visibility = View.GONE
            }
            State.SUCCESSFUL -> {
                tvSearchError.visibility = View.GONE
                rvSearchCities.visibility = View.VISIBLE
            }
            State.LOADING ->{}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { myViewModel.searchCity(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    companion object {
        const val SEARCH_RESULT_CITY = "SEARCH_RESULT_CITY"
    }
}