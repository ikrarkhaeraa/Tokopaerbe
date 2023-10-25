package com.example.tokopaerbe.retrofit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tokopaerbe.core.retrofit.ApiService
import com.example.tokopaerbe.core.retrofit.Authenticator
import com.example.tokopaerbe.core.retrofit.DataSource
import com.example.tokopaerbe.core.retrofit.UserPreferences
import com.example.tokopaerbe.core.room.CartDao
import com.example.tokopaerbe.core.room.NotificationDao
import com.example.tokopaerbe.core.room.ProductDatabase
import com.example.tokopaerbe.core.room.WishlistDao
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Hilt {

    private val Context.database: DataStore<Preferences> by preferencesDataStore("token")

    @Singleton
    @Provides
    fun provideChucker(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideAuthenticator(userPreferences: UserPreferences, context: Context): Authenticator {
        return Authenticator(userPreferences, context)
    }

    @Singleton
    @Provides
    fun provideApiService(context: Context, authenticator: Authenticator): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .build()

        val client = OkHttpClient.Builder()
            .authenticator(authenticator)
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.18.5:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
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
        notificationDao: NotificationDao,
        apiService: ApiService
    ): DataSource {
        return DataSource(preferences, cartDao, wishlistDao, notificationDao, apiService)
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
