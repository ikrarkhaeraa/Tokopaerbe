package com.example.tokopaerbe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tokopaerbe.core.retrofit.DataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
    private val dataSource: DataSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(com.example.tokopaerbe.viewmodel.ViewModel::class.java) -> {
                ViewModel(dataSource) as T
            }

            else -> throw IllegalArgumentException("Unknown Model class: " + modelClass.name)
        }
    }
}

