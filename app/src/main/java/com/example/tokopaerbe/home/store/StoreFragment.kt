package com.example.tokopaerbe.home.store

import DialogSearchFragment
import android.annotation.SuppressLint
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentModalBottomSheetBinding
import com.example.tokopaerbe.pagging.LoadingStateAdapter
import com.example.tokopaerbe.pagging.PaggingModel
import com.example.tokopaerbe.databinding.FragmentStoreBinding
import com.example.tokopaerbe.pagging.ProductPagingSource
import com.example.tokopaerbe.retrofit.user.UserFilter
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private val paggingModel: PaggingModel by viewModels {
        com.example.tokopaerbe.pagging.ViewModelFactory(requireContext())
    }

    private val gridProductAdapter = GridProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

    private val linearProductAdapter = ProductAdapter { product ->
        (requireActivity() as MainActivity).goToProduct(product.productId)
    }

    private var searchText: String? = null
    private var selectedText1: String? = null
    private var selectedText2: String? = null
    private var textTerendah: String? = null
    private var textTertinggi: String? = null

    private val delayMillis = 1000L
    private val filterParams = MutableStateFlow(UserFilter(null, null, null, null, null))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!model.rvStateStore) {
            binding.shimmer.visibility = GONE
        } else {
            binding.shimmerGrid.visibility = GONE
        }

        binding.searchTextField.setText("")

        binding.gambarerror.visibility = GONE
        binding.errorTitle.visibility = GONE
        binding.errorDesc.visibility = GONE
        binding.resetButton.visibility = GONE

        // Set up the refresh listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.chipgroup.removeAllViews()
            searchText = null
            selectedText1 = null
            selectedText2 = null
            textTerendah = null
            textTertinggi = null
            updateFilterAndRequestData()
            val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
            resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                paggingModel.sendFilter(
                    filterResetEmpty.search,
                    filterResetEmpty.sort,
                    filterResetEmpty.brand,
                    filterResetEmpty.lowest,
                    filterResetEmpty.highest
                ).observe(viewLifecycleOwner) { reset ->
                    Log.d("cekResetFilter", resetFilter.toString())
                    linearProductAdapter.submitData(lifecycle, reset)
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(delayMillis)
            settingAdapter()
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
            binding.searchTextField.setText("")
            searchText = bundle.getString("bundleKey").toString()
            Log.d("searchText", searchText!!)
            binding.searchTextField.setText(searchText)
            updateFilterAndRequestData()
        }

        binding.changeRV.setOnClickListener {
            model.rvStateStore = !model.rvStateStore
            toggleLayoutManager()
        }

    }

    private fun toggleLayoutManager() {
        if (!model.rvStateStore) {
            setGridLayoutManager()
            binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)

        } else {
            setLinearLayoutManager()
            binding.changeRV.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        }
    }

    private fun setGridLayoutManager() {
        binding.shimmerGrid.visibility = GONE
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = gridProductAdapter

        settingFilter()

        binding.recyclerView.adapter = gridProductAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { gridProductAdapter.retry() }
        )

        val filterLiveData: LiveData<UserFilter> = filterParams.asLiveData()

        filterLiveData.observe(viewLifecycleOwner) { filter ->
            paggingModel.sendFilter(
                filter.search,
                filter.sort,
                filter.brand,
                filter.lowest,
                filter.highest
            ).observe(viewLifecycleOwner) { result ->

                model.getCode().observe(viewLifecycleOwner) {
                    Log.d("cekCode", it.toString())
                    when (it) {

                        200 -> {
                            gridProductAdapter.submitData(lifecycle, result)
                        }

                        404 -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
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

                        500 -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle500)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc500)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.text = getString(R.string.refreshButtonError)
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    ).observe(viewLifecycleOwner) { reset ->
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

                        else -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitleConnection)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDescConnection)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.text = getString(R.string.refreshButtonError)
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
//                                        filterResetEmpty.search,
//                                        filterResetEmpty.sort,
//                                        filterResetEmpty.brand,
//                                        filterResetEmpty.lowest,
//                                        filterResetEmpty.highest
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    ).observe(viewLifecycleOwner) { reset ->
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
                    }

                }
            }

        }


    }

    private fun setLinearLayoutManager() {
        binding.shimmer.visibility = GONE
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = linearProductAdapter


        settingFilter()

        binding.recyclerView.adapter = linearProductAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { linearProductAdapter.retry() }
        )

        val filterLiveData: LiveData<UserFilter> = filterParams.asLiveData()

        filterLiveData.observe(viewLifecycleOwner) { filter ->
            paggingModel.sendFilter(
                filter.search,
                filter.sort,
                filter.brand,
                filter.lowest,
                filter.highest
            ).observe(viewLifecycleOwner) { result ->

                model.getCode().observe(viewLifecycleOwner) {
                    Log.d("cekCode", it.toString())

                    when (it) {
                        200, 0 -> {
                            linearProductAdapter.submitData(lifecycle, result)
                        }

                        404 -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
//                                        filterResetEmpty.search,
//                                        filterResetEmpty.sort,
//                                        filterResetEmpty.brand,
//                                        filterResetEmpty.lowest,
//                                        filterResetEmpty.highest
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    ).observe(viewLifecycleOwner) { reset ->
                                        binding.chipgroup.removeAllViews()
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

                        500 -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitle500)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDesc500)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.text = getString(R.string.refreshButtonError)
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    ).observe(viewLifecycleOwner) { reset ->
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

                        else -> {
                            binding.recyclerView.visibility = GONE
                            binding.gambarerror.visibility = VISIBLE
                            binding.errorTitle.visibility = VISIBLE
                            binding.errorTitle.text = getString(R.string.errorTitleConnection)
                            binding.errorDesc.visibility = VISIBLE
                            binding.errorDesc.text = getString(R.string.errorDescConnection)
                            binding.resetButton.visibility = VISIBLE
                            binding.resetButton.text = getString(R.string.refreshButtonError)
                            binding.resetButton.setOnClickListener { view ->
                                binding.chipgroup.removeAllViews()
//                                searchText = null
//                                selectedText1 = null
//                                selectedText2 = null
//                                textTerendah = null
//                                textTertinggi = null
                                updateFilterAndRequestData()
                                val resetFilter: LiveData<UserFilter> = filterParams.asLiveData()
                                resetFilter.observe(viewLifecycleOwner) { filterResetEmpty ->
                                    paggingModel.sendFilter(
//                                        filterResetEmpty.search,
//                                        filterResetEmpty.sort,
//                                        filterResetEmpty.brand,
//                                        filterResetEmpty.lowest,
//                                        filterResetEmpty.highest
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                    ).observe(viewLifecycleOwner) { reset ->
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
                    }
                }

            }

        }
    }

    private fun settingAdapter() {
        if (!model.rvStateStore) {
            setGridLayoutManager()
            binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)
        } else {
            setLinearLayoutManager()
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

            selectedText1 = sort
            selectedText2 = brand
            textTerendah = terendah
            textTertinggi = tertinggi

            Log.d("cekFragmentResult", selectedText1.toString())
            Log.d("cekFragmentResult", selectedText2.toString())
            Log.d("cekFragmentResult", textTerendah.toString())
            Log.d("cekFragmentResult", textTertinggi.toString())

            val listFilter = listOf(selectedText1, selectedText2, textTerendah, textTertinggi)

            for (i in listFilter.indices) {
                val chip = Chip(requireActivity())
                chip.text = listFilter[i]
                chip.isClickable = true
                if (chip.text.isNotEmpty()) {
                    binding.chipgroup.addView(chip)
                }
            }
            updateFilterAndRequestData()
        }

    }

    private fun updateFilterAndRequestData() {
        if (selectedText1?.isEmpty() == true) {
            selectedText1 = null
        }

        if (selectedText2?.isEmpty() == true) {
            selectedText2 = null
        }

        if (textTerendah?.isEmpty() == true) {
            textTerendah = null
        }

        if (textTertinggi?.isEmpty() == true) {
            textTertinggi = null
        }

        val newFilter = UserFilter(
            searchText,
            selectedText1,
            selectedText2,
            textTerendah?.toInt(),
            textTertinggi?.toInt()
        )
        Log.d("cekUpdateFilter", searchText.toString())
        Log.d("cekUpdateFilter", selectedText1.toString())
        Log.d("cekUpdateFilter", selectedText2.toString())
        Log.d("cekUpdateFilter", textTerendah.toString())
        Log.d("cekUpdateFilter", textTertinggi.toString())
        filterParams.value = newFilter
    }


}