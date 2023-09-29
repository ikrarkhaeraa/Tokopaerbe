package com.example.tokopaerbe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tokopaerbe.retrofit.DataSource
import com.example.tokopaerbe.retrofit.Injection

class ViewModelFactory(private val dataSource: DataSource) :
    ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }

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
