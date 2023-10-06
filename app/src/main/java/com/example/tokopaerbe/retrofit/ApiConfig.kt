// package com.example.tokopaerbe.retrofit
//
// import okhttp3.OkHttpClient
// import retrofit2.Retrofit
// import retrofit2.converter.gson.GsonConverterFactory
//
// class ApiConfig {
//    companion object {
//        private lateinit var apiService: ApiService
//
//        fun initialize(okHttpClient: OkHttpClient) {
//            val retrofit = Retrofit.Builder()
//                .baseUrl("http://172.17.20.114:5000/")
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
// }
