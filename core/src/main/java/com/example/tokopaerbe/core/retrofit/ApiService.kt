package com.example.tokopaerbe.core.retrofit

import com.example.tokopaerbe.core.retrofit.response.DetailProductResponse
import com.example.tokopaerbe.core.retrofit.response.FulfillmentResponse
import com.example.tokopaerbe.core.retrofit.response.LoginResponse
import com.example.tokopaerbe.core.retrofit.response.PaymentResponse
import com.example.tokopaerbe.core.retrofit.response.ProductsResponse
import com.example.tokopaerbe.core.retrofit.response.ProfileResponse
import com.example.tokopaerbe.core.retrofit.response.RatingResponse
import com.example.tokopaerbe.core.retrofit.response.RefreshResponse
import com.example.tokopaerbe.core.retrofit.response.RegisterResponse
import com.example.tokopaerbe.core.retrofit.response.ReviewResponse
import com.example.tokopaerbe.core.retrofit.response.SearchResponse
import com.example.tokopaerbe.core.retrofit.response.TransactionResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun uploadDataRegister(
        @Header("API_KEY") apiKey: String,
        @Body requestBody: RegisterRequestBody
    ): RegisterResponse

    @POST("login")
    suspend fun uploadDataLogin(
        @Header("API_KEY") apiKey: String,
        @Body requestBody: LoginRequestBody
    ): LoginResponse

    @Multipart
    @POST("profile")
    suspend fun uploadDataProfile(
        @Header("Authorization") auth: String,
        @Part text: MultipartBody.Part,
        @Part image: MultipartBody.Part?
    ): ProfileResponse

    @POST("refresh")
    fun uploadDataRefresh(
        @Header("API_KEY") apiKey: String,
        @Body requestBody: RefreshRequestBody
    ): Call<RefreshResponse>

    @POST("products")
    suspend fun uploadDataProductsPagging(
        @Header("Authorization") auth: String,
        @Query("search") search: String?,
        @Query("brand") brand: String?,
        @Query("lowest") lowest: Int?,
        @Query("highest") highest: Int?,
        @Query("sort") sort: String?,
        @Query("limit") limit: Int?,
        @Query("page") page: Int?,
    ): ProductsResponse


    @POST("search")
    suspend fun uploadDataSearch(
        @Header("Authorization") auth: String,
        @Query("query") search: String
    ): SearchResponse

    @GET("products/{id}")
    suspend fun getDetailProductData(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): DetailProductResponse

    @GET("review/{id}")
    suspend fun getReviewData(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): ReviewResponse

    @POST("fulfillment")
    suspend fun uploadDataFulfillment(
        @Header("Authorization") auth: String,
        @Body requestBody: FulfillmentRequestBody
    ): FulfillmentResponse

    @POST("rating")
    suspend fun uploadDataRating(
        @Header("Authorization") auth: String,
        @Body requestBody: RatingRequestBody
    ): RatingResponse

    @GET("transaction")
    suspend fun getTransactionData(
        @Header("Authorization") auth: String,
    ): TransactionResponse
}
