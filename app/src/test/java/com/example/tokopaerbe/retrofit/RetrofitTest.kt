package com.example.tokopaerbe.retrofit

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bumptech.glide.load.engine.Resource
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.tokopaerbe.retrofit.response.DetailProductResponse
import com.example.tokopaerbe.retrofit.response.ReviewResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@RunWith(AndroidJUnit4::class)
class RetrofitTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    private lateinit var apiService: ApiService


    @Mock
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)


        val client = OkHttpClient.Builder().build()
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `read sample success json file`() {
        val reader = MockResponseFileReader("RegisterResponse.json")
        assertNotNull(reader.content)
    }

    @Test
    fun testRegisterResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("RegisterResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val apiKey = "api_key"
        val email = "email"
        val password = "password"
        val firebaseToken = "firebaseToken"
        val requestBody = RegisterRequestBody(email, password, firebaseToken)
        val actualResponse = apiService.uploadDataRegister(apiKey, requestBody).execute()
        // Assert
        val accessToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoiYWNjZXNzVG9rZW4iLCJleHAiOjE2ODUzNDE1MjB9.ldL_6Qoo-MfMmwHrhxXUv670Uz6j0CCF9t9I8uOmW_LuAUTzCWhjMcQelP8MjfnVDqKSZj2LaqHv3TY08AB7TQ"
        val refreshToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoiYWNjZXNzVG9rZW4iLCJleHAiOjE2ODUzNDQ1MjB9.HeeNuQww-w2tb3pffNC43BCmMCcE3rOj-yL7-pTGOEcIcoFCv2n9IEWS0gqxNnDaNf3sXBm7JHCxFexB5FGRgQ"
        val expiresAt = 600L
        assertEquals(accessToken, actualResponse.body()?.data?.accessToken)
        assertEquals(refreshToken, actualResponse.body()?.data?.refreshToken)
        assertEquals(expiresAt, actualResponse.body()?.data?.expiresAt)
    }

    @Test
    fun testLoginResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("LoginResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val apiKey = "api_key"
        val email = "email"
        val password = "password"
        val firebaseToken = "firebaseToken"
        val requestBody = LoginRequestBody(email, password, firebaseToken)
        val actualResponse = apiService.uploadDataLogin(apiKey, requestBody).execute()
        // Assert
        val userName = ""
        val userImage = ""
        val accessToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoiYWNjZXNzVG9rZW4iLCJleHAiOjE2ODUzNDE4OTV9.AceVKZlMeFFvwNPAC5Opc6mSxhAXWz1CSf4E2FipZsJkPfaFt021Yi3TpG08ENUashUwJX-YLCuIolqnb7EulA"
        val refreshToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoicmVmcmVzaFRva2VuIiwiZXhwIjoxNjg1MzQ0ODk1fQ.tB4EeMvkfJAV_kSwakcEujsJEqNtlvKaBbz6ga58lMw6R1NKNOSi6iy3Qn-dFtHGMkzwqpokY3uOdQYcVtahCA"
        val expiresAt = 600L
        assertEquals(userName, actualResponse.body()?.data?.userName)
        assertEquals(userImage, actualResponse.body()?.data?.userImage)
        assertEquals(accessToken, actualResponse.body()?.data?.accessToken)
        assertEquals(refreshToken, actualResponse.body()?.data?.refreshToken)
        assertEquals(expiresAt, actualResponse.body()?.data?.expiresAt)
    }

    @Test
    fun testRefreshResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("RefreshResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val apiKey = "api_key"
        val token = "token"
        val requestBody = RefreshRequestBody(token)
        val actualResponse = apiService.uploadDataRefresh(apiKey, requestBody).execute()
        // Assert
        val accessToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoiYWNjZXNzVG9rZW4iLCJleHAiOjE2ODUzNDIwMjN9.g4y-WkXHsk6gTxb72-L2Kk2Wv7dZ438zWZIfJ1Z9bER2Ob3ULnuo2ExBzq5S5l6eJ85PUYOeuiCUCeBRZ94RQQ"
        val refreshToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJlY29tbWVyY2UtYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4yMzAuMTI5OjgwODAvIiwidXNlcklkIjoiMzczNTNkMzAtMWIzZC00ZGJlLThmODQtYWZjMjdjNGU5MWJhIiwidHlwZVRva2VuIjoiYWNjZXNzVG9rZW4iLCJleHAiOjE2ODUzNDUwMjN9.U3FQQCGsyBCWE5qUOkWjneI_igtUj9bDKvJI-25o-8a6NMekmvvdlzjJVvK2Yyed9IpAaGTMXNgeQsl9M04uDA"
        val expiresAt = 600L
        assertEquals(accessToken, actualResponse.body()?.data?.accessToken)
        assertEquals(refreshToken, actualResponse.body()?.data?.refreshToken)
        assertEquals(expiresAt, actualResponse.body()?.data?.expiresAt)
    }

    @Test
    fun testProfileResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("ProfileResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val text = MultipartBody.Part.createFormData("userName", "userName")
        val image = MultipartBody.Part.createFormData("userImage", "userImage")
        val actualResponse = apiService.uploadDataProfile(token, text, image).execute()
        // Assert
        val userName = "Test"
        val userImage = "1d32ba79-e879-4425-a011-2da4281f1c1b-test.png"
        assertEquals(userName, actualResponse.body()?.data?.userName)
        assertEquals(userImage, actualResponse.body()?.data?.userImage)
    }

    @Test
    fun testSearchResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("SearchResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val query = "query"
        val actualResponse = apiService.uploadDataSearch(token, query).execute()
        // Assert
        val data = listOf(
            "Lenovo Legion 3",
            "Lenovo Legion 5",
            "Lenovo Legion 7",
            "Lenovo Ideapad 3",
            "Lenovo Ideapad 5",
            "Lenovo Ideapad 7"
        )
        assertEquals(data, actualResponse.body()?.data)
    }

    @Test
    fun testProductsDetailResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("ProductsDetailResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val id = "id"
        val actualResponse = apiService.getDetailProductData(token, id)
        actualResponse.enqueue(
            object : Callback<DetailProductResponse> {
                override fun onResponse(
                    call: Call<DetailProductResponse>,
                    response: Response<DetailProductResponse>
                ) {
                    if (response.isSuccessful) {
                        // Assert
                        val productId = "17b4714d-527a-4be2-84e2-e4c37c2b3292"
                        val productName = "ASUS ROG Strix G17 G713RM-R736H6G-O - Eclipse Gray"
                        val productPrice = 24499000
                        val image = listOf(
                            "https://images.tokopedia.net/img/cache/900/VqbcmM/2022/4/6/0a49c399-cf6b-47f5-91c9-8cbd0b86462d.jpg",
                            "https://images.tokopedia.net/img/cache/900/VqbcmM/2022/3/25/0cc3d06c-b09d-4294-8c3f-1c37e60631a6.jpg",
                            "https://images.tokopedia.net/img/cache/900/VqbcmM/2022/3/25/33a06657-9f88-4108-8676-7adafaa94921.jpg"
                        )
                        val brand = "Asus"
                        val description =
                            "ASUS ROG Strix G17 G713RM-R736H6G-O - Eclipse Gray [AMD Ryzen™ 7 6800H / NVIDIA® GeForce RTX™ 3060 / 8G*2 / 512GB / 17.3inch / WIN11 / OHS]\n\nCPU : AMD Ryzen™ 7 6800H Mobile Processor (8-core/16-thread, 20MB cache, up to 4.7 GHz max boost)\nGPU : NVIDIA® GeForce RTX™ 3060 Laptop GPU\nGraphics Memory : 6GB GDDR6\nDiscrete/Optimus : MUX Switch + Optimus\nTGP ROG Boost : 1752MHz* at 140W (1702MHz Boost Clock+50MHz OC, 115W+25W Dynamic Boost)\nPanel : 17.3-inch FHD (1920 x 1080) 16:9 360Hz IPS-level 300nits sRGB % 100.00%"
                        val store = "AsusStore"
                        val sale = 12
                        val stock = 2
                        val totalRating = 7
                        val totalReview = 5
                        val totalSatisfaction = 100
                        val productRating = 5.0
                        val variant1 = listOf("RAM 16GB", 0)
                        val variant2 = listOf("RAM 32GB", 1000000)
                        val productVariant = listOf(variant1, variant2)

                        assertEquals(productId, response.body()?.data?.productId)
                        assertEquals(productName, response.body()?.data?.productName)
                        assertEquals(productPrice, response.body()?.data?.productPrice)
                        assertEquals(image, response.body()?.data?.image)
                        assertEquals(brand, response.body()?.data?.brand)
                        assertEquals(description, response.body()?.data?.description)
                        assertEquals(store, response.body()?.data?.store)
                        assertEquals(sale, response.body()?.data?.sale)
                        assertEquals(stock, response.body()?.data?.stock)
                        assertEquals(totalRating, response.body()?.data?.totalRating)
                        assertEquals(totalReview, response.body()?.data?.totalReview)
                        assertEquals(totalSatisfaction, response.body()?.data?.totalSatisfaction)
                        assertEquals(productRating, response.body()?.data?.productRating)
                        assertEquals(productVariant, response.body()?.data?.productVariant)
                    } else {

                    }
                }

                override fun onFailure(call: Call<DetailProductResponse>, t: Throwable) {

                }
            }
        )
    }

    @Test
    fun testReviewResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("ReviewResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val id = "id"
        val actualResponse = apiService.getReviewData(token, id)
        actualResponse.enqueue(
            object : Callback<ReviewResponse> {
                override fun onResponse(
                    call: Call<ReviewResponse>,
                    response: Response<ReviewResponse>
                ) {
                    if (response.isSuccessful) {
                        // Assert
                        val review1 = listOf(
                            "John",
                            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQM4VpzpVw8mR2j9_gDajEthwY3KCOWJ1tOhcv47-H9o1a-s9GRPxdb_6G9YZdGfv0HIg&usqp=CAU",
                            4,
                            "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
                        )
                        val review2 = listOf(
                            "Doe",
                            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTR3Z6PN8QNVhH0e7rEINu_XJS0qHIFpDT3nwF5WSkcYmr3znhY7LOTkc8puJ68Bts-TMc&usqp=CAU",
                            5,
                            "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book."
                        )
                        val productReview = listOf(review1, review2)
                        assertEquals(productReview, response.body()?.data)

                    } else {

                    }
                }

                override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {

                }
            }
        )
    }

    @Test
    fun testFulfillmentResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("FulfillmentResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val payment = "payment"
        val actualResponse = apiService.uploadDataFulfillment(payment, mock()).execute()
        // Assert
        val invoiceId = "ba47402c-d263-49d3-a1f8-759ae59fa4a1"
        val status = true
        val date = "09 Jun 2023"
        val time = "08:53"
        val method = "Bank BCA"
        val total = 48998000
        assertEquals(invoiceId, actualResponse.body()?.data?.invoiceId)
        assertEquals(status, actualResponse.body()?.data?.status)
        assertEquals(date, actualResponse.body()?.data?.date)
        assertEquals(time, actualResponse.body()?.data?.time)
        assertEquals(method, actualResponse.body()?.data?.payment)
        assertEquals(total, actualResponse.body()?.data?.total)
    }

    @Test
    fun testRatingResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("RatingResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val invoiceId = "invoiceId"
        val rating = 4
        val review = "review"
        val requestBody = RatingRequestBody(invoiceId, rating, review)
        val actualResponse = apiService.uploadDataRating(token, requestBody).execute()
        // Assert
        val code = "200"
        val message = "Fulfillment rating and review success"
        assertEquals(code, actualResponse.body()?.code)
        assertEquals(message, actualResponse.body()?.message)
    }

    @Test
    fun testTransactionResponse() {
        // Assign
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader("TransactionResponse.json").content)
        mockWebServer.enqueue(response)
        // Act
        val token = "token"
        val actualResponse = apiService.getTransactionData(token).execute()
        // Assert
        val invoiceId = "8cad85b1-a28f-42d8-9479-72ce4b7f3c7d"
        val status = true
        val date = "09 Jun 2023"
        val time = "09:05"
        val payment = "Bank BCA"
        val total = 48998000
        val rating = 4
        val review = "LGTM"
        val image = "https://images.tokopedia.net/img/cache/900/VqbcmM/2022/4/6/0a49c399-cf6b-47f5-91c9-8cbd0b86462d.jpg"
        val name = "ASUS ROG Strix G17 G713RM-R736H6G-O - Eclipse Gray"
        val items = 1
        assertEquals(invoiceId, actualResponse.body()?.data?.get(0)?.invoiceId)
        assertEquals(status, actualResponse.body()?.data?.get(0)?.status)
        assertEquals(date, actualResponse.body()?.data?.get(0)?.date)
        assertEquals(time, actualResponse.body()?.data?.get(0)?.time)
        assertEquals(payment, actualResponse.body()?.data?.get(0)?.payment)
        assertEquals(total, actualResponse.body()?.data?.get(0)?.total)
        assertEquals(rating, actualResponse.body()?.data?.get(0)?.rating)
        assertEquals(review, actualResponse.body()?.data?.get(0)?.review)
        assertEquals(image, actualResponse.body()?.data?.get(0)?.image)
        assertEquals(name, actualResponse.body()?.data?.get(0)?.name)
        assertEquals(items, actualResponse.body()?.data?.get(0)?.items?.size)
    }

}