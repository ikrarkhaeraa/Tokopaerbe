//package com.example.tokopaerbe.core.retrofit
//
//import android.content.Context
//import com.chuckerteam.chucker.api.ChuckerInterceptor
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Inject
//
//class ApiConfig {
//    companion object {
//        private lateinit var apiService: ApiService
//
//        fun initialize(okHttpClient: OkHttpClient) {
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://192.168.1.26:5000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build()
//
//            apiService = retrofit.create(ApiService::class.java)
//        }
//
//        fun getApiService(): ApiService {
//            return apiService
//        }
//    }
//}
