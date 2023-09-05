package com.example.tokopaerbe.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class, WishlistEntity::class],
    version = 2,
    exportSchema = false,)
abstract class CartDatabase : RoomDatabase() {
    abstract fun productDao(): CartDao
    abstract fun wishlistDao(): WishlistDao

    companion object {
        @Volatile
        private var instance: CartDatabase? = null
        fun getInstance(context: Context): CartDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java, "productAdded.db"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
    }
}