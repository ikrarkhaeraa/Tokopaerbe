package com.example.tokopaerbe.retrofit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.example.tokopaerbe.room.CartDao
import com.example.tokopaerbe.room.NotificationDao
import com.example.tokopaerbe.room.ProductDatabase
import com.example.tokopaerbe.room.WishlistDao
import com.example.tokopaerbe.viewmodel.ViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Hilt {

    @Singleton
    @Provides
    fun provideChucker(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.198:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.database
    }

    @Singleton
    @Provides
    fun provideViewModel(data: DataSource): ViewModel {
        return ViewModel(data)
    }

    @Provides
    fun provideSharedDataSource(
        preferences: UserPreferences,
        cartDao: CartDao,
        wishlistDao: WishlistDao,
        notificationDao: NotificationDao
    ): DataSource {
        return DataSource(preferences, cartDao, wishlistDao, notificationDao)
    }

    @Singleton
    @Provides
    fun providecartDao(database: ProductDatabase): CartDao {
        return database.productDao()
    }

    @Singleton
    @Provides
    fun providewishDao(database: ProductDatabase): WishlistDao {
        return database.wishlistDao()
    }

    @Singleton
    @Provides
    fun providenotifDao(database: ProductDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ProductDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ProductDatabase::class.java,
            "productAdded.db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }
}
