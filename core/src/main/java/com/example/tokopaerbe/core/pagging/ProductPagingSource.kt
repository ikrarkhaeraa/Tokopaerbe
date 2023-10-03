package com.example.tokopaerbe.core.pagging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tokopaerbe.core.retrofit.ApiService
import com.example.tokopaerbe.core.retrofit.UserPreferences
import com.example.tokopaerbe.core.retrofit.response.Product
import com.example.tokopaerbe.core.retrofit.user.ErrorState
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class ProductPagingSource(
    private val search: String? = null,
    private val sort: String? = null,
    private val brand: String? = null,
    private val lowest: Int? = null,
    private val highest: Int? = null,
    private val apiService: ApiService,
    private val preferences: UserPreferences
) : PagingSource<Int, Product>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val tokenUser = preferences.getAccessToken().first()
            Log.d("tokenForPaging", tokenUser)
            val responseData = apiService.uploadDataProductsPagging(
                "Bearer $tokenUser",
                search,
                brand,
                lowest,
                highest,
                sort,
                params.loadSize,
                page
            )
            Log.d("dataPaging", "${responseData.data.items}")
            preferences.saveCode(ErrorState(responseData.code))
            Log.d("cekSaveCode", responseData.code.toString())

            LoadResult.Page(
                data = responseData.data.items,
                prevKey = null,
                nextKey = if (page == responseData.data.totalPages) null else page.plus(1)
            )
        } catch (exception: Exception) {
            Log.d("pagingError", exception.toString())
            when (exception) {
                is HttpException ->
                    if (exception.code() == 200) {
                        preferences.saveCode(ErrorState(exception.code()))
                    } else if (exception.code() == 404) {
                        preferences.saveCode(ErrorState(exception.code()))
                    } else if (exception.code() == 500) {
                        preferences.saveCode(ErrorState(exception.code()))
                    }
            }
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
