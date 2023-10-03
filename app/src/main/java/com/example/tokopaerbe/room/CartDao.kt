//package com.example.tokopaerbe.room
//
//import androidx.lifecycle.LiveData
//import androidx.room.Delete
//import androidx.room.Query
//
//@androidx.room.Dao
//interface CartDao {
//
//    @Query("UPDATE productList SET isChecked = :isChecked WHERE productId = :id ")
//    fun isChecked(id: String, isChecked: Boolean)
//
//    @Query("UPDATE productList SET quantity = :quantity WHERE productId = :id ")
//    fun quantity(id: String, quantity: Int)
//
//    @Query("UPDATE productList SET isChecked = :isChecked")
//    fun checkAll(isChecked: Boolean)
//
//    @Query(
//        "INSERT OR REPLACE INTO productList (productId, " +
//            "productName," +
//            " variantName, " +
//            "stock, " +
//            "productPrice, " +
//            "quantity, " +
//            "image, " +
//            "isChecked) values (:id, :productName, :variantName, :stock, :productPrice, :quantity, :image, :isChecked)"
//    )
//    fun addProduct(
//        id: String,
//        productName: String,
//        variantName: String,
//        stock: Int,
//        productPrice: Int,
//        quantity: Int,
//        image: String,
//        isChecked: Boolean,
//    )
//
//    @Query("DELETE FROM productList WHERE productId = :id")
//    fun deleteProduct(id: String)
//
//    @Query("DELETE FROM productList")
//    fun deleteAllCart()
//
//    @Delete
//    suspend fun deleteAllCheckedProduct(cartEntity: List<CartEntity>)
//
//    @Query("SELECT * FROM productList")
//    fun getProduct(): LiveData<List<CartEntity>?>
//
//    @Query("SELECT * FROM productList WHERE productId = :id")
//    suspend fun getCartForDetail(id: String): CartEntity?
//
//    @Query("SELECT * FROM productList WHERE productId = :id")
//    suspend fun getCartForWishlist(id: String): CartEntity?
//}
