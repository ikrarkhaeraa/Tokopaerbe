package com.example.tokopaerbe.retrofit

import okhttp3.MultipartBody
import retrofit2.http.Part
import retrofit2.http.Query

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

data class RefreshRequestBody(
    val token: String,
)

data class FulfillmentRequestBody(
    val payment: String,
    val items: List<Item>
)

data class Item (
    val productId: String,
    val variantName: String,
    val quantity: Int
)

data class RatingRequestBody(
    val invoiceId: String,
    val rating: Int,
    val review: String
)