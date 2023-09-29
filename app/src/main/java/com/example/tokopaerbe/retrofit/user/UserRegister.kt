package com.example.tokopaerbe.retrofit.user

data class UserRegister(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)
