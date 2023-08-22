package com.example.tokopaerbe.retrofit

import com.example.tokopaerbe.retrofit.response.LoginResponse
import com.example.tokopaerbe.retrofit.response.ProfileResponse
import com.example.tokopaerbe.retrofit.response.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("register")
    fun uploadDataRegister(
        @Header("API_KEY") apiKey: String,
        @Body requestBody: RegisterRequestBody
    ): Call<RegisterResponse>

    @POST("login")
    fun uploadDataLogin(
        @Header("API_KEY") apiKey: String,
        @Body requestBody: LoginRequestBody
    ): Call<LoginResponse>

    @Multipart
    @POST("profile")
    fun uploadDataProfile(
        @Header("Authorization") auth: String,
        @Part text: MultipartBody.Part,
        @Part image: MultipartBody.Part
    ): Call<ProfileResponse>
}