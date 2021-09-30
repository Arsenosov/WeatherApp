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
import com.arsenosov.weatherapp.databinding.ActivitySearchBinding
import com.arsenosov.weatherapp.util.Statable
import com.arsenosov.weatherapp.util.State

class SearchActivity : AppCompatActivity(), Statable, SearchView.OnQueryTextListener {

    private lateinit var myViewModel: SearchViewModel
    private lateinit var adapterSearch: SearchRecyclerViewAdapter
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        myViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        myViewModel.stateLive.observe(this, {
            state = it
        })
        myViewModel.citiesLive.observe(this, {
            refreshRecyclerView(it)
        })
        myViewModel.errorLive.observe(this, {
            binding.tvSearchError.text = it
        })
    }

    private fun setupRecyclerView() {
        adapterSearch = SearchRecyclerViewAdapter(emptyList()) {
            returnItem(it)
        }
        binding.rvSearchCities.layoutManager = LinearLayoutManager(this)
        binding.rvSearchCities.adapter = adapterSearch
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

    private fun changeUI(state: State) {
        when (state) {
            State.ERROR -> {
                binding.tvSearchError.visibility = View.VISIBLE
                binding.rvSearchCities.visibility = View.GONE
            }
            State.SUCCESSFUL -> {
                binding.tvSearchError.visibility = View.GONE
                binding.rvSearchCities.visibility = View.VISIBLE
            }
            State.LOADING ->{}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search for a city..."
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { myViewModel.searchCity(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    companion object {
        const val SEARCH_RESULT_CITY = "SEARCH_RESULT_CITY"
    }
}