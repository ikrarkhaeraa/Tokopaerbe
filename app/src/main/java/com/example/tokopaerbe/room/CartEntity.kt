package com.example.tokopaerbe.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productList")
class CartEntity(
    @field:ColumnInfo(name = "productId")
    @field:PrimaryKey
    val productId: String,

    @field:ColumnInfo("productName")
    val productName: String,

    @field:ColumnInfo("productPrice")
    val productPrice: Int,

    @field:ColumnInfo("image")
    val image: String,

    @field:ColumnInfo("stock")
    val stock: Int,

    @field:ColumnInfo("variantName")
    val variantName: String,

    @field:ColumnInfo("quantity")
    var quantity: Int = 1,

    @field:ColumnInfo("isChecked")
    var isChecked: Boolean = false,
)
