package com.example.tokopaerbe.room

import androidx.lifecycle.LiveData
import androidx.room.Query

@androidx.room.Dao
interface WishlistDao {

    @Query(
        "INSERT OR REPLACE INTO wishList (productId, " +
            "productName," +
            "productPrice, " +
            "image, " +
            "store, " +
            "productRating, " +
            "sale, " +
            "stock, " +
            "variantName, " +
            "quantity) values (:id, :productName, :productPrice, :image, :store, :productRating, :sale, :stock, :variantName, :quantity)"
    )
    fun addWishList(
        id: String,
        productName: String,
        productPrice: Int,
        image: String,
        store: String,
        productRating: Float,
        sale: Int,
        stock: Int,
        variantName: String,
        quantity: Int
    )

    @Query("DELETE FROM wishList WHERE productId = :id")
    fun deleteWishList(id: String)

    @Query("SELECT * FROM wishList")
    fun getWishList(): LiveData<List<WishlistEntity>?>

    @Query("SELECT * FROM wishList WHERE productId = :id")
    fun getIsFavorite(id: String): LiveData<List<WishlistEntity>?>

    @Query("SELECT * FROM wishList WHERE productId = :id")
    suspend fun getWishlistForDetail(id: String): WishlistEntity?
}
