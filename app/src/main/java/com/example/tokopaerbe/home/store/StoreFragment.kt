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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.pagging.PaggingModel
import com.example.tokopaerbe.core.retrofit.response.Product
import com.example.tokopaerbe.core.retrofit.user.UserFilter
import com.example.tokopaerbe.databinding.FragmentStoreBinding
import com.example.tokopaerbe.pagging.LoadingStateAdapter
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by activityViewModels()

    private val paggingModel: PaggingModel by viewModels {
        com.example.tokopaerbe.core.pagging.ViewModelFactory(requireContext())
    }

    private val gridProductAdapter = GridProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

    private val linearProductAdapter = ProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

//    private var searchText: String? = null
//    private var selectedText1: String? = null
//    private var selectedText2: String? = null
//    private var textTerendah: String? = null
//    private var textTertinggi: String? = null

    private val delayMillis = 1000L
    private val filterParams = MutableStateFlow(UserFilter(null, null, null, null, null))
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var listProduct: PagingData<Product>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        binding.filterChip.visibility = GONE
        binding.changeRV.visibility = GONE
        binding.divider.visibility = GONE
        GlobalScope.launch(Dispatchers.Main) {
            delay(1200)
            binding.shimmerFilter.visibility = GONE
            binding.shimmerChangerv.visibility = GONE
            binding.divider.visibility = VISIBLE
            binding.filterChip.visibility = VISIBLE
            binding.changeRV.visibility = VISIBLE
        }

        if (!model.rvStateStore) {
            binding.shimmer.visibility = GONE
        } else {
            binding.shimmerGrid.visibility = GONE
        }

        binding.gambarerror.visibility = GONE
        binding.errorTitle.visibility = GONE
        binding.errorDesc.visibility = GONE
        binding.resetButton.visibility = GONE

        // Set up the refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.recyclerView.visibility = GONE
            binding.chipgroup.visibility = GONE
            if (!model.rvStateStore) {
                Log.d("cekPullRefresh", "grid")
                binding.shimmerGrid.visibility = VISIBLE
                binding.filterChip.visibility = GONE
                binding.changeRV.visibility = GONE
                binding.divider.visibility = GONE
                binding.shimmerFilter.visibility = VISIBLE
                binding.shimmerChangerv.visibility = VISIBLE

                paggingModel.sendFilter(
                    model.storeSearchText,
                    model.storeSelectedText1,
                    model.storeSelectedText2,
                    model.storeTextTerendah?.toInt(),
                    model.storeTextTertinggi?.toInt()
                ).observe(viewLifecycleOwner) { result ->
                    model.getCode().observe(viewLifecycleOwner) { code ->
                        if (code == 200) {
                            Log.d("cekCode", code.toString())
                            gridProductAdapter.submitData(lifecycle, result)
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(1000)
                                binding.chipgroup.visibility = VISIBLE
                                binding.shimmerFilter.visibility = GONE
                                binding.shimmerChangerv.visibility = GONE
                                binding.divider.visibility = VISIBLE
                                binding.filterChip.visibility = VISIBLE
                                binding.changeRV.visibility = VISIBLE
                                binding.shimmerGrid.visibility = GONE
                                binding.recyclerView.visibility = VISIBLE
                            }
                        } else {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.setOnClickListener {
                                resetOrRefresh()
                            }
                        }
                    }
                }
            } else {
                Log.d("cekPullRefresh", "linear")
                binding.shimmer.visibility = VISIBLE
                binding.filterChip.visibility = GONE
                binding.changeRV.visibility = GONE
                binding.divider.visibility = GONE
                binding.shimmerFilter.visibility = VISIBLE
                binding.shimmerChangerv.visibility = VISIBLE

                paggingModel.sendFilter(
                    model.storeSearchText,
                    model.storeSelectedText1,
                    model.storeSelectedText2,
                    model.storeTextTerendah?.toInt(),
                    model.storeTextTertinggi?.toInt()
                ).observe(viewLifecycleOwner) { result ->
                    model.getCode().observe(viewLifecycleOwner) { code ->
                        if (code == 200) {
                            Log.d("cekCode", code.toString())
                            linearProductAdapter.submitData(lifecycle, result)
                            GlobalScope.launch(Dispatchers.Main) {
                                delay(1000)
                                binding.chipgroup.visibility = VISIBLE
                                binding.shimmerFilter.visibility = GONE
                                binding.shimmerChangerv.visibility = GONE
                                binding.divider.visibility = VISIBLE
                                binding.filterChip.visibility = VISIBLE
                                binding.changeRV.visibility = VISIBLE
                                binding.shimmer.visibility = GONE
                                binding.recyclerView.visibility = VISIBLE
                            }
                        } else {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.setOnClickListener {
                                resetOrRefresh()
                            }
                        }
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(delayMillis)
            if (isVisible) {
                settingFilter()
                hitApi()
            }
        }

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

        binding.changeRV.setOnClickListener {
            model.rvStateStore = !model.rvStateStore
            settingAdapter()
        }
    }

    private fun hitApi(){
        val filterLiveData: LiveData<UserFilter> = filterParams.asLiveData()
        filterLiveData.observe(viewLifecycleOwner) {
            paggingModel.sendFilter(
                model.storeSearchText,
                model.storeSelectedText1,
                model.storeSelectedText2,
                model.storeTextTerendah?.toInt(),
                model.storeTextTertinggi?.toInt()
            ).observe(viewLifecycleOwner) { result ->

                binding.chipgroup.removeAllViews()
                val listFilter = listOf(
                    model.storeSelectedText1,
                    model.storeSelectedText2,
                    model.storeTextTerendah,
                    model.storeTextTertinggi
                )
                for (i in listFilter.indices) {
                    val chip = Chip(requireActivity())
                    chip.text = listFilter[i]
//                            chip.isClickable = true
                    if (chip.text.isNotEmpty()) {
                        binding.chipgroup.addView(chip)
                        Log.d("cekAddChip", "add")
                    }
                }

                model.getCode().observe(viewLifecycleOwner) {
                    Log.d("cekCode", it.toString())
                    Log.d("cekFilterData", model.storeSelectedText1.toString())
                    Log.d("cekFilterData", model.storeSelectedText2.toString())
                    when (it) {
                        200, 0 -> {
                            listProduct = result
                            settingAdapter()
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
                                param(FirebaseAnalytics.Param.ITEMS, result.toString())
                            }
                        }

                        404 -> {
                            emptyData()
                        }

                        500 -> {
                            errorState()
                        }

                        else -> {
                            errorState()
                        }
                    }
                }
            }
        }
    }

    private fun settingAdapter() {
        if (!model.rvStateStore) {
            binding.shimmerGrid.visibility = GONE
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
            gridProductAdapter.submitData(lifecycle, listProduct)
            binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)
        } else {
            binding.shimmer.visibility = GONE
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = linearProductAdapter
            binding.recyclerView.adapter = linearProductAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { linearProductAdapter.retry() }
            )
            linearProductAdapter.submitData(lifecycle, listProduct)
            binding.changeRV.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun settingFilter() {
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
        hitApi()
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
            resetOrRefresh()
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
        binding.resetButton.setOnClickListener { view ->
            resetOrRefresh()
        }
    }

    private fun resetOrRefresh() {
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
        val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
        resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
            paggingModel.sendFilter(
                model.storeSearchText,
                model.storeSelectedText1,
                model.storeSelectedText2,
                model.storeTextTerendah?.toInt(),
                model.storeTextTertinggi?.toInt()
            ).observe(viewLifecycleOwner) { reset ->
                Log.d("cekResetFilter", resetFilter.toString())
                linearProductAdapter.submitData(lifecycle, reset)
                binding.recyclerView.visibility = VISIBLE
                binding.gambarerror.visibility = GONE
                binding.errorTitle.visibility = GONE
                binding.errorTitle.text = getString(R.string.errorTitle)
                binding.errorDesc.visibility = GONE
                binding.errorDesc.text = getString(R.string.errorDesc)
                binding.resetButton.visibility = GONE
            }
        }
    }
}
