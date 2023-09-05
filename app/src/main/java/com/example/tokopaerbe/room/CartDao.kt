package com.example.tokopaerbe.room

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import retrofit2.http.DELETE

@androidx.room.Dao
interface CartDao {

    @Query ("UPDATE productList SET isChecked = :isChecked WHERE productId = :id ")
    fun isChecked(id: String, isChecked:Boolean)

    @Query("UPDATE productList SET quantity = :quantity WHERE productId = :id ")
    fun quantity(id: String, quantity: Int)

    @Query("UPDATE productList SET isChecked = :isChecked")
    fun checkAll(isChecked: Boolean)

    @Query("UPDATE productList SET cartPrice = :cartPrice")
    fun cartPrice(cartPrice: Int)

    @Query("INSERT OR REPLACE INTO productList (productId, " +
            "productName," +
            " variantName, " +
            "stock, " +
            "productPrice, " +
            "quantity, " +
            "image, " +
            "isChecked, " +
            "cartPrice) values (:id, :productName, :variantName, :stock, :productPrice, :quantity, :image, :isChecked, :cartPrice)")
    fun addProduct(
        id: String,
        productName: String,
        variantName: String,
        stock: Int,
        productPrice: Int,
        quantity: Int,
        image: String,
        isChecked: Boolean,
        cartPrice: Int
    )

    @Query("DELETE FROM productList WHERE productId = :id")
    fun deleteProduct(id: String)

    @Delete
    suspend fun deleteAllCheckedProduct(cartEntity: List<CartEntity>)

    @Query("SELECT * FROM productList")
    fun getProduct(): LiveData<List<CartEntity>?>

}