package com.example.tokopaerbe.core.retrofit.user

data class UserRegister(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)
