//package com.example.tokopaerbe.retrofit.response
//
//import com.google.gson.annotations.SerializedName
//
//data class ProductsResponse(
//
//    @field:SerializedName("data")
//    val data: DataProduct,
//
//    @field:SerializedName("code")
//    val code: Int,
//
//    @field:SerializedName("message")
//    val message: String
//)
//
//data class DataProduct(
//    @field:SerializedName("items")
//    val items: List<Product>,
//
//    @field:SerializedName("itemsPerPage")
//    val itemsPerPage: Int,
//
//    @field:SerializedName("currentItemCount")
//    val currentItemCount: String,
//
//    @field:SerializedName("pageIndex")
//    val pageIndex: Int,
//
//    @field:SerializedName("totalPages")
//    val totalPages: Int,
//)
//
//data class Product(
//
//    @field:SerializedName("productId")
//    val productId: String,
//
//    @field:SerializedName("productName")
//    val productName: String,
//
//    @field:SerializedName("productPrice")
//    val productPrice: Int,
//
//    @field:SerializedName("image")
//    val image: String,
//
//    @field:SerializedName("brand")
//    val brand: String,
//
//    @field:SerializedName("store")
//    val store: String,
//
//    @field:SerializedName("sale")
//    val sale: Int,
//
//    @field:SerializedName("productRating")
//    val productRating: Float,
//)
