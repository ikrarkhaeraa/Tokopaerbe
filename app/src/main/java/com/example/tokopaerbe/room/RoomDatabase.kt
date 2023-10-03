//package com.example.tokopaerbe.room
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(
//    entities = [CartEntity::class, WishlistEntity::class, NotificationsEntity::class],
//    version = 2,
//    exportSchema = false,
//)
//abstract class ProductDatabase : RoomDatabase() {
//    abstract fun productDao(): CartDao
//    abstract fun wishlistDao(): WishlistDao
//    abstract fun notificationDao(): NotificationDao
//
//    companion object {
//        @Volatile
//        private var instance: ProductDatabase? = null
//        fun getInstance(context: Context): ProductDatabase =
//            instance ?: synchronized(this) {
//                instance ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    ProductDatabase::class.java,
//                    "productAdded.db"
//                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
//            }
//    }
//}
