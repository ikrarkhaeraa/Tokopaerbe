//package com.example.tokopaerbe.core.pagging
//
//import android.content.Context
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.example.tokopaerbe.core.retrofit.response.Product
//import dagger.hilt.android.lifecycle.HiltViewModel
//
//class PaggingModel(private val productRepository: PaggingRepository) : ViewModel() {
//
//    fun sendFilter(
//        search: String?,
//        sort: String?,
//        brand: String?,
//        lowest: Int?,
//        highest: Int?
//    ): LiveData<PagingData<Product>> =
//        productRepository.getProductPaging(search, sort, brand, lowest, highest)
//            .cachedIn(viewModelScope)
//}
//
//class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PaggingModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return PaggingModel(Injection.providePaging(context)) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
