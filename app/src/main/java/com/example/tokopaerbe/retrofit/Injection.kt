package com.example.tokopaerbe.retrofit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tokopaerbe.pagging.PaggingRepository
import com.example.tokopaerbe.room.CartDatabase

private val Context.database: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): DataSource {
        val preferences = UserPreferences.getInstance(context.database)
        val database = CartDatabase.getInstance(context)
        val dao = database.productDao()
        val wishDao = database.wishlistDao()
        return DataSource.getInstance(preferences, dao, wishDao)
    }

    fun providePaging(context: Context): PaggingRepository {
//        val database = PaggingDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val preferences = UserPreferences.getInstance(context.database)
        return PaggingRepository(apiService, preferences)
    }

//    fun provideApiConfig(context: Context): ApiConfig {
//        val preferences = UserPreferences.getInstance(context.database)
//        return ApiConfig(preferences)
//    }
}