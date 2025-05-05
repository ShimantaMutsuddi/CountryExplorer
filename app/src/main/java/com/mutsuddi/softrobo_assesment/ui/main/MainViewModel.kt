package com.mutsuddi.softrobo_assesment.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutsuddi.softrobo_assesment.data.model.Country
import com.mutsuddi.softrobo_assesment.data.repository.CountryRepository
import com.mutsuddi.softrobo_assesment.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CountryRepository
) : ViewModel() {

    private val _countries = MutableStateFlow<Resource<List<Country>>>(Resource.Loading())
    val countries: StateFlow<Resource<List<Country>>> = _countries

    // Full unfiltered list
    private var allCountries: List<Country> = emptyList()

    // Sorting direction flag
    private var isAscending = true

    fun fetchCountries() {
        _countries.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.getCountries()
            if (result is Resource.Success) {
                allCountries = result.data ?: emptyList()
                applyCurrentSortAndSearch()
            } else {
                _countries.value = result
            }
        }
    }

    // Internal state
    private var currentQuery: String = ""

    fun searchCountries(query: String) {
        currentQuery = query
        applyCurrentSortAndSearch()
    }

    fun setSortOrder(ascending: Boolean) {
        isAscending = ascending
        applyCurrentSortAndSearch()
    }

   // fun isSortAscending(): Boolean = isAscending



    private fun applyCurrentSortAndSearch() {
        var filtered = allCountries

        // Apply search filter
        if (currentQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(currentQuery, ignoreCase = true) ||
                        it.capital.contains(currentQuery, ignoreCase = true) ||
                        it.region.contains(currentQuery, ignoreCase = true) ||
                        it.code.contains(currentQuery, ignoreCase = true)
            }
        }

        // Apply sort
        filtered = if (isAscending) {
            filtered.sortedBy { it.name }
        } else {
            filtered.sortedByDescending { it.name }
        }

        _countries.value = Resource.Success(filtered)
    }


}
