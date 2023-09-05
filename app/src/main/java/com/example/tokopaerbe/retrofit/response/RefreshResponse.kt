package com.example.tokopaerbe.retrofit.response

import com.google.gson.annotations.SerializedName

data class RefreshResponse(

    @field:SerializedName("data")
    val data: DataRefresh,

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String
)

data class DataRefresh(

    @field:SerializedName("userName")
    val accessToken: String,

    @field:SerializedName("userImage")
    val refreshToken: String,

    @field:SerializedName("userImage")
    val expiresAt: Long,
    )