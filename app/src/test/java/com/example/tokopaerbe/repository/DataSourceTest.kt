package com.example.tokopaerbe.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.tokopaerbe.core.retrofit.ApiService
import com.example.tokopaerbe.core.retrofit.DataSource
import com.example.tokopaerbe.core.retrofit.FulfillmentRequestBody
import com.example.tokopaerbe.core.retrofit.LoginRequestBody
import com.example.tokopaerbe.core.retrofit.RatingRequestBody
import com.example.tokopaerbe.core.retrofit.RegisterRequestBody
import com.example.tokopaerbe.core.retrofit.UserPreferences
import com.example.tokopaerbe.core.retrofit.response.DataLogin
import com.example.tokopaerbe.core.retrofit.response.DataRegister
import com.example.tokopaerbe.core.retrofit.response.Fulfillment
import com.example.tokopaerbe.core.retrofit.response.FulfillmentResponse
import com.example.tokopaerbe.core.retrofit.response.LoginResponse
import com.example.tokopaerbe.core.retrofit.response.RegisterResponse
import com.example.tokopaerbe.core.retrofit.response.SearchResponse
import com.example.tokopaerbe.core.room.CartDao
import com.example.tokopaerbe.core.room.NotificationDao
import com.example.tokopaerbe.core.room.WishlistDao
import com.example.tokopaerbe.retrofit.MockResponseFileReader
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.mockwebserver.MockResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class DataSourceTest {

    private lateinit var dataSource: DataSource
    private lateinit var apiService: ApiService

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    @Before
    fun setUp() {
        val preferences = mock(UserPreferences::class.java)
        val cartDao = mock(CartDao::class.java)
        val wishDao = mock(WishlistDao::class.java)
        val notifDao = mock(NotificationDao::class.java)
        apiService = mock(ApiService::class.java)

        dataSource = DataSource(preferences, cartDao, wishDao, notifDao, apiService)
    }


    @Test
    fun testUploadRegisterData() = runTest {
        val apiKey = "your_api_key"
        val email = "test@example.com"
        val password = "test_password"
        val firebaseToken = "firebase_token"
        val expectedResponse = RegisterResponse(
            DataRegister(
                "","",0L
            ),
            200,
            "OK"
        )
        val requestBody = RegisterRequestBody(email, password, firebaseToken)
        whenever(apiService.uploadDataRegister(apiKey, requestBody)).thenReturn(expectedResponse)
        val result = dataSource.uploadRegisterData(apiKey, requestBody).first()
        assertEquals(expectedResponse, result)
    }

    @Test
    fun testUploadLoginData() = runTest {
        val apiKey = "your_api_key"
        val email = "test@example.com"
        val password = "test_password"
        val firebaseToken = "firebase_token"
        val expectedResponse = LoginResponse(
            DataLogin(
                "","","","",0L
            ),
            200,
            "OK"
        )
        val requestBody = LoginRequestBody(email, password, firebaseToken)
        whenever(apiService.uploadDataLogin(apiKey, requestBody)).thenReturn(expectedResponse)
        val result = dataSource.uploadLoginData(apiKey, requestBody).first()
        assertEquals(expectedResponse, result)
    }

    @Test
    fun testUploadProfileData() = runTest {
        val auth = "auth"
        val text = MultipartBody.Part.createFormData("userName", "userName")
        val image = MultipartBody.Part.createFormData("userImage", "userImage")

        apiService.uploadDataProfile(auth, text, image)
        backgroundScope.launch {
            val data = dataSource.profile.first()
            assertEquals(200, data.code)
        }
    }

    @Test
    fun testUploadSearchData() = runTest {
        val auth = "auth"
        val query = "query"
        val expectedResponse = SearchResponse(
            listOf(""),
            200,
            "OK"
        )
        whenever(apiService.uploadDataSearch(auth, query)).thenReturn(expectedResponse)
        val result = dataSource.uploadSearchData(auth, query).first()
        assertEquals(expectedResponse, result)
    }

    @Test
    fun testGetReviewData() = runTest {
        val auth = "auth"
        val id = "id"

        apiService.getReviewData(auth, id)
        backgroundScope.launch {
            val data = dataSource.review.getOrAwaitValue()
            assertEquals(200, data.code)
        }
    }

    @Test
    fun testGetDetailProductData() = runTest {
        val auth = "auth"
        val id = "id"

        apiService.getDetailProductData(auth, id)
        backgroundScope.launch {
            val data = dataSource.detail.getOrAwaitValue()
            assertEquals(200, data.code)
        }
    }

    @Test
    fun testUploadFulfillmentData() = runTest {
        val auth = "auth"
        val payment = "payment"
        val expectedResponse = FulfillmentResponse(
            Fulfillment("", true, "","","", 10000000),
            200,
            "OK"
        )
        val requestBody = FulfillmentRequestBody(payment, mock())
        whenever(apiService.uploadDataFulfillment(auth, requestBody)).thenReturn(expectedResponse)
        val result = dataSource.uploadFulfillmentData(auth, requestBody).first()
        assertEquals(expectedResponse, result)
    }

    @Test
    fun testUploadRatingData() = runTest {
        val auth = "auth"
        val invoiceId = "invoiceId"
        val rating = 5
        val review = "review"
        val requestBody = RatingRequestBody(invoiceId, rating, review)

        apiService.uploadDataRating(auth, requestBody)
        backgroundScope.launch {
            val data = dataSource.rating.getOrAwaitValue()
            assertEquals(200, data.code)
        }
    }

    @Test
    fun testGetTransactionData() = runTest {
        val auth = "auth"

        apiService.getTransactionData(auth)
        backgroundScope.launch {
            val data = dataSource.transaction.getOrAwaitValue()
            assertEquals(200, data.code)
        }
    }

    @Test
    fun testFirstInstallState() = runTest {
        val state = true
        dataSource.userInstall()
        backgroundScope.launch {
            val data = dataSource.getUserFirstInstallState().first()
            assertEquals(!state, data)
        }
    }

    @Test
    fun testLoginState() = runTest {
        val state = true
        dataSource.userLogin()
        backgroundScope.launch {
            val data = dataSource.getUserLoginState().first()
            assertEquals(!state, data)
        }
    }

    @Test
    fun testDarkThemeState() = runTest {
        val state = false
        dataSource.darkTheme(true)
        backgroundScope.launch {
            val data = dataSource.getIsDarkState().first()
            assertEquals(!state, data)
        }
    }

    @Test
    fun testAddProductCartandDelete1() = runTest {
        val id = ""
        val productName = "productName"
        val variantName = "variantName"
        val stock = 1
        val productPrice = 1000000
        val quantity = 2
        val image = "string"
        val isChecked = false
        dataSource.addProductCart(
            id,
            productName,
            variantName,
            stock,
            productPrice,
            quantity,
            image,
            isChecked
        )
        dataSource.deleteProductCart(id)
        backgroundScope.launch {
            val data = dataSource.getProductCart().getOrAwaitValue()
            assertEquals(null, data)
        }
    }

    @Test
    fun testAddProductCartandDeleteAll() = runTest {
        val id = ""
        val productName = "productName"
        val variantName = "variantName"
        val stock = 1
        val productPrice = 1000000
        val quantity = 2
        val image = "string"
        val isChecked = false
        dataSource.addProductCart(
            id,
            productName,
            variantName,
            stock,
            productPrice,
            quantity,
            image,
            isChecked
        )
        dataSource.deleteAllCheckedProduct(mock())
        backgroundScope.launch {
            val data = dataSource.getProductCart().getOrAwaitValue()
            assertEquals(null, data)
        }
    }

    @Test
    fun testAddWishlistAndDelete() = runTest {
        val id = ""
        val productName = "productName"
        val variantName = "variantName"
        val stock = 1
        val productPrice = 1000000
        val quantity = 2
        val image = "string"
        val isChecked = false
        val store = "store"
        val productRating = 5f
        val sale = 2
        dataSource.addWishList(
            id,
            productName,
            productPrice,
            image,
            store,
            productRating,
            sale,
            stock,
            variantName,
            quantity
        )
        dataSource.deleteWishList(id)
        backgroundScope.launch {
            val data = dataSource.getWishList().getOrAwaitValue()
            assertEquals(null, data)
        }
    }

    @Test
    fun testAddNotificationsAndGet() = runTest {
        val notifId = 0
        val notifType = "notifType"
        val notifTitle = "notifTitle"
        val notifBody = "notifBody"
        val notifDate = "notifDate"
        val notifTime = "notifTime"
        val notifImage = "notifImage"
        val isChecked = false
        dataSource.addNotifications(
            notifId,
            notifType,
            notifTitle,
            notifBody,
            notifDate,
            notifTime,
            notifImage,
            isChecked
        )
        backgroundScope.launch {
            val data = dataSource.getNotification().getOrAwaitValue()
            assertEquals(notifType, data?.get(0)?.notifType)
            assertEquals(notifTitle, data?.get(0)?.notifTitle)
            assertEquals(notifBody, data?.get(0)?.notifBody)
            assertEquals(notifDate, data?.get(0)?.notifDate)
            assertEquals(notifTime, data?.get(0)?.notifTime)
            assertEquals(notifImage, data?.get(0)?.notifImage)
            assertEquals(isChecked, data?.get(0)?.isChecked)
        }
    }

    @Test
    fun testGetUnreadNotifications() = runTest {
        val notifId = 0
        val notifType = "notifType"
        val notifTitle = "notifTitle"
        val notifBody = "notifBody"
        val notifDate = "notifDate"
        val notifTime = "notifTime"
        val notifImage = "notifImage"
        val isChecked = false
        dataSource.addNotifications(
            notifId,
            notifType,
            notifTitle,
            notifBody,
            notifDate,
            notifTime,
            notifImage,
            isChecked
        )

        backgroundScope.launch {
            val data = dataSource.getUnReadNotifications(false).getOrAwaitValue()
            assertEquals(1, data?.size)
        }
    }

    @Test
    fun testReadNotifications() = runTest {
        val notifId = 0
        val notifType = "notifType"
        val notifTitle = "notifTitle"
        val notifBody = "notifBody"
        val notifDate = "notifDate"
        val notifTime = "notifTime"
        val notifImage = "notifImage"
        val isChecked = false
        dataSource.addNotifications(
            notifId,
            notifType,
            notifTitle,
            notifBody,
            notifDate,
            notifTime,
            notifImage,
            isChecked
        )
        dataSource.notifIsChecked(1, true)
        backgroundScope.launch {
            val data = dataSource.getUnReadNotifications(false).getOrAwaitValue()
            assertEquals(null, data)
        }
    }
}
