package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentCheckoutBinding
import com.example.tokopaerbe.retrofit.response.PaymentResponse
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CheckoutFragment : Fragment(),CheckoutAdapter.OnItemClickListener {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val args: CheckoutFragmentArgs by navArgs()
    private var productCheckout: ListCheckout = ListCheckout(emptyList())
    private var totalPrice = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
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

        productCheckout = args.productCheckout
        Log.d("cekCheckout", productCheckout.toString())

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CheckoutAdapter(productCheckout.listCheckout, this)
        binding.recyclerView.adapter = adapter

        calculateTotalPrice(productCheckout.listCheckout)

        binding.pilihPembayaran.setOnClickListener {
            lifecycleScope.launch {
                val token = model.getUserToken().first()
                val auth = "Bearer $token"

                model.getPaymentData(auth)
                model.payment.observe(viewLifecycleOwner) {
                    if (it.code == 200) {
                        findNavController().navigate(R.id.action_checkoutFragment_to_pilihPembayaranFragment)
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int, item: CheckoutDataClass) {
        calculateTotalPrice(productCheckout.listCheckout)
    }

    private fun calculateTotalPrice(productCheckout: List<CheckoutDataClass>) {
        totalPrice = productCheckout.sumByDouble { it.productPrice.toDouble() * it.productQuantity }
        updateTotalPriceUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPriceUI() {
        val formattedTotalPrice = formatPrice(totalPrice)
        binding.price.text = "Rp$formattedTotalPrice"
    }

    private fun formatPrice(price: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(
            Locale(
                "id",
                "ID"
            )
        )
        return numberFormat.format(price)
    }

}