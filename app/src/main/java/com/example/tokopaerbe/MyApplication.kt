package com.example.tokopaerbe

import Authenticator
import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tokopaerbe.retrofit.ApiConfig
import com.example.tokopaerbe.retrofit.ApiService
import com.example.tokopaerbe.retrofit.UserPreferences
import com.google.firebase.FirebaseApp
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

    @Inject
    lateinit var chucker: Context

    @InternalCoroutinesApi
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val chuckerInterceptor = ChuckerInterceptor.Builder(this)
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(Authenticator(preferences, chucker))
            .build()

        ApiConfig.initialize(client)
    }

}
