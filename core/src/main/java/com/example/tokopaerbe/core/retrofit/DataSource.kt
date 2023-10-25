package com.example.tokopaerbe.core.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.tokopaerbe.core.pagging.ProductPagingSource
import com.example.tokopaerbe.core.retrofit.response.DetailProductResponse
import com.example.tokopaerbe.core.retrofit.response.FulfillmentResponse
import com.example.tokopaerbe.core.retrofit.response.LoginResponse
import com.example.tokopaerbe.core.retrofit.response.PaymentResponse
import com.example.tokopaerbe.core.retrofit.response.Product
import com.example.tokopaerbe.core.retrofit.response.ProductsResponse
import com.example.tokopaerbe.core.retrofit.response.ProfileResponse
import com.example.tokopaerbe.core.retrofit.response.RatingResponse
import com.example.tokopaerbe.core.retrofit.response.RegisterResponse
import com.example.tokopaerbe.core.retrofit.response.ReviewResponse
import com.example.tokopaerbe.core.retrofit.response.SearchResponse
import com.example.tokopaerbe.core.retrofit.response.TransactionResponse
import com.example.tokopaerbe.core.retrofit.user.UserLogin
import com.example.tokopaerbe.core.retrofit.user.UserProfile
import com.example.tokopaerbe.core.retrofit.user.UserRegister
import com.example.tokopaerbe.core.retrofit.user.ValueBottomSheet
import com.example.tokopaerbe.core.room.CartDao
import com.example.tokopaerbe.core.room.CartEntity
import com.example.tokopaerbe.core.room.NotificationDao
import com.example.tokopaerbe.core.room.NotificationsEntity
import com.example.tokopaerbe.core.room.WishlistDao
import com.example.tokopaerbe.core.room.WishlistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DataSource @Inject constructor(
    private val pref: UserPreferences,
    private val cartDao: CartDao,
    private val wishDao: WishlistDao,
    private val notifDao: NotificationDao,
    private val apiService: ApiService
) {

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

    fun uploadRegisterData(API_KEY: String, requestBody: RegisterRequestBody) = flow {
        emit(apiService.uploadDataRegister(API_KEY, requestBody))
    }

    fun uploadLoginData(API_KEY: String, requestBody: LoginRequestBody) = flow {
        emit(apiService.uploadDataLogin(API_KEY, requestBody))
    }

    fun uploadProfileData(
        auth: String,
        userName: MultipartBody.Part,
        userImage: MultipartBody.Part?
    ) = flow {
        emit(apiService.uploadDataProfile(auth, userName, userImage))
    }

    fun uploadSearchData(auth: String, query: String) = flow {
        emit(apiService.uploadDataSearch(auth, query))
    }

    fun getReviewData(auth: String, id: String) = flow {
        emit(apiService.getReviewData(auth, id))
    }


    fun getDetailProductData(auth: String, id: String) = flow {
        emit(apiService.getDetailProductData(auth, id))
    }

    fun uploadFulfillmentData(auth: String, requestBody: FulfillmentRequestBody) = flow {
        emit(apiService.uploadDataFulfillment(auth, requestBody))
    }

    fun uploadRatingData(auth: String, requestBody: RatingRequestBody) = flow {
        emit(apiService.uploadDataRating(auth, requestBody))
    }

    fun uploadTransactionData(auth: String) = flow {
        emit(apiService.getTransactionData(auth))
    }

    fun getProductPaging(
        search: String?,
        sort: String?,
        brand: String?,
        lowest: Int?,
        highest: Int?
    ): Flow<PagingData<Product>> {
        Log.d("cekRepository", "keHitRepositorynya")
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 1
            ),
            pagingSourceFactory = {
                ProductPagingSource(search, sort, brand, lowest, highest, apiService, pref)
            }
        ).flow
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

    fun getCode(): Flow<Int> {
        return pref.getCode()
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

    fun getRefreshResponseCode(): LiveData<Int> {
        return pref.getRefreshResponseCode().asLiveData()
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

    fun getProductCart(): LiveData<List<CartEntity>?> {
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

    fun deleteAllCart() {
        return cartDao.deleteAllCart()
    }

    fun deleteAllNotif() {
        return notifDao.deleteAllNotif()
    }

    fun deleteAllWishlist() {
        return wishDao.deleteAllWishlist()
    }

    suspend fun deleteAllCheckedProduct(cartEntity: List<CartEntity>) {
        return cartDao.deleteAllCheckedProduct(cartEntity)
    }

    fun addProductCart(
        id: String,
        productName: String,
        variantName: String,
        stock: Int,
        productPrice: Int,
        quantity: Int,
        image: String,
        isChecked: Boolean
    ) {
        return cartDao.addProduct(
            id,
            productName,
            variantName,
            stock,
            productPrice,
            quantity,
            image,
            isChecked
        )
    }

    fun isChecked(id: String, isChecked: Boolean) {
        return cartDao.isChecked(id, isChecked)
    }

    fun notifIsChecked(id: Int, isChecked: Boolean) {
        return notifDao.notifIsChecked(id, isChecked)
    }

    fun getUnReadNotifications(isChecked: Boolean): LiveData<List<NotificationsEntity>?> {
        return notifDao.getUnreadNotifications(isChecked)
    }

    fun quantity(id: String, quantity: Int) {
        return cartDao.quantity(id, quantity)
    }

    fun checkAll(isChecked: Boolean) {
        return cartDao.checkAll(isChecked)
    }

    fun addWishList(
        id: String,
        productName: String,
        productPrice: Int,
        image: String,
        store: String,
        productRating: Float,
        sale: Int,
        stock: Int,
        variantName: String,
        quantity: Int
    ) {
        return wishDao.addWishList(
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
    }

    fun getWishList(): LiveData<List<WishlistEntity>?> {
        return wishDao.getWishList()
    }

    fun deleteWishList(id: String) {
        return wishDao.deleteWishList(id)
    }

    fun getIsFavorite(id: String): LiveData<List<WishlistEntity>?> {
        return wishDao.getIsFavorite(id)
    }

    suspend fun addNotifications(
        notifId: Int,
        notifType: String,
        notifTitle: String,
        notifBody: String,
        notifDate: String,
        notifTime: String,
        notifImage: String,
        isChecked: Boolean
    ) {
        return notifDao.addNotifications(
            notifId,
            notifType,
            notifTitle,
            notifBody,
            notifDate,
            notifTime,
            notifImage,
            isChecked
        )
    }

    fun getNotification(): LiveData<List<NotificationsEntity>?> {
        return notifDao.getNotifications()
    }
}
