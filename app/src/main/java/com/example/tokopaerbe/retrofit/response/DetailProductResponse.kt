package com.example.tokopaerbe.retrofit.response

import com.google.gson.annotations.SerializedName

data class DetailProductResponse(

    @field:SerializedName("data")
    val data: DetailProduct,

    @field:SerializedName("code")
    val code: Int,

    @field:SerializedName("message")
    val message: String
)

data class DetailProduct(
    @field:SerializedName("productId")
    val productId: String,

    @field:SerializedName("productName")
    val productName: String,

    @field:SerializedName("productPrice")
    val productPrice: Int,

    @field:SerializedName("image")
    val image: List<String>,

    @field:SerializedName("brand")
    val brand: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("store")
    val store: String,

    @field:SerializedName("sale")
    val sale: Int,

    @field:SerializedName("stock")
    val stock: Int,

    @field:SerializedName("totalRating")
    val totalRating: Int,

    @field:SerializedName("totalReview")
    val totalReview: Int,

    @field:SerializedName("totalSatisfaction")
    val totalSatisfaction: Int,

    @field:SerializedName("productRating")
    val productRating: Float,

    @field:SerializedName("productVariant")
    val productVariant: List<ProductVariant>
)

data class ProductVariant(

    @field:SerializedName("variantName")
    val variantName: String,

    @field:SerializedName("variantPrice")
    val variantPrice: Int
)
