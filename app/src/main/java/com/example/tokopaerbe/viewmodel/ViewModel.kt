package com.example.tokopaerbe.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tokopaerbe.core.retrofit.DataSource
import com.example.tokopaerbe.core.retrofit.FulfillmentRequestBody
import com.example.tokopaerbe.core.retrofit.LoginRequestBody
import com.example.tokopaerbe.core.retrofit.RatingRequestBody
import com.example.tokopaerbe.core.retrofit.RegisterRequestBody
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
import com.example.tokopaerbe.core.room.CartEntity
import com.example.tokopaerbe.core.room.NotificationsEntity
import com.example.tokopaerbe.core.room.WishlistEntity
import com.example.tokopaerbe.core.utils.SealedClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val data: DataSource) : ViewModel() {

    var profile: Flow<ProfileResponse> = data.profile
    var signIn: Flow<LoginResponse> = data.signIn
    var signUp: Flow<RegisterResponse> = data.signUp
    val search: LiveData<SearchResponse> = data.search
    val detail: LiveData<DetailProductResponse> = data.detail
    val review: LiveData<ReviewResponse> = data.review
    val payment: LiveData<PaymentResponse> = data.payment
    val fulfillment: LiveData<FulfillmentResponse> = data.fulfillment
    val rating: LiveData<RatingResponse> = data.rating
    val transaction: LiveData<TransactionResponse> = data.transaction
    var priceDetail: String = ""
    var selectedChip: Int = 0
    var storeSearchText: String? = null
    var storeSelectedText1: String? = null
    var storeSelectedText2: String? = null
    var storeTextTerendah: String? = null
    var storeTextTertinggi: String? = null

    private var _search: String = ""
    var searchFilter: String = ""
    fun setSearchValue(searchText: String) {
        _search = searchText
    }

    private var _sort: String = ""
    var sort: String = ""
    fun clearSortValue() {
        sort = ""
    }

    private var _brand: String = ""
    var brand: String = ""
    fun clearBrandValue() {
        brand = ""
    }

    private var _textTerendah: String = ""
    var textTerendah: String = ""
    fun clearTerendahValue() {
        textTerendah = ""
    }

    private var _textTertinggi: String = ""
    var textTertinggi: String = ""
    fun clearTertinggiValue() {
        textTertinggi = ""
    }

    var rvStateStore: Boolean = true
        get() = field
        set(value) {
            field = value
        }

    var iconFavState: Boolean = true
        get() = field
        set(value) {
            field = value
        }

    var rvStateWishList: Boolean = true
        get() = field
        set(value) {
            field = value
        }

    fun quantity(id: String, quantity: Int) {
        return data.quantity(id, quantity)
    }

    fun checkAll(isChecked: Boolean) {
        return data.checkAll(isChecked)
    }

    fun isChecked(id: String, isChecked: Boolean) {
        return data.isChecked(id, isChecked)
    }

    fun notifIsChecked(id: Int, isChecked: Boolean) {
        return data.notifIsChecked(id, isChecked)
    }

    fun getUnreadNotificatios(isChecked: Boolean): LiveData<List<NotificationsEntity>?> {
        return data.getUnReadNotifications(isChecked)
    }

    fun addCartProduct(
        id: String,
        productName: String,
        variantName: String,
        stock: Int,
        productPrice: Int,
        quantity: Int,
        image: String,
        isChecked: Boolean
    ) {
        return data.addProductCart(
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
        return data.addWishList(
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

    fun deleteWishList(id: String) {
        return data.deleteWishList(id)
    }

    fun getIsFavorite(id: String): LiveData<List<WishlistEntity>?> {
        return data.getIsFavorite(id)
    }

    fun getWishList(): LiveData<List<WishlistEntity>?> {
        return data.getWishList()
    }

    fun addNotifications(
        notifId: Int,
        notifType: String,
        notifTitle: String,
        notifBody: String,
        notifDate: String,
        notifTime: String,
        notifImage: String,
        isChecked: Boolean
    ) {
        viewModelScope.launch {
            data.addNotifications(
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
    }

    fun getNotification(): LiveData<List<NotificationsEntity>?> {
        return data.getNotification()
    }

    fun deleteCartProduct(id: String) {
        return data.deleteProductCart(id)
    }

    fun deleteAllCart() {
        return data.deleteAllCart()
    }

    fun deleteAllNotif() {
        return data.deleteAllNotif()
    }

    fun deleteAllWishlist() {
        return data.deleteAllWishlist()
    }

    fun deleteAllCheckedProduct(cartEntity: List<CartEntity>) {
        viewModelScope.launch {
            data.deleteAllCheckedProduct(cartEntity)
        }
    }

    fun getCartProduct(): LiveData<List<CartEntity>?> {
        return data.getProductCart()
    }

    suspend fun getCartforDetail(id: String): CartEntity? {
        return data.getCartForDetail(id)
    }

    suspend fun getWishlistforDetail(id: String): WishlistEntity? {
        return data.getWishlistForDetail(id)
    }

    suspend fun getCartforWishlist(id: String): CartEntity? {
        return data.getCartForWishlist(id)
    }

    fun getUserLoginState(): Flow<Boolean> {
        return data.getUserLoginState()
    }

    fun getRefreshResponseCode(): LiveData<Int> {
        return data.getRefreshResponseCode()
    }

    fun getIsDarkState(): Flow<Boolean> {
        return data.getIsDarkState()
    }

    fun getUserFirstInstallState(): Flow<Boolean> {
        return data.getUserFirstInstallState()
    }

    fun getFavoriteState(): LiveData<Boolean> {
        return data.getFavoriteState()
    }

    fun getUserToken(): Flow<String> {
        return data.userToken()
    }

    fun getUserName(): Flow<String> {
        return data.userName()
    }

    fun saveSessionRegister(sessionRegister: UserRegister) {
        viewModelScope.launch {
            data.saveSessionRegister(sessionRegister)
        }
    }

    fun saveSessionLogin(sessionLogin: UserLogin) {
        viewModelScope.launch {
            data.saveSessionLogin(sessionLogin)
        }
    }

    fun saveSessionProfile(sessionProfile: UserProfile) {
        viewModelScope.launch {
            data.saveSessionProfile(sessionProfile)
        }
    }

    fun userInstall() {
        viewModelScope.launch {
            data.userInstall()
        }
    }

    fun getCode(): Flow<Int> {
        return data.getCode()
    }

    fun favoriteKey() {
        viewModelScope.launch {
            data.favoriteKey()
        }
    }

    fun darkTheme(value: Boolean) {
        viewModelScope.launch {
            data.darkTheme(value)
        }
    }

    fun userLogin() {
        viewModelScope.launch {
            data.userLogin()
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            data.userLogout()
        }
    }

    private val _profileData = MutableSharedFlow<SealedClass<ProfileResponse>>()
    val profileData = _profileData

    fun postDataProfile(
        auth: String,
        userName: MultipartBody.Part,
        userImage: MultipartBody.Part?
    ) = viewModelScope.launch {
        _profileData.emit(SealedClass.Init)
        _profileData.emit(SealedClass.Loading)

        data.uploadProfileData(auth, userName, userImage).catch {
            _profileData.emit(SealedClass.Error(it))
        }.collect {
            _profileData.emit(SealedClass.Success(it))
        }
    }


    private val _searchData = MutableStateFlow<SealedClass<SearchResponse>>(SealedClass.Init)
    val searchData = _searchData

    fun postDataSearch(auth: String, query: String) = viewModelScope.launch {
        _searchData.emit(SealedClass.Loading)

        data.uploadSearchData(auth, query).catch {
            _searchData.emit(SealedClass.Error(it))
        }.collect {
            _searchData.emit(SealedClass.Success(it))
        }
    }

    private val _productDetailData = MutableStateFlow<SealedClass<DetailProductResponse>>(SealedClass.Init)
    val productDetailData = _productDetailData
    fun getDetailProductData(id: String) = viewModelScope.launch {
        val token = getUserToken().first()
        val auth = "Bearer $token"
        Log.d("cekAuthDetail", auth)

        _productDetailData.emit(SealedClass.Loading)

        data.getDetailProductData(auth, id).catch {
            _productDetailData.emit(SealedClass.Error(it))
        }.collect {
            _productDetailData.emit(SealedClass.Success(it))
        }
    }

    private val _reviewData = MutableStateFlow<SealedClass<ReviewResponse>>(SealedClass.Init)
    val reviewData = _reviewData
    fun getReviewData(id: String) = viewModelScope.launch {
        val token = getUserToken().first()
        val auth = "Bearer $token"
        Log.d("cekAuthDetail", auth)

        _reviewData.emit(SealedClass.Loading)

        data.getReviewData(auth, id).catch {
            _reviewData.emit(SealedClass.Error(it))
        }.collect {
            _reviewData.emit(SealedClass.Success(it))
        }
    }


    private val _registerData = MutableSharedFlow<SealedClass<RegisterResponse>>()
    val registerData = _registerData

    fun postDataRegister(API_KEY: String, requestBody: RegisterRequestBody) =
        viewModelScope.launch {
            _registerData.emit(SealedClass.Loading)

            data.uploadRegisterData(API_KEY, requestBody).catch {
                _registerData.emit(SealedClass.Error(it))
            }.collect {
                _registerData.emit(SealedClass.Success(it))
            }
        }

    private val _loginData = MutableSharedFlow<SealedClass<LoginResponse>>()
    val loginData = _loginData

    fun postDataLogin(API_KEY: String, requestBody: LoginRequestBody) = viewModelScope.launch {
        _loginData.emit(SealedClass.Loading)

        data.uploadLoginData(API_KEY, requestBody).catch {
            _loginData.emit(SealedClass.Error(it))
        }.collect {
            _loginData.emit(SealedClass.Success(it))
        }
    }

    private val _fulfillmentData = MutableSharedFlow<SealedClass<FulfillmentResponse>>()
    val fulfillmentData = _fulfillmentData

    fun postDataFulfillment(auth: String, requestBody: FulfillmentRequestBody) =
        viewModelScope.launch {
            _fulfillmentData.emit(SealedClass.Loading)

            data.uploadFulfillmentData(auth, requestBody).catch {
                _fulfillmentData.emit(SealedClass.Error(it))
            }.collect {
                _fulfillmentData.emit(SealedClass.Success(it))
            }
        }

    private val _ratingData = MutableSharedFlow<SealedClass<RatingResponse>>()
    val ratingData = _ratingData

    fun postDataRating(auth: String, requestBody: RatingRequestBody) = viewModelScope.launch {
        _ratingData.emit(SealedClass.Loading)

        data.uploadRatingData(auth, requestBody).catch {
            _ratingData.emit(SealedClass.Error(it))
        }.collect {
            _ratingData.emit(SealedClass.Success(it))
        }
    }

    private val _transactionData =
        MutableStateFlow<SealedClass<TransactionResponse>>(SealedClass.Init)
    val transactionData = _transactionData

    fun postDataTransaction(auth: String) = viewModelScope.launch {
        _transactionData.emit(SealedClass.Loading)

        data.uploadTransactionData(auth).catch {
            _transactionData.emit(SealedClass.Error(it))
        }.collect {
            _transactionData.emit(SealedClass.Success(it))
        }
    }

    val sendFilter: (String?, String?, String?, Int?, Int?) -> Flow<PagingData<Product>> = { search, sort, brand, lowest, highest ->
        Log.d("cekViewModel", "keHitViewModelnya")
        data.getProductPaging(search, sort, brand, lowest, highest)
            .cachedIn(viewModelScope)
    }

}

