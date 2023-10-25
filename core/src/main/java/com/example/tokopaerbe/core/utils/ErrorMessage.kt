package com.example.tokopaerbe.core.utils

import com.example.tokopaerbe.core.retrofit.ErrorResponse
import com.example.tokopaerbe.core.utils.ErrorMessage.errorMessage
import com.google.gson.Gson
import okio.IOException
import retrofit2.HttpException

object ErrorMessage {
    fun Throwable.errorMessage(): String {
        return when (this) {
            is HttpException -> {
                val response = this.response()?.errorBody()?.string()
                val error = Gson().fromJson(response, ErrorResponse::class.java)
                error.message
            }
            is IOException -> {
                "No Internet Connection"
            }
            else -> {
                "Undefined Error"
            }
        }
    }
}