package com.example.tokopaerbe.core.utils

sealed class SealedClass<out T> {
    object Init : SealedClass<Nothing>()
    data class Success<out T>(val data: T) : SealedClass<T>()
    object Loading : SealedClass<Nothing>()
    data class Error(val message: Throwable) : SealedClass<Nothing>()
}


