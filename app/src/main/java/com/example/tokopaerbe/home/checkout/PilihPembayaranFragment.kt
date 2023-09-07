package com.example.tokopaerbe.home.checkout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentCheckoutBinding
import com.example.tokopaerbe.databinding.FragmentPilihPembayaranBinding
import com.example.tokopaerbe.home.store.SearchAdapter
import com.example.tokopaerbe.retrofit.response.PaymentResponse
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PilihPembayaranFragment : Fragment(), MetodePembayaranAdapter.OnItemClickListener {

    private var _binding: FragmentPilihPembayaranBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private var clickedLabel: String? = null
    private var clickedImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPilihPembayaranBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.cartToolbar

        val navigationIcon: View = toolbar

        navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }


        model.payment.observe(viewLifecycleOwner) {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = PilihPembayaranAdapter(this)
            binding.recyclerView.adapter = adapter
            adapter.submitList(it.data)
        }

    }

    override fun onItemClick(image: String, name: String) {
        clickedImage = image
        clickedLabel = name
        Log.d("cekclicked", clickedLabel.toString())

        val choosenMethod = bundleOf().apply {
            putString("image", clickedImage)
            putString("label", clickedLabel)
        }

        setFragmentResult("choosenMethod", choosenMethod)
        Log.d("cekChoosenMethod", choosenMethod.toString())

        findNavController().navigateUp()
    }

}