package com.example.tokopaerbe.home.store

import DialogSearchFragment
import android.annotation.SuppressLint
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
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.pagging.LoadingStateAdapter
import com.example.tokopaerbe.pagging.PaggingModel
import com.example.tokopaerbe.databinding.FragmentStoreBinding
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
    private var isGridLayoutManager = false

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

        binding.gambarerror.visibility = GONE
        binding.errorTitle.visibility = GONE
        binding.errorDesc.visibility = GONE
        binding.resetButton.visibility = GONE

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
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = gridProductAdapter


        settingFilter()

        setFragmentResultListener("searchText") { _, bundle ->
            searchText = bundle.getString("bundleKey").toString()
            Log.d("searchText", searchText!!)
            if (searchText!!.isNotEmpty()) {
                updateFilterAndRequestData()
//                binding.searchTextField.setText(searchText)
            }
        }

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

//                model.getCode().observe(viewLifecycleOwner) {
//                    Log.d("cekCode", it.toString())
//                    if (it == 200) {
//                        gridProductAdapter.submitData(lifecycle, result)
//                    }  else if (it == 404) {
//                    binding.recyclerView.visibility = GONE
//                    binding.gambarerror.visibility = VISIBLE
//                    binding.errorTitle.visibility = VISIBLE
//                    binding.errorTitle.text = getString(R.string.errorTitle)
//                    binding.errorDesc.visibility = VISIBLE
//                    binding.errorDesc.text = getString(R.string.errorDesc)
//                    binding.resetButton.visibility = VISIBLE
//                } else if (it == 500) {
//                    binding.recyclerView.visibility = GONE
//                    binding.gambarerror.visibility = VISIBLE
//                    binding.errorTitle.visibility = VISIBLE
//                    binding.errorTitle.text = getString(R.string.errorTitle500)
//                    binding.errorDesc.visibility = VISIBLE
//                    binding.errorDesc.text = getString(R.string.errorDesc500)
//                    binding.resetButton.visibility = VISIBLE
//                    binding.resetButton.text = getString(R.string.refreshButtonError)
//                } else {
//                    binding.recyclerView.visibility = GONE
//                    binding.gambarerror.visibility = VISIBLE
//                    binding.errorTitle.visibility = VISIBLE
//                    binding.errorTitle.text = getString(R.string.errorTitleConnection)
//                    binding.errorDesc.visibility = VISIBLE
//                    binding.errorDesc.text = getString(R.string.errorDescConnection)
//                    binding.resetButton.visibility = VISIBLE
//                    binding.resetButton.text = getString(R.string.refreshButtonError)
//                }
//
//                }

                gridProductAdapter.submitData(lifecycle, result)
                Log.d("cekDataPaging", result.toString())
            }

        }


    }

    private fun setLinearLayoutManager() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = linearProductAdapter


        settingFilter()

        setFragmentResultListener("searchText") { _, bundle ->
            searchText = bundle.getString("bundleKey").toString()
            Log.d("searchText", searchText!!)
            if (searchText!!.isNotEmpty()) {
                updateFilterAndRequestData()
//                binding.searchTextField.setText(searchText)
            }
        }

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

//                model.getCode().observe(viewLifecycleOwner) {
//                    Log.d("cekCode", it.toString())
//                    if (it == 200) {
//                        linearProductAdapter.submitData(lifecycle, result)
//                    } else if (it == 404) {
//                        binding.recyclerView.visibility = GONE
//                        binding.gambarerror.visibility = VISIBLE
//                        binding.errorTitle.visibility = VISIBLE
//                        binding.errorTitle.text = getString(R.string.errorTitle)
//                        binding.errorDesc.visibility = VISIBLE
//                        binding.errorDesc.text = getString(R.string.errorDesc)
//                        binding.resetButton.visibility = VISIBLE
//                    } else if (it == 500) {
//                        binding.recyclerView.visibility = GONE
//                        binding.gambarerror.visibility = VISIBLE
//                        binding.errorTitle.visibility = VISIBLE
//                        binding.errorTitle.text = getString(R.string.errorTitle500)
//                        binding.errorDesc.visibility = VISIBLE
//                        binding.errorDesc.text = getString(R.string.errorDesc500)
//                        binding.resetButton.visibility = VISIBLE
//                        binding.resetButton.text = getString(R.string.refreshButtonError)
//                    } else {
//                        binding.recyclerView.visibility = GONE
//                        binding.gambarerror.visibility = VISIBLE
//                        binding.errorTitle.visibility = VISIBLE
//                        binding.errorTitle.text = getString(R.string.errorTitleConnection)
//                        binding.errorDesc.visibility = VISIBLE
//                        binding.errorDesc.text = getString(R.string.errorDescConnection)
//                        binding.resetButton.visibility = VISIBLE
//                        binding.resetButton.text = getString(R.string.refreshButtonError)
//                    }
//
//                }

                linearProductAdapter.submitData(lifecycle, result)
                Log.d("cekDataPaging", result.toString())
            }

        }


    }

    private fun settingAdapter() {
        binding.shimmer.visibility = GONE
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

        binding.chipgroup.removeAllViews()

//        setFragmentResultListener("filter") { _, bundle ->
//            var sort = bundle.getString("selectedText1")
//            var brand = bundle.getString("selectedText2")
//            var textTerendah = bundle.getString("textTerendah")
//            var textTertinggi = bundle.getString("textTertinggi")
//
//            Log.d("cekFragmentResult", sort.toString())
//            Log.d("cekFragmentResult", brand.toString())
//            Log.d("cekFragmentResult", textTerendah.toString())
//            Log.d("cekFragmentResult", textTertinggi.toString())
//        }

        setFragmentResultListener("textFromChipGroup1") { _, bundle ->
            selectedText1 = bundle.getString("bundleKey").toString()
            Log.d("cekSelectedText1", selectedText1!!)
            if (selectedText1!!.isNotEmpty()) {
                val chip = Chip(requireActivity())
                chip.text = selectedText1 // Set the text for the chip
                binding.chipgroup.addView(chip)
                if (selectedText1!!.isNotEmpty()) {
                    updateFilterAndRequestData()
                }
            }
        }

        setFragmentResultListener("textFromChipGroup2") { _, bundle ->
            selectedText2 = bundle.getString("bundleKey").toString()
            Log.d("cekSelectedText2", selectedText2!!)
            if (selectedText2!!.isNotEmpty()) {
                val chip = Chip(requireActivity())
                chip.text = selectedText2 // Set the text for the chip
                binding.chipgroup.addView(chip)
                if (selectedText2!!.isNotEmpty()) {
                    updateFilterAndRequestData()
                }

            }
        }

        setFragmentResultListener("textTerendah") { _, bundle ->
            textTerendah = bundle.getString("bundleKey").toString()
            Log.d("cekTextTerendah", textTerendah!!)
            if (textTerendah!!.isNotEmpty()) {
                val chip = Chip(requireActivity())
                chip.text = "< $textTerendah" // Set the text for the chip
                binding.chipgroup.addView(chip)
                if (textTerendah!!.isNotEmpty()) {
                    updateFilterAndRequestData()
                }

            }
        }

        setFragmentResultListener("textTertinggi") { _, bundle ->
            textTertinggi = bundle.getString("bundleKey").toString()
            Log.d("cekTextTertinggi", textTertinggi!!)
            if (textTertinggi!!.isNotEmpty()) {
                val chip = Chip(requireActivity())
                chip.text = "> $textTertinggi" // Set the text for the chip
                binding.chipgroup.addView(chip)
                if (textTertinggi!!.isNotEmpty()) {
                    updateFilterAndRequestData()
                }

            }
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