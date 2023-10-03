package com.example.tokopaerbe.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "wishList")
class WishlistEntity(
    @field:ColumnInfo(name = "productId")
    @field:PrimaryKey
    val productId: String,

    @field:ColumnInfo("productName")
    val productName: String,

    @field:ColumnInfo("productPrice")
    val productPrice: Int,

    @field:ColumnInfo("image")
    val image: String,

    @field:ColumnInfo("store")
    val store: String,

    @field:SerializedName("productRating")
    val productRating: Float,

    @field:SerializedName("sale")
    val sale: Int,

    @field:ColumnInfo("stock")
    val stock: Int,

    @field:ColumnInfo("variantName")
    val variantName: String,

    @field:ColumnInfo("quantity")
    var quantity: Int = 1,

)
