package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentCheckoutBinding
import com.example.tokopaerbe.home.transaction.TransactionDataClass
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CheckoutFragment : Fragment(), CheckoutAdapter.OnItemClickListener {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private var image: String? = null
    private var label: String? = null

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val args: CheckoutFragmentArgs by navArgs()
    private var productCheckout: ListCheckout = ListCheckout(emptyList())
    private var totalPrice = 0.0
    private lateinit var listProductFulfillment: ArrayList<com.example.tokopaerbe.core.retrofit.Item>
    private val item: TransactionDataClass? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

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

        binding.buttonBayar.isEnabled = false

        setFragmentResultListener("choosenMethod") { _, bundle ->
            image = bundle.getString("image").toString()
            label = bundle.getString("label").toString()
            Log.d("method", image.toString())
            Log.d("method", label.toString())

            Glide.with(requireContext()).load(image).into(binding.addCardIcon)
            binding.metodePembayaran.text = label

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO) {
                param(FirebaseAnalytics.Param.CURRENCY, "Rupiah")
                param(FirebaseAnalytics.Param.VALUE, totalPrice)
                param(FirebaseAnalytics.Param.COUPON, "SUMMER_FUN")
                param(FirebaseAnalytics.Param.PAYMENT_TYPE, label.toString())
                param(FirebaseAnalytics.Param.ITEMS, arrayOf(productCheckout).toString())
            }

            binding.buttonBayar.isEnabled = true

            listProductFulfillment = ArrayList()
            productCheckout.listCheckout.map {
                val product = com.example.tokopaerbe.core.retrofit.Item(
                    it.productId,
                    it.productVariant,
                    it.productQuantity
                )
                listProductFulfillment.add(product)

                Log.d("cekfulfillmentData", listProductFulfillment.toString())
            }

            binding.buttonBayar.setOnClickListener {
                binding.buttonBayar.visibility = GONE
                showLoading(true)

                lifecycleScope.launch {
                    val token = model.getUserToken().first()
                    val auth = "Bearer $token"

                    model.postDataFulfillment(auth, label!!, listProductFulfillment)

                    lifecycleScope.launch {
                        model.fulfillment.observe(viewLifecycleOwner) {
                            if (it.code == 200) {
                                findNavController().navigate(
                                    R.id.action_checkoutFragment_to_statusFragment,
                                    StatusFragmentArgs(item, 0).toBundle(),
                                    navOptions = null
                                )
                            }
                        }
                    }
                }

                firebaseAnalytics.logEvent("button_click") {
                    param(FirebaseAnalytics.Param.METHOD, "Payment Button")
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = GONE
        }
    }
}
