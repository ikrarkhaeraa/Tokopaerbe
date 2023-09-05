package com.example.tokopaerbe.home.store

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filter(
    var search: String?,
    var sort: String?,
    var brand: String?,
    var textTerendah: String?,
    var textTertinggi: String?
) : Parcelable