//package com.example.tokopaerbe.pagging
//
//import androidx.lifecycle.LiveData
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.liveData
//import com.example.tokopaerbe.retrofit.ApiService
//import com.example.tokopaerbe.retrofit.UserPreferences
//import com.example.tokopaerbe.retrofit.response.Product
//
//class PaggingRepository(
//    private val apiService: ApiService,
//    private val preferences: UserPreferences
//) {
//    fun getProductPaging(
//        search: String?,
//        sort: String?,
//        brand: String?,
//        lowest: Int?,
//        highest: Int?
//    ): LiveData<PagingData<Product>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                initialLoadSize = 10,
//                prefetchDistance = 1
//            ),
//            pagingSourceFactory = {
//                ProductPagingSource(search, sort, brand, lowest, highest, apiService, preferences)
//            }
//        ).liveData
//    }
//}
