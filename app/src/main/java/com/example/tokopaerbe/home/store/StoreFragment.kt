package com.example.tokopaerbe.home.store

import DialogSearchFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.retrofit.response.Product
import com.example.tokopaerbe.core.retrofit.user.UserFilter
import com.example.tokopaerbe.databinding.FragmentStoreBinding
import com.example.tokopaerbe.pagging.LoadingStateAdapter
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.IOException

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private val model: ViewModel by activityViewModels()

    private val gridProductAdapter = GridProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

    private val linearProductAdapter = ProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

    private val filterParams = MutableStateFlow(UserFilter(null, null, null, null, null))
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var listProduct: PagingData<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        settingAdapter()

        binding.filterChip.setOnClickListener {
            val modalBottomSheet = ModalBottomSheetFragment()
            modalBottomSheet.show(parentFragmentManager, ModalBottomSheetFragment.TAG)
        }

        binding.searchTextField.setOnClickListener {
            val dialogFragment = DialogSearchFragment()
            dialogFragment.show(parentFragmentManager, DialogSearchFragment.TAG)
        }

        setFragmentResultListener("searchText") { _, bundle ->
            model.storeSearchText = bundle.getString("bundleKey").toString()
            Log.d("searchText", model.storeSearchText.toString())
            binding.searchTextField.setText(model.storeSearchText)
            updateFilter()

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS) {
                param(FirebaseAnalytics.Param.SEARCH_TERM, model.searchFilter)
            }
        }

        setFragmentResultListener("filter") { _, bundle ->
            binding.chipgroup.removeAllViews()

            val sort = bundle.getString("selectedText1")
            val brand = bundle.getString("selectedText2")
            val terendah = bundle.getString("textTerendah")
            val tertinggi = bundle.getString("textTertinggi")

            model.storeSelectedText1 = sort
            model.storeSelectedText2 = brand
            model.storeTextTerendah = terendah
            model.storeTextTertinggi = tertinggi

            updateFilter()
        }

        binding.changeRV.setOnClickListener {
            model.rvStateStore = !model.rvStateStore
            settingAdapter()
            if (!model.rvStateStore) {
                gridProductAdapter.submitData(viewLifecycleOwner.lifecycle, listProduct)
            } else {
                linearProductAdapter.submitData(viewLifecycleOwner.lifecycle, listProduct)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewLifecycleOwner.lifecycleScope.launch {
                sendAndGetRequestData()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val filterData: Flow<UserFilter> = filterParams
            filterData.collectLatest {
                Log.d("cekHit", "kehit")
                sendAndGetRequestData()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            linearProductAdapter.loadStateFlow.collectLatest { pagingState ->
                val isLoading = pagingState.refresh is LoadState.Loading
                val isError = pagingState.refresh is LoadState.Error
                val isSuccess = pagingState.refresh is LoadState.NotLoading

                hideError()

                if (isLoading) {
                    hideUi()
                    showShimmer()
                } else if (isSuccess) {
                    hideShimmer()
                    showUi()
                } else if (isError) {
                    val error = (pagingState.refresh as LoadState.Error).error
                    Log.d("cekError", error.message.toString())
                    when (error) {
                        is retrofit2.HttpException -> {
                            Log.d("cekFlowError", "http")
                            Log.d("cekCode", error.code().toString())
                            if (error.code() == 404) {
                                hideShimmer()
                                emptyData()
                            } else if (error.code() == 500) {
                                hideShimmer()
                                errorState()
                            }
                        }

                        is IOException -> {
                            Log.d("cekFlowError", "masukio")
                            hideShimmer()
                            noConnection()
                        }
                    }

                } else {
                    hideShimmer()
                    errorState()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            gridProductAdapter.loadStateFlow.collectLatest { pagingState ->
                val isLoading = pagingState.refresh is LoadState.Loading
                val isError = pagingState.refresh is LoadState.Error
                val isSuccess = pagingState.refresh is LoadState.NotLoading

                hideError()

                if (isLoading) {
                    hideUi()
                    showShimmer()
                } else if (isSuccess) {
                    hideShimmer()
                    showUi()
                } else if (isError) {
                    val error = (pagingState.refresh as LoadState.Error).error
                    Log.d("cekError", error.message.toString())
                    when (error) {
                        is retrofit2.HttpException -> {
                            if (error.code() == 404) {
                                hideShimmer()
                                emptyData()
                            } else if (error.code() == 500) {
                                hideShimmer()
                                errorState()
                            }
                        }

                        is IOException -> {
                            hideShimmer()
                            noConnection()
                        }
                    }

                } else {
                    hideShimmer()
                    errorState()
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStoreBinding.inflate(inflater, container, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    private suspend fun sendAndGetRequestData() {
        model.sendFilter(
            model.storeSearchText,
            model.storeSelectedText1,
            model.storeSelectedText2,
            model.storeTextTerendah?.toInt(),
            model.storeTextTertinggi?.toInt()
        ).collectLatest {
            Log.d("cekGetPaging", "get")
            listProduct = it
            settingChipGroup()
            if (!model.rvStateStore) {
                gridProductAdapter.submitData(lifecycle, listProduct)
            } else {
                linearProductAdapter.submitData(lifecycle, listProduct)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun settingChipGroup() {
        binding.chipgroup.removeAllViews()
        val listFilter = listOf(
            model.storeSelectedText1,
            model.storeSelectedText2,
            model.storeTextTerendah,
            model.storeTextTertinggi
        )
        Log.d("cekListIndices", listFilter.indices.toString())
        for (i in listFilter.indices) {
            val chip = Chip(requireActivity())
            chip.text = listFilter[i]
            if (i == 2 && model.storeTextTerendah != null) {
                chip.text = "> ${listFilter[2]}"
            }
            if (i == 3 && model.storeTextTertinggi != null) {
                chip.text = "< ${listFilter[3]}"
            }
            if (chip.text.isNotEmpty()) {
                binding.chipgroup.addView(chip)
                Log.d("cekAddChip", "add")
            }
        }
    }

    private fun updateFilter() {
        if (model.storeSearchText?.isEmpty() == true) {
            model.storeSearchText = null
        }
        if (model.storeSelectedText1?.isEmpty() == true) {
            model.storeSelectedText1 = null
        }
        if (model.storeSelectedText2?.isEmpty() == true) {
            model.storeSelectedText2 = null
        }
        if (model.storeTextTerendah?.isEmpty() == true) {
            model.storeTextTerendah = null
        }
        if (model.storeTextTertinggi?.isEmpty() == true) {
            model.storeTextTertinggi = null
        }

        val newFilter = UserFilter(
            model.storeSearchText,
            model.storeSelectedText1,
            model.storeSelectedText2,
            model.storeTextTerendah?.toInt(),
            model.storeTextTertinggi?.toInt()
        )
        filterParams.value = newFilter
    }

    private fun settingAdapter() {
        if (!model.rvStateStore) {
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recyclerView.adapter = gridProductAdapter

            binding.recyclerView.adapter = gridProductAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { gridProductAdapter.retry() }
            )
            (binding.recyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == gridProductAdapter.itemCount) {
                            2
                        } else {
                            1
                        }
                    }
                }
            binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)

        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = linearProductAdapter

            binding.recyclerView.adapter = linearProductAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { linearProductAdapter.retry() }
            )
            binding.changeRV.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        }
    }

    private fun showShimmer() {
        binding.shimmerFilter.visibility = VISIBLE
        binding.shimmerChangerv.visibility = VISIBLE
        if (!model.rvStateStore) {
            Log.d("cekRvState", "false")
            binding.shimmerGrid.visibility = VISIBLE
            binding.shimmer.visibility = GONE
        } else {
            Log.d("cekRvState", "true")
            binding.shimmer.visibility = VISIBLE
            binding.shimmerGrid.visibility = GONE
        }
    }

    private fun hideShimmer() {
        binding.shimmer.visibility = GONE
        binding.shimmerGrid.visibility = GONE
        binding.shimmerChangerv.visibility = GONE
        binding.shimmerFilter.visibility = GONE
    }

    private fun showUi() {
        binding.divider.visibility = VISIBLE
        binding.changeRV.visibility = VISIBLE
        binding.filterChip.visibility = VISIBLE
        binding.chipgroup.visibility = VISIBLE
        binding.recyclerView.visibility = VISIBLE
    }

    private fun hideUi() {
        binding.divider.visibility = GONE
        binding.changeRV.visibility = GONE
        binding.filterChip.visibility = GONE
        binding.chipgroup.visibility = GONE
        binding.recyclerView.visibility = GONE
    }

    private fun hideError() {
        binding.gambarerror.visibility = GONE
        binding.errorTitle.visibility = GONE
        binding.errorDesc.visibility = GONE
        binding.resetButton.visibility = GONE
    }

    private fun emptyData() {
        binding.shimmerGrid.visibility = GONE
        binding.shimmer.visibility = GONE
        binding.recyclerView.visibility = GONE
        binding.gambarerror.visibility = VISIBLE
        binding.errorTitle.visibility = VISIBLE
        binding.errorTitle.text = getString(R.string.errorTitle)
        binding.errorDesc.visibility = VISIBLE
        binding.errorDesc.text = getString(R.string.errorDesc)
        binding.resetButton.visibility = VISIBLE
        binding.resetButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                resetOrRefresh()
            }
        }
    }

    private fun noConnection() {
        binding.shimmerGrid.visibility = GONE
        binding.shimmer.visibility = GONE
        binding.recyclerView.visibility = GONE
        binding.gambarerror.visibility = VISIBLE
        binding.errorTitle.visibility = VISIBLE
        binding.errorTitle.text = getString(R.string.errorTitleConnection)
        binding.errorDesc.visibility = VISIBLE
        binding.errorDesc.text = getString(R.string.errorDescConnection)
        binding.resetButton.visibility = VISIBLE
        binding.resetButton.text = getString(R.string.refreshButtonError)
        binding.resetButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                resetOrRefresh()
            }
        }
    }

    private fun errorState() {
        binding.shimmerGrid.visibility = GONE
        binding.shimmer.visibility = GONE
        binding.recyclerView.visibility = GONE
        binding.gambarerror.visibility = VISIBLE
        binding.errorTitle.visibility = VISIBLE
        binding.errorTitle.text = getString(R.string.errorTitle500)
        binding.errorDesc.visibility = VISIBLE
        binding.errorDesc.text = getString(R.string.errorDesc500)
        binding.resetButton.visibility = VISIBLE
        binding.resetButton.text = getString(R.string.refreshButtonError)
        binding.resetButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                resetOrRefresh()
            }
        }
    }

    private suspend fun resetOrRefresh() {
        binding.chipgroup.removeAllViews()
        binding.searchTextField.setText("")
        model.storeSearchText = null
        model.storeSelectedText1 = null
        model.storeSelectedText2 = null
        model.storeTextTerendah = null
        model.storeTextTertinggi = null
        model.searchFilter = ""
        model.sort = ""
        model.brand = ""
        model.textTerendah = ""
        model.textTertinggi = ""
        updateFilter()
        sendAndGetRequestData()
    }

}