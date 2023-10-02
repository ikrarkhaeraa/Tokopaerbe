package com.example.tokopaerbe.home.checkout

import android.os.Parcelable
import android.support.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CheckoutDataClass(
    var productId: String,
    var productImage: String,
    var productName: String,
    var productVariant: String,
    var productStock: Int,
    var productPrice: Int,
    var productQuantity: Int
) : Parcelable

@Keep
@Parcelize
data class ListCheckout(
    val listCheckout: List<CheckoutDataClass>
) : Parcelable
