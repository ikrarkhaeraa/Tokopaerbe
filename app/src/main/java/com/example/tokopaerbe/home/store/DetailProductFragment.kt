package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tokopaerbe.ImageSliderAdapter
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentDetailProductBinding
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.CheckoutFragmentArgs
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private val model: ViewModel by activityViewModels()
    private val args: DetailProductFragmentArgs by navArgs()
    private var productId: String = ""
    private lateinit var listSearchResult: List<String>
    private var isIconBorder = true
    private var index: Int = 0

    private lateinit var listCheckout: ArrayList<CheckoutDataClass>
    private var productCheckout: ListCheckout = ListCheckout(emptyList())

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gambarerror.visibility = GONE
        binding.errorTitle.visibility = GONE
        binding.errorDesc.visibility = GONE
        binding.refreshButton.visibility = GONE

        binding.detailFragment.visibility = GONE
        showLoading(true)

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)

            showLoading(false)
            binding.detailFragment.visibility = VISIBLE

            val toolbar: androidx.appcompat.widget.Toolbar = binding.detailProductToolbar

            val navigationIcon: View = toolbar

            navigationIcon.setOnClickListener {
                findNavController().navigateUp()
            }

            productId = args.productId

            lifecycleScope.launch {
                val userToken = model.getUserToken().first()
                val token = "Bearer $userToken"
                Log.d("cekTokenForDetail", token)
//                model.getDetailProductData(token, productId)
            }

            model.detail.observe(viewLifecycleOwner) {
                if (it.code != 200) {
                    binding.gambarerror.visibility = VISIBLE
                    binding.errorTitle.visibility = VISIBLE
                    binding.errorDesc.visibility = VISIBLE
                    binding.refreshButton.visibility = VISIBLE
                    binding.refreshButton.setOnClickListener {
                        lifecycleScope.launch {
                            val userToken = model.getUserToken().first()
                            val token = "Bearer $userToken"
                            Log.d("cekTokenForDetail", token)
//                            model.getDetailProductData(token, productId)
                        }
                    }
                } else {
                    Log.d("cekDetailProduct", it.data.toString())
                    Log.d("cekPrice", it.data.productPrice.toString())

                    var price = it.data.productPrice
                    var itemPrice = formatPrice(price.toDouble())
                    binding.price.text = "Rp$itemPrice"
                    binding.productName.text = it.data.productName
                    binding.sold.text = "Terjual ${it.data.sale}"
                    binding.rating.text = it.data.productRating.toString()
                    binding.review.text = it.data.totalReview.toString()

                    var selectedVariant = ""
                    binding.chipGroupVarian.removeAllViews()
                    for (i in it.data.productVariant.indices) {
                        val chip = Chip(requireActivity(), null, R.style.ChipInput)
                        chip.text = it.data.productVariant[i].variantName
                        chip.isClickable = true
                        chip.tag = i
                        binding.chipGroupVarian.addView(chip)

                        if (chip.tag == 0) {
                            chip.isChecked = true
                        }

                        chip.setOnClickListener { view ->
                            selectedVariant = (view as Chip).text.toString()
                            index = view.tag as Int

                            if (index == 1) {
                                val priceNormal = it.data.productPrice
                                val priceVariant = it.data.productVariant[1].variantPrice
                                price = priceNormal + priceVariant
                                itemPrice = formatPrice(price.toDouble())
                                binding.price.text = "Rp$itemPrice"
                            } else {
                                price = it.data.productPrice
                                itemPrice = formatPrice(price.toDouble())
                                binding.price.text = "Rp$itemPrice"
                            }

                            Log.d("cekPrice", price.toString())
                            Log.d("cekIndexChip", index.toString())
                            Log.d("cekSelectedVariant", selectedVariant)
                        }
                    }

                    binding.descproduct.text = it.data.description
                    binding.ratingGedeDibawah.text = it.data.productRating.toString()
                    binding.satisfaction.text = "${it.data.totalSatisfaction} pembeli merasa puas"
                    binding.totalRating.text =
                        "${it.data.totalRating} rating ${it.data.totalReview} ulasan"

                    listSearchResult = it.data.image
                    val imageSliderAdapter = ImageSliderAdapter(listSearchResult)
                    binding.viewpager.adapter = imageSliderAdapter

                    if (listSearchResult.size == 1) {
                        binding.tabLayout.visibility = GONE
                    } else {
                        TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->
                        }.attach()
                    }

                    binding.keranjangButton.setOnClickListener { _ ->

                        lifecycleScope.launch {
                            val productCart = model.getCartforDetail(it.data.productId)
                            Log.d("cekProductCart", productCart?.productId.toString())

                            if (selectedVariant.isEmpty()) {
                                selectedVariant = it.data.productVariant[0].variantName

                                Log.d("cek1", "klik1")

                                if (productCart.toString() == "null") {
                                    model.addCartProduct(
                                        it.data.productId,
                                        it.data.productName,
                                        selectedVariant,
                                        it.data.stock,
                                        it.data.productPrice,
                                        1,
                                        it.data.image[0],
                                        false
                                    )
                                    Toast.makeText(
                                        requireContext(),
                                        "Added to cart",
                                        LENGTH_SHORT
                                    ).show()
                                } else if (productCart?.productId == it.data.productId && productCart.quantity < productCart.stock) {
                                    model.quantity(it.data.productId, productCart.quantity.plus(1))
                                    Toast.makeText(
                                        requireContext(),
                                        "Quantity is update",
                                        LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Stock is unavailable",
                                        LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.d("cek2", "klik2")

                                if (productCart.toString() == "null") {
                                    model.addCartProduct(
                                        it.data.productId,
                                        it.data.productName,
                                        selectedVariant,
                                        it.data.stock,
                                        it.data.productPrice,
                                        1,
                                        it.data.image[0],
                                        false
                                    )
                                    Toast.makeText(
                                        requireContext(),
                                        "Added to cart",
                                        LENGTH_SHORT
                                    ).show()
                                } else if (productCart?.productId == it.data.productId && productCart.quantity < productCart.stock) {
                                    model.quantity(it.data.productId, productCart.quantity.plus(1))
                                    Toast.makeText(
                                        requireContext(),
                                        "Quantity is update",
                                        LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Stock is unavailable",
                                        LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    binding.lihatSemua.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_detailProduct_to_reviewFragment,
                            ReviewFragmentArgs(productId).toBundle(),
                            navOptions = null
                        )
                    }

                    model.getWishList().observe(viewLifecycleOwner) { wishList ->
                        val isFavList = wishList.orEmpty()

                        val isFav = isFavList.any { wishListEntity ->
                            wishListEntity.productId == it.data.productId
                        }

                        if (isFav) {
                            binding.favorite.setImageResource(R.drawable.baseline_favorite_24)

                            binding.favorite.setOnClickListener { view ->
                                model.deleteWishList(it.data.productId)
                                isIconBorder = !isIconBorder
                                Toast.makeText(
                                    requireContext(),
                                    "Remove from wishlist",
                                    LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            binding.favorite.setImageResource(R.drawable.baseline_favorite_border_24)

                            binding.favorite.setOnClickListener { view ->
                                model.addWishList(
                                    it.data.productId,
                                    it.data.productName,
                                    price,
                                    it.data.image[0],
                                    it.data.store,
                                    it.data.productRating,
                                    it.data.sale,
                                    it.data.stock,
                                    it.data.productVariant[0].variantName,
                                    1
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Added to wishlist",
                                    LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    binding.share.setOnClickListener { view ->
                        val shareIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Product : ${it.data.productName}\n" + "Price : $itemPrice\n" + "Link : http://ecommerce.tokopaerbe.com/product/${it.data.productId}"
                            )
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(shareIntent, null))
                    }

                    listCheckout = ArrayList()
                    binding.buyNow.setOnClickListener { view ->

                        if (selectedVariant.isNullOrEmpty()) {
                            selectedVariant = it.data.productVariant[0].variantName
                            val productId = it.data.productId
                            val productImage = it.data.image[0]
                            val productName = it.data.productName
                            val productVariant = selectedVariant
                            val productStock = it.data.stock
                            val productPrice = it.data.productPrice
                            val productQuantity = 1
                            val product = CheckoutDataClass(
                                productId,
                                productImage,
                                productName,
                                productVariant,
                                productStock,
                                productPrice,
                                productQuantity
                            )
                            listCheckout.add(product)
                        } else {
                            val productId = it.data.productId
                            val productImage = it.data.image[0]
                            val productName = it.data.productName
                            val productVariant = selectedVariant
                            val productStock = it.data.stock
                            val productPrice = it.data.productPrice
                            val productQuantity = 1
                            val product = CheckoutDataClass(
                                productId,
                                productImage,
                                productName,
                                productVariant,
                                productStock,
                                productPrice,
                                productQuantity
                            )
                            listCheckout.add(product)
                        }
                        Log.d("ceklistChekout", listCheckout.toString())
                        productCheckout = ListCheckout(listCheckout)
                        findNavController().navigate(
                            R.id.action_detailProduct_to_checkoutFragment,
                            CheckoutFragmentArgs(productCheckout).toBundle(),
                            navOptions = null
                        )
                    }
                }
            }
        }
    }

    private fun formatPrice(price: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(
            Locale(
                "id",
                "ID"
            )
        ) // Use the appropriate locale for your formatting
        return numberFormat.format(price)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = GONE
        }
    }
}
