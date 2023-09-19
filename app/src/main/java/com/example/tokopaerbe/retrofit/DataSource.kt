package com.example.tokopaerbe.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.example.tokopaerbe.retrofit.response.DetailProductResponse
import com.example.tokopaerbe.retrofit.response.FulfillmentResponse
import com.example.tokopaerbe.retrofit.response.LoginResponse
import com.example.tokopaerbe.retrofit.response.PaymentResponse
import com.example.tokopaerbe.retrofit.response.ProfileResponse
import com.example.tokopaerbe.retrofit.response.RatingResponse
import com.example.tokopaerbe.retrofit.response.RegisterResponse
import com.example.tokopaerbe.retrofit.response.ReviewResponse
import com.example.tokopaerbe.retrofit.response.SearchResponse
import com.example.tokopaerbe.retrofit.response.TransactionResponse
import com.example.tokopaerbe.retrofit.user.UserLogin
import com.example.tokopaerbe.retrofit.user.UserProfile
import com.example.tokopaerbe.retrofit.user.UserRegister
import com.example.tokopaerbe.retrofit.user.ValueBottomSheet
import com.example.tokopaerbe.room.CartDao
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.room.NotificationDao
import com.example.tokopaerbe.room.NotificationsEntity
import com.example.tokopaerbe.room.WishlistDao
import com.example.tokopaerbe.room.WishlistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DataSource @Inject constructor(
    private val pref: UserPreferences,
    private val cartDao: CartDao,
    private val wishDao: WishlistDao,
    private val notifDao: NotificationDao
) {

    companion object {
        @Volatile
        private var instance: DataSource? = null
        fun getInstance(preferences: UserPreferences, cartDao: CartDao, wishDao: WishlistDao, notifDao: NotificationDao): DataSource =
            instance ?: synchronized(this) {
                instance ?: DataSource(preferences, cartDao, wishDao, notifDao)
            }.also { instance = it }
    }

    private var _signUp = MutableLiveData<RegisterResponse>()
    var signUp: Flow<RegisterResponse> = _signUp.asFlow()

    private var _signIn = MutableLiveData<LoginResponse>()
    var signIn: Flow<LoginResponse> = _signIn.asFlow()

    private var _profile = MutableLiveData<ProfileResponse>()
    var profile: Flow<ProfileResponse> = _profile.asFlow()

    private val _search = MutableLiveData<SearchResponse>()
    val search: LiveData<SearchResponse> = _search

    private val _detail = MutableLiveData<DetailProductResponse>()
    val detail: LiveData<DetailProductResponse> = _detail

    private val _review = MutableLiveData<ReviewResponse>()
    val review: LiveData<ReviewResponse> = _review

    private val _payment = MutableLiveData<PaymentResponse>()
    val payment: LiveData<PaymentResponse> = _payment

    private val _fulfillment = MutableLiveData<FulfillmentResponse>()
    val fulfillment: LiveData<FulfillmentResponse> = _fulfillment

    private val _rating = MutableLiveData<RatingResponse>()
    val rating: LiveData<RatingResponse> = _rating

    private val _transaction = MutableLiveData<TransactionResponse>()
    val transaction: LiveData<TransactionResponse> = _transaction

    fun uploadRegisterData(API_KEY: String, email:String, password:String, firebaseToken: String) {
        val requestBody = RegisterRequestBody(email, password, firebaseToken)
        val client = ApiConfig.getApiService().uploadDataRegister(API_KEY, requestBody)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("signUpResponse", "onResponse: ${response.message()}")
                    _signUp.value = response.body()
                } else {
                    Log.e("signUp", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("signUpFailure", "onFailure: ${t.message}")
            }
        })
    }


    fun uploadLoginData(API_KEY: String, email:String, password:String, firebaseToken: String) {
        val requestBody = LoginRequestBody(email, password, firebaseToken)
        val client = ApiConfig.getApiService().uploadDataLogin(API_KEY, requestBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("signInResponse", "onResponse: ${response.message()}")
                    _signIn.value = response.body()
                } else {
                    Log.e("signIn", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("signInFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadProfileData(auth: String, userName:MultipartBody.Part, userImage: MultipartBody.Part) {
        val client = ApiConfig.getApiService().uploadDataProfile(auth, userName, userImage)
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("profileResponse", "onResponse: ${response.message()}")
                    _profile.value = response.body()
                } else {
                    Log.e("profile", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("profileFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadSearchData(auth: String, query: String) {
        val client = ApiConfig.getApiService().uploadDataSearch(auth, query)
        client.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("searchResponse", "onResponse: ${response.message()}")
                    _search.value = response.body()
                } else {
                    Log.e("search", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("searchFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun getReviewData(auth: String, id: String) {
        val client = ApiConfig.getApiService().getReviewData(auth, id)
        client.enqueue(object : Callback<ReviewResponse> {
            override fun onResponse(
                call: Call<ReviewResponse>,
                response: Response<ReviewResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("reviewResponse", "onResponse: ${response.message()}")
                    _review.value = response.body()
                } else {
                    Log.e("review", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ReviewResponse>, t: Throwable) {
                Log.e("reviewFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun getPaymentData(auth: String) {
        val client = ApiConfig.getApiService().getPaymentData(auth)
        client.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(
                call: Call<PaymentResponse>,
                response: Response<PaymentResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("paymentResponse", "onResponse: ${response.message()}")
                    _payment.value = response.body()
                } else {
                    Log.e("payment", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                Log.e("paymentFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun getDetailProductData(auth: String, id: String) {
        val client = ApiConfig.getApiService().getDetailProductData(auth, id)
        client.enqueue(object : Callback<DetailProductResponse> {
            override fun onResponse(
                call: Call<DetailProductResponse>,
                response: Response<DetailProductResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("detailResponse", "onResponse: ${response.message()}")
                    _detail.value = response.body()
                } else {
                    Log.e("detail", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailProductResponse>, t: Throwable) {
                Log.e("detailFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadFulfillmentData(auth: String, payment: String, items:List<Item>) {
        val requestBody = FulfillmentRequestBody(payment, items)
        val client = ApiConfig.getApiService().uploadDataFulfillment(auth, requestBody)
        client.enqueue(object : Callback<FulfillmentResponse> {
            override fun onResponse(
                call: Call<FulfillmentResponse>,
                response: Response<FulfillmentResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("fulfillmentResponse", "onResponse: ${response.message()}")
                    _fulfillment.value = response.body()
                } else {
                    Log.e("fulfillment", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<FulfillmentResponse>, t: Throwable) {
                Log.e("fulfillmentFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun uploadRatingData(auth: String, invoiceId: String, rating:Int, review: String) {
        val requestBody = RatingRequestBody(invoiceId, rating, review)
        val client = ApiConfig.getApiService().uploadDataRating(auth, requestBody)
        client.enqueue(object : Callback<RatingResponse> {
            override fun onResponse(
                call: Call<RatingResponse>,
                response: Response<RatingResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("ratingResponse", "onResponse: ${response.message()}")
                    _rating.value = response.body()
                } else {
                    Log.e("rating", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RatingResponse>, t: Throwable) {
                Log.e("ratingFailure", "onFailure: ${t.message}")
            }
        })
    }

    fun getTransactionData(auth: String) {
        val client = ApiConfig.getApiService().getTransactionData(auth)
        client.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>
            ) {
                if (response.isSuccessful) {
                    Log.e("transactionResponse", "onResponse: ${response.message()}")
                    _transaction.value = response.body()
                } else {
                    Log.e("transaction", "onResponse: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e("transactionFailure", "onFailure: ${t.message}")
            }
        })
    }

    suspend fun saveSessionProfile(sessionProfile: UserProfile) {
        pref.saveUserProfile(sessionProfile)
    }

    suspend fun saveSessionRegister(sessionRegister: UserRegister) {
        pref.saveUserRegister(sessionRegister)
    }


    suspend fun saveSessionLogin(sessionLogin: UserLogin) {
        pref.saveUserLogin(sessionLogin)
    }

    suspend fun saveValueBottomSheet(valueBottomSheet: ValueBottomSheet) {
        pref.saveValueBottomSheet(valueBottomSheet)
    }

    fun getValueBottomSheet(): Flow<String> {
        return pref.getValueBottomSheet()
    }

    fun userToken(): Flow<String> {
       return pref.getAccessToken()
    }

    fun getCode(): LiveData<Int> {
        return pref.getCode().asLiveData()
    }

    fun userName(): Flow<String> {
        return pref.getUserName()
    }

    fun getUserFirstInstallState(): Flow<Boolean> {
        return pref.getUserFirstInstallState()
    }

    fun getFavoriteState(): LiveData<Boolean> {
        return pref.getFavoriteState().asLiveData()
    }

    fun getUserLoginState(): Flow<Boolean> {
        return pref.getUserLoginState()
    }

    fun getIsDarkState(): Flow<Boolean> {
        return pref.getIsDarkState()
    }

    suspend fun userLogin() {
        pref.login()
    }

    suspend fun userInstall() {
        pref.install()
    }

    suspend fun favoriteKey() {
        pref.favoriteKey()
    }

    suspend fun darkTheme(value: Boolean) {
        pref.darkTheme(value)
    }

    suspend fun userLogout() {
        pref.logout()
    }

    fun getProductCart() : LiveData<List<CartEntity>?> {
        return cartDao.getProduct()
    }

    suspend fun getCartForDetail(id: String): CartEntity? {
        return cartDao.getCartForDetail(id)
    }

    suspend fun getWishlistForDetail(id: String): WishlistEntity? {
        return wishDao.getWishlistForDetail(id)
    }

    suspend fun getCartForWishlist(id: String): CartEntity? {
        return cartDao.getCartForWishlist(id)
    }

    fun deleteProductCart(id: String) {
        return cartDao.deleteProduct(id)
    }

    suspend fun deleteAllCheckedProduct(cartEntity: List<CartEntity>) {
        return cartDao.deleteAllCheckedProduct(cartEntity)
    }

    fun addProductCart(id: String,
                       productName: String,
                       variantName: String,
                       stock: Int,
                       productPrice: Int,
                       quantity: Int,
                       image: String,
                       isChecked: Boolean) {
        return cartDao.addProduct(id, productName, variantName, stock, productPrice, quantity, image, isChecked)
    }

    fun isChecked(id: String, isChecked: Boolean) {
        return cartDao.isChecked(id, isChecked)
    }

    fun notifIsChecked(id: Int, isChecked: Boolean) {
        return notifDao.notifIsChecked(id, isChecked)
    }

    fun quantity(id: String, quantity: Int) {
        return cartDao.quantity(id, quantity)
    }

    fun checkAll(isChecked: Boolean) {
        return cartDao.checkAll(isChecked)
    }


    fun addWishList(id: String,
                    productName: String,
                    productPrice: Int,
                    image: String,
                    store: String,
                    productRating: Float,
                    sale: Int,
                    stock: Int,
                    variantName: String,
                    quantity: Int) {
        return wishDao.addWishList(id, productName, productPrice, image, store, productRating, sale, stock, variantName, quantity)
    }

    fun getWishList() : LiveData<List<WishlistEntity>?> {
        return wishDao.getWishList()
    }

    fun deleteWishList(id: String) {
        return wishDao.deleteWishList(id)
    }

    fun getIsFavorite(id: String): LiveData<List<WishlistEntity>?> {
        return wishDao.getIsFavorite(id)
    }

    suspend fun addNotifications(notifType: String,
                         notifTitle: String,
                         notifBody: String,
                         notifDate: String,
                         notifTime: String,
                         notifImage: String,
                         isChecked: Boolean) {
        return notifDao.addNotifications(notifType, notifTitle, notifBody, notifDate, notifTime, notifImage, isChecked)
    }

    fun getNotification() : LiveData<List<NotificationsEntity>?> {
        return notifDao.getNotifications()
    }

}