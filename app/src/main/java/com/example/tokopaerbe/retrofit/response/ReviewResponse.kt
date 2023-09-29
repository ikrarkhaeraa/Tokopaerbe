package com.example.tokopaerbe.retrofit.response

import com.google.gson.annotations.SerializedName

data class ReviewResponse(

    @field:SerializedName("data")
    val data: List<Review>,

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String
)

data class Review(
    @field:SerializedName("userName")
    val userName: String,

    @field:SerializedName("userImage")
    val userImage: String,

    @field:SerializedName("userRating")
    val userRating: Int,

    @field:SerializedName("userReview")
    val userReview: String,
)
