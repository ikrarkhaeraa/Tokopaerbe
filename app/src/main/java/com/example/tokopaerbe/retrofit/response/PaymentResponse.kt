package com.example.tokopaerbe.retrofit.response

import com.google.gson.annotations.SerializedName

data class PaymentResponse (

    @field:SerializedName("data")
    val data: List<Payment>,

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String
)

data class Payment (
    @field:SerializedName("title")
    val title : String,

    @field:SerializedName("item")
    val item : List<PaymentMethod>
)

data class PaymentMethod (

    @field:SerializedName("label")
    val label : String,

    @field:SerializedName("image")
    val image : String,

    @field:SerializedName("status")
    val status : Boolean
)
