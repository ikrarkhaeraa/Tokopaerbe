package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.tokopaerbe.ImageSliderAdapter
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentDetailProductBinding
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.CheckoutFragmentArgs
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


class DetailProductFragment : Fragment() {

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val args: DetailProductFragmentArgs by navArgs()
    private var token: String = ""
    private var productId: String = ""
    private lateinit var listSearchResult: List<String>
    private var isIconBorder = true

    private lateinit var listCheckout: ArrayList<CheckoutDataClass>
    private var productCheckout: ListCheckout = ListCheckout(emptyList())

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            model.getDetailProductData(token, productId)
        }

        model.detail.observe(viewLifecycleOwner) {
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
            var index = 0
            binding.chipGroupVarian.removeAllViews()
            for (i in it.data.productVariant.indices) {
                val chip = Chip(requireActivity())
                chip.text = it.data.productVariant[i].variantName
                chip.isClickable = true
                chip.tag = i
                binding.chipGroupVarian.addView(chip)

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
            binding.totalRating.text = "${it.data.totalRating} rating ${it.data.totalReview} ulasan"

            listSearchResult = it.data.image
            val imageSliderAdapter = ImageSliderAdapter(listSearchResult)
            binding.viewpager.adapter = imageSliderAdapter

            if (listSearchResult.size == 1) {
                binding.tabLayout.visibility = GONE
            } else {
                TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->
                }.attach()
            }

            binding.keranjangButton.setOnClickListener { view ->

                if (selectedVariant.isNullOrEmpty()) {
                    selectedVariant = it.data.productVariant[0].variantName
                    model.addCartProduct(
                        it.data.productId,
                        it.data.productName,
                        selectedVariant,
                        it.data.stock,
                        price,
                        1,
                        it.data.image[0],
                        false,
                        0
                    )
                } else {
                    model.addCartProduct(
                        it.data.productId,
                        it.data.productName,
                        selectedVariant,
                        it.data.stock,
                        price,
                        1,
                        it.data.image[0],
                        false,
                        0
                    )
                }

                Toast.makeText(this.requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                    .show()
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
                            this.requireContext(),
                            "Remove from wishlist",
                            Toast.LENGTH_SHORT
                        )
                            .show()
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
                            this.requireContext(),
                            "Added to wishlist",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
            }


            listCheckout = ArrayList()
            binding.buyNow.setOnClickListener { view ->

                if (selectedVariant.isNullOrEmpty()) {
                    selectedVariant = it.data.productVariant[0].variantName
                    val produdtId = it.data.productId
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
                        productQuantity)
                    listCheckout.add(product)
                } else {
                    val produdtId = it.data.productId
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
                        productQuantity)
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

    private fun setIconFavorite() {
        val iconResource = if (isIconBorder) {
            R.drawable.baseline_favorite_border_24
        } else {
            R.drawable.baseline_favorite_24
        }
        binding.favorite.setImageResource(iconResource)
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

}