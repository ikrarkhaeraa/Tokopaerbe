package com.example.tokopaerbe.core.retrofit.user

data class UserFilter(
    val search: String?,
    val sort: String?,
    val brand: String?,
    val lowest: Int?,
    val highest: Int?,
)
