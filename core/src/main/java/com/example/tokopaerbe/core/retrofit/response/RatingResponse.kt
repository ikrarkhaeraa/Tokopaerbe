package com.example.tokopaerbe.core.retrofit.response

import com.google.gson.annotations.SerializedName

data class RatingResponse(
    @field:SerializedName("code")
    val code: String,

    @field:SerializedName("message")
    val message: String
)
