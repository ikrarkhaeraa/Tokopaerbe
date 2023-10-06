// package com.example.tokopaerbe.retrofit
//
// import com.example.tokopaerbe.retrofit.response.DetailProductResponse
// import com.example.tokopaerbe.retrofit.response.FulfillmentResponse
// import com.example.tokopaerbe.retrofit.response.LoginResponse
// import com.example.tokopaerbe.retrofit.response.PaymentResponse
// import com.example.tokopaerbe.retrofit.response.ProductsResponse
// import com.example.tokopaerbe.retrofit.response.ProfileResponse
// import com.example.tokopaerbe.retrofit.response.RatingResponse
// import com.example.tokopaerbe.retrofit.response.RefreshResponse
// import com.example.tokopaerbe.retrofit.response.RegisterResponse
// import com.example.tokopaerbe.retrofit.response.ReviewResponse
// import com.example.tokopaerbe.retrofit.response.SearchResponse
// import com.example.tokopaerbe.retrofit.response.TransactionResponse
// import okhttp3.MultipartBody
// import retrofit2.Call
// import retrofit2.http.Body
// import retrofit2.http.GET
// import retrofit2.http.Header
// import retrofit2.http.Multipart
// import retrofit2.http.POST
// import retrofit2.http.Part
// import retrofit2.http.Path
// import retrofit2.http.Query
//
// interface ApiService {
//    @POST("register")
//    fun uploadDataRegister(
//        @Header("API_KEY") apiKey: String,
//        @Body requestBody: RegisterRequestBody
//    ): Call<RegisterResponse>
//
//    @POST("login")
//    fun uploadDataLogin(
//        @Header("API_KEY") apiKey: String,
//        @Body requestBody: LoginRequestBody
//    ): Call<LoginResponse>
//
//    @Multipart
//    @POST("profile")
//    fun uploadDataProfile(
//        @Header("Authorization") auth: String,
//        @Part text: MultipartBody.Part,
//        @Part image: MultipartBody.Part?
//    ): Call<ProfileResponse>
//
//    @POST("refresh")
//    fun uploadDataRefresh(
//        @Header("API_KEY") apiKey: String,
//        @Body requestBody: RefreshRequestBody
//    ): Call<RefreshResponse>
//
//    @POST("products")
//    suspend fun uploadDataProductsPagging(
//        @Header("Authorization") auth: String,
//        @Query("search") search: String?,
//        @Query("brand") brand: String?,
//        @Query("lowest") lowest: Int?,
//        @Query("highest") highest: Int?,
//        @Query("sort") sort: String?,
//        @Query("limit") limit: Int?,
//        @Query("page") page: Int?,
//    ): ProductsResponse
//
//    @POST("search")
//    fun uploadDataSearch(
//        @Header("Authorization") auth: String,
//        @Query("query") search: String
//    ): Call<SearchResponse>
//
//    @GET("products/{id}")
//    fun getDetailProductData(
//        @Header("Authorization") auth: String,
//        @Path("id") id: String
//    ): Call<DetailProductResponse>
//
//    @GET("review/{id}")
//    fun getReviewData(
//        @Header("Authorization") auth: String,
//        @Path("id") id: String
//    ): Call<ReviewResponse>
//
//    @GET("payment")
//    fun getPaymentData(
//        @Header("Authorization") auth: String,
//    ): Call<PaymentResponse>
//
//    @POST("fulfillment")
//    fun uploadDataFulfillment(
//        @Header("Authorization") auth: String,
//        @Body requestBody: FulfillmentRequestBody
//    ): Call<FulfillmentResponse>
//
//    @POST("rating")
//    fun uploadDataRating(
//        @Header("Authorization") auth: String,
//        @Body requestBody: RatingRequestBody
//    ): Call<RatingResponse>
//
//    @GET("transaction")
//    fun getTransactionData(
//        @Header("Authorization") auth: String,
//    ): Call<TransactionResponse>
// }
