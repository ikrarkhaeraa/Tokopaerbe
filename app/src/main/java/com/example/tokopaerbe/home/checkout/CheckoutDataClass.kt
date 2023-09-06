package com.example.tokopaerbe.home.checkout

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckoutDataClass (
    var productImage: String,
    var productName: String,
    var productVariant: String,
    var productStock: Int,
    var productPrice: Int,
    var productQuantity: Int
) : Parcelable

@Parcelize
data class ListCheckout (
    val listCheckout: List<CheckoutDataClass>
) : Parcelable