package com.example.tokopaerbe.core.retrofit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.tokopaerbe.core.pagging.PaggingRepository
import com.example.tokopaerbe.core.room.ProductDatabase

val Context.database: DataStore<Preferences> by preferencesDataStore("token")

object Injection {

    fun provideRepository(context: Context): DataSource {
        val preferences = UserPreferences(context.database)
        val database = ProductDatabase.getInstance(context)
        val dao = database.productDao()
        val wishDao = database.wishlistDao()
        val notifDao = database.notificationDao()
        return DataSource.getInstance(preferences, dao, wishDao, notifDao)
    }

    fun providePaging(context: Context): PaggingRepository {
        val apiService = ApiConfig.getApiService()
        val preferences = UserPreferences(context.database)
        return PaggingRepository(apiService, preferences)
    }
}
