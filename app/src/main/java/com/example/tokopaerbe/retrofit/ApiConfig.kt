package com.example.tokopaerbe.retrofit

import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//@AndroidEntryPoint
class ApiConfig {
    companion object{
        fun getApiService(): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//            val chucker = ChuckerInterceptor
            val client = OkHttpClient.Builder()
//                .addInterceptor(chucker)
                .addInterceptor(loggingInterceptor)
//                .authenticator(this, preferences)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://172.17.20.235:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}