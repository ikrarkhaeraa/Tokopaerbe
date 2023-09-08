package com.example.tokopaerbe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tokopaerbe.home.store.Filter
import com.example.tokopaerbe.retrofit.DataSource
import com.example.tokopaerbe.retrofit.Item
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
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.room.WishlistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ViewModel(private val data: DataSource) : ViewModel() {

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


    private val _search = MutableLiveData<String?>()
    val searchFilter: LiveData<String?>
        get() = _search
    fun setSearchValue(selectedText: String) {
        _search.postValue(selectedText)
    }

    private val _sort = MutableLiveData<String?>()
    val sort: LiveData<String?>
        get() = _sort
    fun setSortValue(selectedText: String) {
        _sort.postValue(selectedText)
    }

    private val _brand = MutableLiveData<String?>()
    val brand: LiveData<String?>
        get() = _brand
    fun setBrandValue(selectedText: String) {
        _brand.postValue(selectedText)
    }

    private val _textTerendah = MutableLiveData<String?>()
    val textTerendah: LiveData<String?>
        get() = _textTerendah
    fun setTerendahValue(selectedText: String) {
        _textTerendah.postValue(selectedText)
    }

    private val _textTertinggi = MutableLiveData<String?>()
    val textTertinggi: LiveData<String?>
        get() = _textTertinggi
    fun setTertinggiValue(selectedText: String) {
        _textTertinggi.postValue(selectedText)
    }

    fun setSelectedFilter(filter: Filter) {
        _search.value = filter.search
        _sort.value = filter.sort
        _brand.value = filter.brand
        _textTerendah.value = filter.textTerendah
        _textTertinggi.value = filter.textTertinggi
    }


    var rvStateStore: Boolean = true
        get() = field
        set(value)  {
            field = value
        }

    var rvStateWishList: Boolean = true
        get() = field
        set(value)  {
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
    fun addCartProduct(id: String,
                       productName: String,
                       variantName: String,
                       stock: Int,
                       productPrice: Int,
                       quantity: Int,
                       image: String,
                       isChecked: Boolean,
                       cartPrice: Int) {
        return data.addProductCart(id, productName, variantName, stock, productPrice, quantity, image, isChecked, cartPrice)
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
        return data.addWishList(id, productName, productPrice, image, store, productRating, sale, stock, variantName, quantity)
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

    fun deleteCartProduct(id: String) {
        return data.deleteProductCart(id)
    }

    fun deleteAllCheckedProduct(cartEntity: List<CartEntity>) {
        viewModelScope.launch {
           data.deleteAllCheckedProduct(cartEntity)
        }
    }
    fun getCartProduct(): LiveData<List<CartEntity>?> {
        return data.getProductCart()
    }
    fun getUserLoginState(): Flow<Boolean> {
        return data.getUserLoginState()
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

    fun getCode(): LiveData<Int>  {
           return data.getCode()
    }

    fun favoriteKey() {
        viewModelScope.launch {
            data.favoriteKey()
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

    fun postDataProfile(auth: String, userName: MultipartBody.Part, userImage: MultipartBody.Part) {
        viewModelScope.launch {
            data.uploadProfileData(auth, userName, userImage)
        }
    }

    fun postDataSearch(auth: String, query: String) {
        viewModelScope.launch {
            data.uploadSearchData(auth, query)
        }
    }

    fun getDetailProductData(auth: String, id: String) {
        viewModelScope.launch {
            data.getDetailProductData(auth, id)
        }
    }

    fun getReviewData(auth: String, id: String) {
        viewModelScope.launch {
            data.getReviewData(auth, id)
        }
    }

    fun getPaymentData(auth: String) {
        viewModelScope.launch {
            data.getPaymentData(auth)
        }
    }

    fun postDataRegister(API_KEY:String, email:String, password:String, firebaseToken:String) {
        viewModelScope.launch {
            data.uploadRegisterData(API_KEY, email, password, firebaseToken)
        }
    }

    fun postDataLogin(API_KEY:String, email:String, password:String, firebaseToken:String) {
        viewModelScope.launch {
            data.uploadLoginData(API_KEY, email, password, firebaseToken)
        }
    }

    fun postDataFulfillment(auth: String, payment: String, items:List<Item>) {
        viewModelScope.launch {
            data.uploadFulfillmentData(auth, payment, items)
        }
    }

    fun postDataRating(auth: String, invoiceId: String, rating:Int, review: String) {
        viewModelScope.launch {
            data.uploadRatingData(auth, invoiceId, rating, review)
        }
    }

    fun getTransactionData(auth: String) {
        viewModelScope.launch {
            data.getTransactionData(auth)
        }
    }

}