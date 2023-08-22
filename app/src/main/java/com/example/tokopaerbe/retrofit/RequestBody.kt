package com.example.tokopaerbe.retrofit

import okhttp3.MultipartBody
import retrofit2.http.Part

data class RegisterRequestBody(
    val email: String,
    val password: String,
    val firebaseToken: String
)

data class LoginRequestBody(
    val email: String,
    val password: String,
    val firebaseToken: String
)

//data class ProfileRequestBody(
//    @Part val text: MultipartBody.Part,
//    @Part val image: MultipartBody.Part
//)