package com.example.tokopaerbe.retrofit

data class UserSession(
    val userName: String,
    val userImage: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val isLogin: Boolean,
    val isfirstInstall: Boolean
)