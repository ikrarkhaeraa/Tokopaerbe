package com.example.tokopaerbe.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.tokopaerbe.core.retrofit.user.UserLogin
import com.example.tokopaerbe.core.retrofit.user.UserProfile
import com.example.tokopaerbe.core.retrofit.user.UserRegister
import com.example.tokopaerbe.viewmodel.ViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ViewModelTest {

    private lateinit var viewModel: ViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
    fun setup() {
        viewModel = ViewModel(mock())
    }

    @Test
    fun testProfile() = runTest {
        val auth = "auth"
        val text = MultipartBody.Part.createFormData("userName", "userName")
        val image = MultipartBody.Part.createFormData("userImage", "userImage")
        val userName = "userName"
        val userImage = "userImage"
        viewModel.postDataProfile(auth, text, image)
        viewModel.saveSessionProfile(UserProfile(userName, userImage))
        backgroundScope.launch {
            val data = viewModel.profile.first()
            val savedUsername = viewModel.getUserName().first()
            assertEquals(200, data.code)
            assertEquals(userName, savedUsername)
        }
    }

    @Test
    fun testLogin() = runTest {
        val auth = "auth"
        val email = "email"
        val password = "password"
        val firebaseToken = "firebaseToken"
        val userName = "userName"
        val userImage = "userImage"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiresAt = 0L
        viewModel.postDataLogin(auth, email, password, firebaseToken)
        viewModel.userLogin()
        viewModel.saveSessionLogin(
            UserLogin(
                userName,
                userImage,
                accessToken,
                refreshToken,
                expiresAt
            )
        )
        backgroundScope.launch {
            val data = viewModel.signIn.first()
            val state = viewModel.getUserLoginState().first()
            val savedUsername = viewModel.getUserName().first()
            val savedToken = viewModel.getUserToken().first()
            assertEquals(200, data.code)
            assertEquals(true, state)
            assertEquals("userName", savedUsername)
            assertEquals("refreshToken", savedToken)
        }
    }

    @Test
    fun testRegister() = runTest {
        val auth = "auth"
        val email = "email"
        val password = "password"
        val firebaseToken = "firebaseToken"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val expiresAt = 0L
        viewModel.postDataRegister(auth, email, password, firebaseToken)
        viewModel.userLogin()
        viewModel.saveSessionRegister(UserRegister(accessToken, refreshToken, expiresAt))
        backgroundScope.launch {
            val data = viewModel.signIn.first()
            val state = viewModel.getUserLoginState().first()
            val savedToken = viewModel.getUserToken().first()
            assertEquals(200, data.code)
            assertEquals(true, state)
            assertEquals("refreshToken", savedToken)
        }
    }

    @Test
    fun testPostDataSearch() = runTest {
        // Prepare test data
        val auth = "auth_token"
        val query = "search_query"

        // Call the function
        viewModel.postDataSearch(auth, query)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val searchResponse = viewModel.search.getOrAwaitValue()
            assertEquals(200, searchResponse)
        }
    }

    @Test
    fun testGetDetailProductData() = runTest {
        // Prepare test data
        val productId = "example_product_id"
        val authToken = "example_auth_token"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.getDetailProductData(authToken, productId)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val detailResponse = viewModel.detail.getOrAwaitValue()
            assertEquals(200, detailResponse.code)
        }
    }

    @Test
    fun testGetReviewData() = runTest {
        // Prepare test data
        val productId = "example_product_id"
        val authToken = "example_auth_token"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.getReviewData(authToken, productId)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val reviewResponse = viewModel.review.getOrAwaitValue()
            assertEquals(200, reviewResponse.code)
        }
    }

    @Test
    fun testGetPaymentData() = runTest {
        // Prepare test data
        val authToken = "example_auth_token"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.getPaymentData(authToken)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val paymentResponse = viewModel.payment.getOrAwaitValue()
            assertEquals(200, paymentResponse.code)
        }
    }

    @Test
    fun testPostDataFulfillment() = runTest {
        // Prepare test data
        val authToken = "example_auth_token"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.postDataFulfillment(authToken, "payment_method", mock())

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val fulfillmentResponse = viewModel.fulfillment.getOrAwaitValue()
            assertEquals(200, fulfillmentResponse.code)
        }
    }

    @Test
    fun testPostDataRating() = runTest {
        // Prepare test data
        val authToken = "example_auth_token"
        val invoiceId = "example_invoice_id"
        val rating = 5
        val review = "Great product!"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.postDataRating(authToken, invoiceId, rating, review)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val ratingResponse = viewModel.rating.getOrAwaitValue()
            assertEquals(200, ratingResponse.code)
        }
    }

    @Test
    fun testGetTransactionData() = runTest {
        // Prepare test data
        val authToken = "example_auth_token"

        // Mock getUserToken function
        whenever(viewModel.getUserToken()).thenReturn(flowOf(authToken))

        // Call the function
        viewModel.getTransactionData(authToken)

        // Observe the LiveData and verify the result
        backgroundScope.launch {
            val transactionResponse = viewModel.transaction.getOrAwaitValue()
            assertEquals(200, transactionResponse.code)
        }
    }
}
