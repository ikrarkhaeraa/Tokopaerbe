package com.example.tokopaerbe.home.checkout

import android.os.Parcelable
import android.support.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class FulfillmentDataClass(
    var transactionId: String,
    var statusValue: Boolean,
    var dateValue: String,
    var timeValue: String,
    var paymentMethod: String,
    var totalPrice: Int,
) : Parcelable

@Keep
@Parcelize
data class ListFulfillmentData(
    val listFulfillmentData: List<FulfillmentDataClass>
) : Parcelable