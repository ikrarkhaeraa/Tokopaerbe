package com.example.tokopaerbe

import Authenticator
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tokopaerbe.retrofit.ApiConfig
import com.example.tokopaerbe.retrofit.ApiService
import com.example.tokopaerbe.retrofit.UserPreferences
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var preferences: UserPreferences

    @InternalCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val chuckerInterceptor = ChuckerInterceptor.Builder(this)
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(Authenticator(apiService, preferences))
            .build()

        ApiConfig.initialize(client)
    }

}
