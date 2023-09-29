package com.example.tokopaerbe.retrofit.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("data")
    val data: DataRegister,

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String
)

data class DataRegister(

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("refreshToken")
    val refreshToken: String,

    @field:SerializedName("expiresAt")
    val expiresAt: Long,
)
