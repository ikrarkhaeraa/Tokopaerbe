package com.example.tokopaerbe.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.tokopaerbe.core.retrofit.DataSource
import com.example.tokopaerbe.core.retrofit.FulfillmentRequestBody
import com.example.tokopaerbe.core.retrofit.Item
import com.example.tokopaerbe.core.retrofit.LoginRequestBody
import com.example.tokopaerbe.core.retrofit.RegisterRequestBody
import com.example.tokopaerbe.core.retrofit.response.DataLogin
import com.example.tokopaerbe.core.retrofit.response.DataProfile
import com.example.tokopaerbe.core.retrofit.response.DataRegister
import com.example.tokopaerbe.core.retrofit.response.Fulfillment
import com.example.tokopaerbe.core.retrofit.response.FulfillmentResponse
import com.example.tokopaerbe.core.retrofit.response.LoginResponse
import com.example.tokopaerbe.core.retrofit.response.ProfileResponse
import com.example.tokopaerbe.core.retrofit.response.RegisterResponse
import com.example.tokopaerbe.core.retrofit.response.SearchResponse
import com.example.tokopaerbe.core.retrofit.user.UserLogin
import com.example.tokopaerbe.core.retrofit.user.UserProfile
import com.example.tokopaerbe.core.retrofit.user.UserRegister
import com.example.tokopaerbe.core.utils.SealedClass
import com.example.tokopaerbe.viewmodel.ViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ViewModelTest {

    private lateinit var viewModel: ViewModel
    private lateinit var dataSource: DataSource

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
        dataSource = mock(DataSource::class.java)
        viewModel = ViewModel(dataSource)
    }

    @Test
    fun testProfile() = runTest {
        val auth = "auth"
        val userName = MultipartBody.Part.createFormData("userName", "userName")
        val userImage = MultipartBody.Part.createFormData("userImage", "userImage")
        val expectedResponse = ProfileResponse(
            DataProfile(
                "",""
            ),
            200,
            "OK"
        )

        `when`(dataSource.uploadProfileData(auth, userName, userImage)).thenReturn(flowOf(expectedResponse))

        viewModel.postDataProfile(auth, userName, userImage)

        val result = mutableListOf<SealedClass<ProfileResponse>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.profileData.toList(result)
        }

        advanceUntilIdle()
        assertEquals(result, listOf(SealedClass.Loading, SealedClass.Success(expectedResponse)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testRegister() = runTest {
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
        `when`(dataSource.uploadRegisterData(apiKey, requestBody)).thenReturn(flowOf(expectedResponse))

        viewModel.postDataRegister(apiKey, RegisterRequestBody(email, password, firebaseToken))

        val result = mutableListOf<SealedClass<RegisterResponse>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.registerData.toList(result)
        }

        advanceUntilIdle()
        assertEquals(result, listOf(SealedClass.Loading, SealedClass.Success(expectedResponse)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLogin() = runTest {
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
        `when`(dataSource.uploadLoginData(apiKey, requestBody)).thenReturn(flowOf(expectedResponse))

        viewModel.postDataLogin(apiKey, LoginRequestBody(email, password, firebaseToken))

        val result = mutableListOf<SealedClass<LoginResponse>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.loginData.toList(result)
        }

        advanceUntilIdle()
        assertEquals(result, listOf(SealedClass.Loading, SealedClass.Success(expectedResponse)))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testPostDataSearch() = runTest {
        // Prepare test data
        val auth = "auth_token"
        val query = "search_query"
        val expectedResponse = SearchResponse(
            listOf(""),
            200,
            "OK"
        )

        `when`(dataSource.uploadSearchData(auth, query)).thenReturn(flowOf(expectedResponse))

        viewModel.postDataSearch(auth, query)

        val result = mutableListOf<SealedClass<SearchResponse>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.searchData.toList(result)
        }

        advanceUntilIdle()
        assertEquals(result, listOf(SealedClass.Init, SealedClass.Loading, SealedClass.Success(expectedResponse)))
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testPostDataFulfillment() = runTest {
        // Prepare test data
        val auth = "auth_token"
        val payment = "payment"
        val expectedResponse = FulfillmentResponse(
            Fulfillment("",true,"","","",10000000),
            200,
            "OK"
        )

        val requestBody = FulfillmentRequestBody(payment, mock())
        `when`(dataSource.uploadFulfillmentData(auth, requestBody)).thenReturn(flowOf(expectedResponse))

        viewModel.postDataFulfillment(auth, requestBody)

        val result = mutableListOf<SealedClass<FulfillmentResponse>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.fulfillmentData.toList(result)
        }

        advanceUntilIdle()
        assertEquals(result, listOf(SealedClass.Init, SealedClass.Loading, SealedClass.Success(expectedResponse)))
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
