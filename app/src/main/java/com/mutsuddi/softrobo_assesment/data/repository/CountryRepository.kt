package com.mutsuddi.softrobo_assesment.data.repository

import com.mutsuddi.softrobo_assesment.data.model.Country
import com.mutsuddi.softrobo_assesment.data.network.CountryApiService
import com.mutsuddi.softrobo_assesment.util.Resource
import com.mutsuddi.softrobo_assesment.util.safeApiCall
import javax.inject.Inject

class CountryRepository @Inject constructor(
    private val api: CountryApiService
) {
    suspend fun getCountries(): Resource<List<Country>> = safeApiCall {
        val response = api.getCountries()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                body.sortedBy { it.name }
            } else {
                throw Exception("Empty response body")
            }
        } else {
            throw Exception("${response.code()} ${response.message()}")
        }
    }
}