package com.mutsuddi.softrobo_assesment.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mutsuddi.softrobo_assesment.R
import com.mutsuddi.softrobo_assesment.databinding.ActivityMainBinding
import com.mutsuddi.softrobo_assesment.util.NetworkUtils
import com.mutsuddi.softrobo_assesment.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var countryAdapter: CountryAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Setup RecyclerView
        countryAdapter = CountryAdapter()

        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = countryAdapter

        lifecycleScope.launch {
            viewModel.countries.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        //countryAdapter.submitList(result.data ) // custom method
                        countryAdapter.submitList(result.data ?: emptyList()) {
                            binding.recyclerView.scrollToPosition(0) // Scroll to top after list is updated
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

       /* if (NetworkUtils.isInternetAvailable(this)) {
            viewModel.fetchCountries()
        } else {
            Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") {
                    if (NetworkUtils.isInternetAvailable(this)) {
                        viewModel.fetchCountries()
                    } else {
                        Toast.makeText(this, "Still no internet!", Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
        }*/

        binding.retryButton.setOnClickListener {
            checkInternetAndFetch()
        }

        checkInternetAndFetch()

    }

    private fun checkInternetAndFetch() {
        if (NetworkUtils.isInternetAvailable(this)) {
            binding.noInternetLottie.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            binding.noInternetText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            viewModel.fetchCountries()
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.noInternetLottie.visibility = View.VISIBLE
            binding.retryButton.visibility = View.VISIBLE
            binding.noInternetText.visibility = View.VISIBLE
            binding.progressBar.visibility=View.INVISIBLE

        }
    }

    private var isAscending = true
    private var sortMenuItem: MenuItem? = null

    // Inflate the menu with the SearchView
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Log.d("Menu", "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.main_menu, menu)

        sortMenuItem = menu?.findItem(R.id.action_sort)
        updateSortMenuItem()

        // Get the SearchView from the menu
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        /*if (searchView == null) {
            Log.e("SearchView", "SearchView is null!")
        }*/

        // Setup the SearchView listener
        searchView?.queryHint = "Search.."
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // When the search is submitted, use the ViewModel to filter the list
                //Log.d("Search", "SearchView working")
                viewModel.searchCountries(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Whenever the query text changes, filter the list
                //Log.d("Search", "SearchView working")
                viewModel.searchCountries(newText ?: "")
                return true
            }
        })

        return true
    }

    private fun updateSortMenuItem() {
        sortMenuItem?.apply {
            title = if (isAscending) "Sort A-Z" else "Sort Z-A"
            //setIcon(if (isAscending) R.drawable.ic_az else R.drawable.ic_za)

            val iconRes = if (isAscending) R.drawable.ic_az else R.drawable.ic_za
            val icon = ContextCompat.getDrawable(this@MainActivity, iconRes)
            icon?.setTint(ContextCompat.getColor(this@MainActivity, R.color.surface_light)) // Make sure white color exists
            this.icon = icon
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                isAscending = !isAscending
                viewModel.setSortOrder(isAscending)
                updateSortMenuItem()
                //(binding.recyclerView.layoutManager as? LinearLayoutManager)?.reverseLayout = !isAscending

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}


