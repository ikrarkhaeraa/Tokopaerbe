package com.example.tokopaerbe.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDatabaseTest : TestCase() {
    private lateinit var db: ProductDatabase
    private lateinit var cartDao: CartDao
    private lateinit var wishDao: WishlistDao
    private lateinit var notifDao: NotificationDao

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ProductDatabase::class.java).build()
        cartDao = db.productDao()
        wishDao = db.wishlistDao()
        notifDao = db.notificationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()




    //TEST CART DAO
    @Test
    fun testGetProductFromCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertNotNull(cartList)
            if (cartList != null) {
                assertTrue(cartList.isNotEmpty())
            }

            val cartEntity = cartList?.get(0)
            assertEquals(productId, cartEntity?.productId)
            assertEquals(productName, cartEntity?.productName)
            assertEquals(variantName, cartEntity?.variantName)
            assertEquals(stock, cartEntity?.stock)
            assertEquals(productPrice, cartEntity?.productPrice)
            assertEquals(quantity, cartEntity?.quantity)
            assertEquals(image, cartEntity?.image)
            assertEquals(isChecked, cartEntity?.isChecked)
        }
    }

    @Test
    fun testInsertAndDeleteCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )

            cartDao.deleteProduct(productId)

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertTrue(cartList.isNullOrEmpty())

        }
    }

    @Test
    fun testInsertAndGetForDetailCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )

            val cartEntity = cartDao.getCartForDetail(productId)

            assertEquals(productId, cartEntity?.productId)
            assertEquals(productName, cartEntity?.productName)
            assertEquals(variantName, cartEntity?.variantName)
            assertEquals(stock, cartEntity?.stock)
            assertEquals(productPrice, cartEntity?.productPrice)
            assertEquals(quantity, cartEntity?.quantity)
            assertEquals(image, cartEntity?.image)
            assertEquals(isChecked, cartEntity?.isChecked)
        }
    }

    @Test
    fun testInsertAndGetForWishlistCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )

            val cartEntity = cartDao.getCartForWishlist(productId)

            assertEquals(productId, cartEntity?.productId)
            assertEquals(productName, cartEntity?.productName)
            assertEquals(variantName, cartEntity?.variantName)
            assertEquals(stock, cartEntity?.stock)
            assertEquals(productPrice, cartEntity?.productPrice)
            assertEquals(quantity, cartEntity?.quantity)
            assertEquals(image, cartEntity?.image)
            assertEquals(isChecked, cartEntity?.isChecked)
        }
    }

    @Test
    fun testInsertDeleteAllCheckedProductAndGetCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(productId, productName, variantName, stock, productPrice, quantity, image, isChecked)

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertNotNull(cartList)
            if (cartList != null) {
                assertTrue(cartList.isNotEmpty())
            }

            val cartEntity = cartList?.get(0)

            cartDao.deleteAllCheckedProduct(listOf(cartEntity!!))

            assertEquals(productId, cartEntity.productId)

            val cartLiveData2 = cartDao.getProduct()

            val cartList2 = LiveDataTestUtil.getValue(cartLiveData2)

            assertTrue(cartList2.isNullOrEmpty())
        }
    }

    @Test
    fun testInsertAndCheckAllCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            var isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )
            isChecked = true
            cartDao.checkAll(isChecked)

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertNotNull(cartList)
            if (cartList != null) {
                assertTrue(cartList.isNotEmpty())
            }

            val cartEntity = cartList?.get(0)
            assertEquals(isChecked, cartEntity?.isChecked)
        }
    }

    @Test
    fun testInsertAndIsCheckedCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            val quantity = 2
            val image = "sample_image.jpg"
            var isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )
            isChecked = true
            cartDao.isChecked(productId, isChecked)

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertNotNull(cartList)
            if (cartList != null) {
                assertTrue(cartList.isNotEmpty())
            }

            val cartEntity = cartList?.get(0)
            assertEquals(productId, cartEntity?.productId)
            assertEquals(isChecked, cartEntity?.isChecked)
        }
    }

    @Test
    fun testInsertAndQuantityCartDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val variantName = "Sample Variant"
            val stock = 10
            val productPrice = 100
            var quantity = 2
            val image = "sample_image.jpg"
            val isChecked = false

            cartDao.addProduct(
                productId,
                productName,
                variantName,
                stock,
                productPrice,
                quantity,
                image,
                isChecked
            )
            quantity = 3
            cartDao.quantity(productId, quantity)

            val cartLiveData = cartDao.getProduct()

            val cartList = LiveDataTestUtil.getValue(cartLiveData)

            assertNotNull(cartList)
            if (cartList != null) {
                assertTrue(cartList.isNotEmpty())
            }

            val cartEntity = cartList?.get(0)
            assertEquals(productId, cartEntity?.productId)
            assertEquals(quantity, cartEntity?.quantity)
        }
    }





    //TEST WISHLIST DAO
    @Test
    fun testGetWishlistFromWishlistDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val productPrice = 100
            val image = "sample_image.jpg"
            val store = "Product Store"
            val rating = 5f
            val sale = 10
            val stock = 10
            val variantName = "Sample Variant"
            val quantity = 2

            wishDao.addWishList(
                productId,
                productName,
                productPrice,
                image,
                store,
                rating,
                sale,
                stock,
                variantName,
                quantity
            )

            val wishLiveData = wishDao.getWishList()

            val wishList = LiveDataTestUtil.getValue(wishLiveData)

            assertNotNull(wishList)
            if (wishList != null) {
                assertTrue(wishList.isNotEmpty())
            }

            val wishlistEntity = wishList?.get(0)
            assertEquals(productId, wishlistEntity?.productId)
            assertEquals(productName, wishlistEntity?.productName)
            assertEquals(productPrice, wishlistEntity?.productPrice)
            assertEquals(image, wishlistEntity?.image)
            assertEquals(store, wishlistEntity?.store)
            assertEquals(rating, wishlistEntity?.productRating)
            assertEquals(sale, wishlistEntity?.sale)
            assertEquals(stock,wishlistEntity?.stock)
            assertEquals(variantName, wishlistEntity?.variantName)
            assertEquals(quantity, wishlistEntity?.quantity)
        }
    }

    @Test
    fun testGetWishlistForDetailFromWishlistDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val productPrice = 100
            val image = "sample_image.jpg"
            val store = "Product Store"
            val rating = 5f
            val sale = 10
            val stock = 10
            val variantName = "Sample Variant"
            val quantity = 2

            wishDao.addWishList(
                productId,
                productName,
                productPrice,
                image,
                store,
                rating,
                sale,
                stock,
                variantName,
                quantity
            )

            val wishList = wishDao.getWishlistForDetail(productId)

            assertEquals(productId, wishList?.productId)
            assertEquals(productName, wishList?.productName)
            assertEquals(productPrice, wishList?.productPrice)
            assertEquals(image, wishList?.image)
            assertEquals(store, wishList?.store)
            assertEquals(rating, wishList?.productRating)
            assertEquals(sale, wishList?.sale)
            assertEquals(stock,wishList?.stock)
            assertEquals(variantName, wishList?.variantName)
            assertEquals(quantity, wishList?.quantity)
        }
    }

    @Test
    fun testGetIsFavoriteFromWishlistDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val productPrice = 100
            val image = "sample_image.jpg"
            val store = "Product Store"
            val rating = 5f
            val sale = 10
            val stock = 10
            val variantName = "Sample Variant"
            val quantity = 2

            wishDao.addWishList(
                productId,
                productName,
                productPrice,
                image,
                store,
                rating,
                sale,
                stock,
                variantName,
                quantity
            )

            val wishLiveData = wishDao.getIsFavorite(productId)

            val wishList = LiveDataTestUtil.getValue(wishLiveData)

            assertNotNull(wishList)
            if (wishList != null) {
                assertTrue(wishList.isNotEmpty())
            }

            val wishlistEntity = wishList?.get(0)
            assertEquals(productId, wishlistEntity?.productId)
            assertEquals(productName, wishlistEntity?.productName)
            assertEquals(productPrice, wishlistEntity?.productPrice)
            assertEquals(image, wishlistEntity?.image)
            assertEquals(store, wishlistEntity?.store)
            assertEquals(rating, wishlistEntity?.productRating)
            assertEquals(sale, wishlistEntity?.sale)
            assertEquals(stock,wishlistEntity?.stock)
            assertEquals(variantName, wishlistEntity?.variantName)
            assertEquals(quantity, wishlistEntity?.quantity)
        }
    }

    @Test
    fun testDeletWishListFromWishlistDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {
            val productId = "product123"
            val productName = "Sample Product"
            val productPrice = 100
            val image = "sample_image.jpg"
            val store = "Product Store"
            val rating = 5f
            val sale = 10
            val stock = 10
            val variantName = "Sample Variant"
            val quantity = 2

            wishDao.addWishList(
                productId,
                productName,
                productPrice,
                image,
                store,
                rating,
                sale,
                stock,
                variantName,
                quantity
            )

            wishDao.deleteWishList(productId)

            val wishLiveData = wishDao.getWishList()

            val wishList = LiveDataTestUtil.getValue(wishLiveData)

            assertTrue(wishList.isNullOrEmpty())
        }
    }





    //TEST NOTIFICATION DAO
    @Test
    fun testGetNotificationsFromNotifDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {

            val notifType = "Notif Type"
            val notifTitle = "Notif Title"
            val notifBody = "Notif Body"
            val notifDate = "NotifDate"
            val notifTime = "Notif Time"
            val notifImage = "Notif Image"
            val isChecked = false

            notifDao.addNotifications(
                notifType,
                notifTitle,
                notifBody,
                notifDate,
                notifTime,
                notifImage,
                isChecked
            )

            val notifLiveData = notifDao.getNotifications()

            val notif = LiveDataTestUtil.getValue(notifLiveData)

            assertNotNull(notif)
            if (notif != null) {
                assertTrue(notif.isNotEmpty())
            }

            val notifEntity  = notif?.get(0)
            assertEquals(notifType, notifEntity?.notifType)
            assertEquals(notifTitle, notifEntity?.notifTitle)
            assertEquals(notifBody, notifEntity?.notifBody)
            assertEquals(notifDate, notifEntity?.notifDate)
            assertEquals(notifTime, notifEntity?.notifTime)
            assertEquals(notifImage, notifEntity?.notifImage)
            assertEquals(isChecked, notifEntity?.isChecked)
        }
    }

    @Test
    fun testGetUnreadNotificationsFromNotifDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {

            val notifType = "Notif Type"
            val notifTitle = "Notif Title"
            val notifBody = "Notif Body"
            val notifDate = "NotifDate"
            val notifTime = "Notif Time"
            val notifImage = "Notif Image"
            val isChecked = false

            notifDao.addNotifications(
                notifType,
                notifTitle,
                notifBody,
                notifDate,
                notifTime,
                notifImage,
                isChecked
            )

            val notifLiveData = notifDao.getUnreadNotifications(isChecked)

            val notif = LiveDataTestUtil.getValue(notifLiveData)

            assertNotNull(notif)
            if (notif != null) {
                assertTrue(notif.isNotEmpty())
            }

            val notifEntity  = notif?.get(0)
            assertEquals(notifType, notifEntity?.notifType)
            assertEquals(notifTitle, notifEntity?.notifTitle)
            assertEquals(notifBody, notifEntity?.notifBody)
            assertEquals(notifDate, notifEntity?.notifDate)
            assertEquals(notifTime, notifEntity?.notifTime)
            assertEquals(notifImage, notifEntity?.notifImage)
            assertEquals(isChecked, notifEntity?.isChecked)
        }
    }

    @Test
    fun testNotifIsCheckedFromNotifDao() = runBlocking {
        // Use withContext to switch to a background coroutine context
        withContext(Dispatchers.IO) {

            val notifId = 0
            val notifType = "Notif Type"
            val notifTitle = "Notif Title"
            val notifBody = "Notif Body"
            val notifDate = "NotifDate"
            val notifTime = "Notif Time"
            val notifImage = "Notif Image"
            val isChecked = false

            notifDao.addNotifications(
                notifType,
                notifTitle,
                notifBody,
                notifDate,
                notifTime,
                notifImage,
                isChecked
            )

            notifDao.notifIsChecked(notifId, isChecked)

            val notifLiveData = notifDao.getNotifications()

            val notifications = LiveDataTestUtil.getValue(notifLiveData)

            assertNotNull(notifications)
            if (notifications != null) {
                assertTrue(notifications.isNotEmpty())
            }

            val notifEntity = notifications?.get(0)
            assertEquals(notifTitle, notifEntity?.notifTitle)
            assertEquals(isChecked, notifEntity?.isChecked)
        }
    }

}