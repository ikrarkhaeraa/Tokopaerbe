package com.example.tokopaerbe.home.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentCartBinding
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.CheckoutFragmentArgs
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale


class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private var totalPrice = 0.0
    private lateinit var listCheckout: ArrayList<CheckoutDataClass>
    private var productCheckout: ListCheckout = ListCheckout(emptyList())


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.cartToolbar

        val navigationIcon: View = toolbar

        navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        model.getCartProduct().observe(viewLifecycleOwner) { cartList ->
            if (cartList.isNullOrEmpty()) {
                hideEmptyCartUI()
            } else {
                showCartItemsUI(cartList)
                calculateTotalPrice(cartList)

                val selectedItem = cartList.filter { it.isChecked }
                val isAllChecked = cartList.all { it.isChecked }
                val isAnyChecked = cartList.any { it.isChecked }

                binding.checkBox.isChecked = isAllChecked
                if (isAnyChecked) {
                    binding.buttonHapus.visibility = VISIBLE
                    binding.buttonBeli.isEnabled = true
                }

                binding.buttonHapus.setOnClickListener {
                    model.deleteAllCheckedProduct(selectedItem)
                }

                listCheckout = ArrayList()
                binding.buttonBeli.setOnClickListener {
                    selectedItem.map {
                        val productImage = it.image
                        val productName = it.productName
                        val productVariant = it.variantName
                        val productStock = it.stock
                        val productPrice = it.productPrice
                        val productQuantity = it.quantity
                        val product = CheckoutDataClass(
                            productImage,
                            productName,
                            productVariant,
                            productStock,
                            productPrice,
                            productQuantity)
                        listCheckout.add(product)
                    }
                    Log.d("ceklistChekout", listCheckout.toString())
                    productCheckout = ListCheckout(listCheckout)
                    findNavController().navigate(
                        R.id.action_cartFragment_to_checkoutFragment,
                        CheckoutFragmentArgs(productCheckout).toBundle(),
                        navOptions = null
                    )
                }

            }
        }


        binding.checkBox.setOnClickListener {
            model.checkAll(binding.checkBox.isChecked)
        }

        updateTotalPriceUI()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    private fun hideEmptyCartUI() {
        binding.checkBox.visibility = GONE
        binding.textView11.visibility = GONE
        binding.buttonHapus.visibility = GONE
        binding.materialDivider.visibility = GONE
        binding.materialDivider2.visibility = GONE
        binding.textView14.visibility = GONE
        binding.price.visibility = GONE
        binding.buttonBeli.visibility = GONE

        binding.imageView5.visibility = VISIBLE
        binding.textView5.visibility = VISIBLE
        binding.descempty.visibility = VISIBLE

        binding.recyclerView.visibility = GONE
    }

    private fun showCartItemsUI(cartList: List<CartEntity>) {
        binding.imageView5.visibility = GONE
        binding.textView5.visibility = GONE
        binding.descempty.visibility = GONE

        binding.checkBox.visibility = VISIBLE
        binding.textView11.visibility = VISIBLE
        binding.buttonHapus.visibility = GONE
        binding.buttonBeli.isEnabled = false
        binding.materialDivider.visibility = VISIBLE
        binding.materialDivider2.visibility = VISIBLE
        binding.textView14.visibility = VISIBLE
        binding.price.visibility = VISIBLE
        binding.buttonBeli.visibility = VISIBLE

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CartAdapter(model)
        binding.recyclerView.adapter = adapter
        adapter.submitList(cartList)
        binding.recyclerView.visibility = VISIBLE
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


    @SuppressLint("SetTextI18n")
    private fun updateTotalPriceUI() {
        val formattedTotalPrice = formatPrice(totalPrice)
        binding.price.text = "Rp$formattedTotalPrice"
    }

    private fun calculateTotalPrice(cartList: List<CartEntity>) {
        totalPrice = cartList
            .filter { it.isChecked }
            .sumByDouble { it.productPrice.toDouble() * it.quantity }

        updateTotalPriceUI()
    }

}