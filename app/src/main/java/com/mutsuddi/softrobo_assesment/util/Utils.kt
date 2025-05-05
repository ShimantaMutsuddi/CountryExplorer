package com.mutsuddi.softrobo_assesment.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> = withContext(Dispatchers.IO) {
    return@withContext try {
        val result = apiCall()
        Resource.Success(result)
    } catch (e: IOException) {
        Resource.Error("Network error: ${e.localizedMessage}")
    } catch (e: HttpException) {
        Resource.Error("HTTP error: ${e.code()} ${e.message()}")
    } catch (e: Exception) {
        Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}")
    }
}

object NetworkUtils {
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}