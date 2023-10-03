package com.example.tokopaerbe.core.retrofit.user

data class UserLogin(
    val userName: String,
    val userImage: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
)
