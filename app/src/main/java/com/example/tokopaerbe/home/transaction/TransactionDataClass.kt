package com.example.tokopaerbe.home.transaction

import android.os.Parcelable
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionDataClass(
    val invoiceId: String,
    val StatusValue: String,
    val tanggalValue: String,
    val waktuValue: String,
    val metodePembayaranValue: String,
    val totalPembayaranValue: Int
) : Parcelable

@Parcelize
data class ItemTransaction (
    val itemTransaction: List<TransactionDataClass>
) : Parcelable