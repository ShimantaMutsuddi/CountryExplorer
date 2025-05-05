package com.mutsuddi.softrobo_assesment.di

import com.mutsuddi.softrobo_assesment.data.network.CountryApiService
import com.mutsuddi.softrobo_assesment.data.repository.CountryRepository
import com.mutsuddi.softrobo_assesment.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModules {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    @Provides
    @Singleton
    fun provideCountryApiService(retrofit: Retrofit): CountryApiService =
        retrofit.create(CountryApiService::class.java)

    @Provides
    @Singleton
    fun provideCountryRepository(api: CountryApiService): CountryRepository =
        CountryRepository(api)
}