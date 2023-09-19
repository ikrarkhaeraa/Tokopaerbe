package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.ImagePainter.State.Empty.painter
import coil.compose.rememberImagePainter
import com.example.mycompose.ui.theme.MyComposeTheme
import com.example.tokopaerbe.R
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.CheckoutFragment
import com.example.tokopaerbe.home.checkout.CheckoutFragmentArgs
import com.example.tokopaerbe.home.checkout.CheckoutFragmentDirections
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.retrofit.response.ProductVariant
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


class ComposeDetailProduct : Fragment() {

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by activityViewModels()
    private val args: ComposeDetailProductArgs by navArgs()
    private var productId: String = ""
    private var listSearchResult: List<String>? = listOf()
    private var productVariant: List<ProductVariant>? = listOf()
    private var isIconBorder = true
//    private var index: Int = 0

    private lateinit var listCheckout: ArrayList<CheckoutDataClass>
    private var productCheckout: ListCheckout = ListCheckout(emptyList())
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyComposeTheme {
                    DetailProductScreenViewModel()
                }
            }
        }
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun DetailProductScreenViewModel(
    ) {


        val userToken = model.getUserToken().collectAsState(initial = null).value
        val token = "Bearer $userToken"
        Log.d("cekTokenForDetail", token)
        model.getDetailProductData(token, productId)


        var price: Int? = 0
        var itemPrice: String? = ""
        var productName: String? = ""
        var sold: String? = ""
        var rating: String? = ""
        var review: String? = ""
        var selectedVariant: String? = ""
        var descProduct: String? = ""
        var ratingGedeDibawah: String? = ""
        var satisfaction: String? = ""
        var totalRating: String? = ""
        var index: Int? = 0
        var stock: Int? = 0
        var store: String? = ""
        var sale: Int? = 0

        if (token.isNotEmpty()) {

            price = model.detail.observeAsState().value?.data?.productPrice
            productName = model.detail.observeAsState().value?.data?.productName
            sold = "${model.detail.observeAsState().value?.data?.sale}"
            rating = model.detail.observeAsState().value?.data?.productRating.toString()
            review = model.detail.observeAsState().value?.data?.totalReview.toString()
            descProduct = model.detail.observeAsState().value?.data?.description
            ratingGedeDibawah = model.detail.observeAsState().value?.data?.productRating.toString()
            satisfaction =
                "${model.detail.observeAsState().value?.data?.totalSatisfaction} pembeli merasa puas"
            totalRating =
                "${model.detail.observeAsState().value?.data?.totalRating} rating ${model.detail.observeAsState().value?.data?.totalReview} ulasan"
            productVariant = model.detail.observeAsState().value?.data?.productVariant
            stock = model.detail.observeAsState().value?.data?.stock
            store = model.detail.observeAsState().value?.data?.store
            sale = model.detail.observeAsState().value?.data?.sale

            Log.d("cekPriceDetailCompose", itemPrice.toString())

            listSearchResult = model.detail.observeAsState().value?.data?.image
            Log.d("cekImage", listSearchResult.toString())

            if (price != null) {

                fun formatPrice(price: Double?): String {
                    val numberFormat = NumberFormat.getNumberInstance(
                        Locale(
                            "id", "ID"
                        )
                    ) // Use the appropriate locale for your formatting
                    return numberFormat.format(price)
                }

                itemPrice = formatPrice(price.toDouble())

            }

        }

        DetailProductScreen(
            price,
            itemPrice,
            productName,
            sold,
            rating,
            review,
            selectedVariant,
            descProduct,
            ratingGedeDibawah,
            satisfaction,
            totalRating,
            listSearchResult,
            productVariant,
            index,
            stock,
            store,
            sale,
        )

    }

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
        ExperimentalFoundationApi::class
    )
    @Composable
    fun DetailProductScreen(
        price: Int? = 0,
        itemPrice: String? = "",
        productName: String? = "",
        sold: String? = "",
        rating: String? = "",
        review: String? = "",
        selectedVariant: String? = "",
        descProduct: String? = "",
        ratingGedeDibawah: String? = "",
        satisfaction: String? = "",
        totalRating: String? = "",
        listSearchResult: List<String>? = listOf(),
        productVariant: List<ProductVariant>? = listOf(),
        index: Int? = 0,
        stock: Int? = 0,
        store: String? = "",
        sale: Int? = 0,
    ) {

        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            if (productName != null) {
                param(FirebaseAnalytics.Param.ITEMS, productName)
            }
        }

        var priceState = remember { mutableStateOf(price) }
        var itemPriceState = remember { mutableStateOf(itemPrice) }
        var selectedVariantState = remember { mutableStateOf(selectedVariant) }
        val selectedVariantIndex = remember { mutableStateOf<Int?>(null) }

        priceState.value = price
        itemPriceState.value = itemPrice
        selectedVariantState.value = selectedVariant
        selectedVariantIndex.value = index

        var isImageChanged by remember { mutableStateOf(true) }

        var imageResource: Painter = if (isImageChanged) {
            painterResource(id = R.drawable.baseline_favorite_border_24)
        } else {
            painterResource(id = R.drawable.baseline_favorite_24)
        }


        productId = args.productIdCompose
        Log.d("cekComposeId", productId)

        fun formatPrice(price: Double?): String {
            val numberFormat = NumberFormat.getNumberInstance(
                Locale(
                    "id", "ID"
                )
            ) // Use the appropriate locale for your formatting
            return numberFormat.format(price)
        }

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Product Detail", modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    Image(painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                // Handle click action here
                                findNavController().navigateUp()
                            })
                },
            )
        },

            bottomBar = {
                BottomAppBar {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        OutlinedButton(
                            onClick = {
                                // Handle button click here
                                listCheckout = ArrayList()
                                if (selectedVariantIndex.value == 0) {

                                    if (productName != null && productVariant != null && stock != null && price != null && listSearchResult != null) {
                                        val productId = productId
                                        val productImage = listSearchResult[0]
                                        val productNameData = productName
                                        val productVariantData = productVariant[0].variantName
                                        val productStock = stock
                                        val productPrice = price
                                        val productQuantity = 1
                                        val product = CheckoutDataClass(
                                            productId,
                                            productImage,
                                            productNameData,
                                            productVariantData,
                                            productStock,
                                            productPrice,
                                            productQuantity
                                        )
                                        listCheckout.add(product)
                                    }

                                } else {

                                    if (productName != null && productVariant != null && stock != null && price != null && listSearchResult != null) {
                                        val productId = productId
                                        val productImage = listSearchResult[0]
                                        val productNameData = productName
                                        val productVariantData = productVariant[1].variantName
                                        val productStock = stock
                                        val productPrice = price
                                        val productQuantity = 1
                                        val product = CheckoutDataClass(
                                            productId,
                                            productImage,
                                            productNameData,
                                            productVariantData,
                                            productStock,
                                            productPrice,
                                            productQuantity
                                        )
                                        listCheckout.add(product)
                                    }

                                }
                                Log.d("ceklistChekout", listCheckout.toString())
                                productCheckout = ListCheckout(listCheckout)

                                findNavController().navigate(
                                    R.id.action_detailProductCompose_to_checkoutFragment,
                                    CheckoutFragmentArgs(productCheckout).toBundle(),
                                    navOptions = null
                                )

                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT) {
                                    param(FirebaseAnalytics.Param.ITEMS, productCheckout.toString())
                                }


                            }, modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.beliLangsung))
                        }
                        Button(
                            onClick = {
                                lifecycleScope.launch {

                                    firebaseAnalytics = Firebase.analytics
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
                                        if (productName != null) {
                                            param(FirebaseAnalytics.Param.ITEMS, productName)
                                        }
                                    }

                                    val productCart = model.getCartforDetail(productId)
                                    Log.d("cekProductCart", productCart?.productId.toString())

                                    if (selectedVariantIndex.value == 0) {

                                        Log.d("cek1", "klik1")

                                        if (productCart.toString() == "null") {

                                            if (productName != null && productVariant != null && stock != null && price != null && listSearchResult != null) {
                                                model.addCartProduct(
                                                    productId,
                                                    productName,
                                                    productVariant[0].variantName,
                                                    stock,
                                                    price,
                                                    1,
                                                    listSearchResult[0],
                                                    false
                                                )
                                                Toast.makeText(
                                                    requireContext(), "Added to cart",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        } else if (productCart?.productId == productId && productCart.quantity < productCart.stock) {
                                            model.quantity(productId, productCart.quantity.plus(1))
                                            Toast.makeText(
                                                requireContext(), "Quantity is update",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {
                                            Toast.makeText(
                                                requireContext(), "Stock is unavailable",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    } else {

                                        Log.d("cek2", "klik2")

                                        if (productCart.toString() == "null") {

                                            if (productName != null && productVariant != null && stock != null && price != null && listSearchResult != null) {
                                                model.addCartProduct(
                                                    productId,
                                                    productName,
                                                    productVariant[1].variantName,
                                                    stock,
                                                    price,
                                                    1,
                                                    listSearchResult[0],
                                                    false
                                                )
                                            }

                                            Toast.makeText(
                                                requireContext(), "Added to cart",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else if (productCart?.productId == productId && productCart.quantity < productCart.stock) {
                                            model.quantity(productId, productCart.quantity.plus(1))
                                            Toast.makeText(
                                                requireContext(), "Quantity is update",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                requireContext(), "Stock is unavailable",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                                }
                            }, modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.keranjang))
                        }
                    }
                }
            }

        ) { padding ->
            // Content of your screen goes here
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {

                val pagerState = rememberPagerState()

                listSearchResult?.size?.let {
                    Box {

                        HorizontalPager(
                            modifier = Modifier.fillMaxSize(),
                            pageCount = it,
                            state = pagerState
                        ) { pageIndex ->
                            val imageUrl = listSearchResult[pageIndex]
                            Image(
                                painter = rememberImagePainter(
                                    data = imageUrl,
                                    builder = {
                                        crossfade(true)
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(309.dp)
                            )
                        }

                        Row(
                            Modifier
                                .height(16.dp)
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(it) { iteration ->
                                val color =
                                    if (pagerState.currentPage == iteration) colorResource(id = R.color.primaryColor) else Color.LightGray
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(8.dp)

                                )
                            }
                        }

                    }

                }

                Row(Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Rp${itemPriceState.value}",
                        fontFamily = FontFamily(Font(R.font.semibold)),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                    )
                    Log.d("cekPriceTitle", itemPriceState.value.toString())

                    Image(painter = painterResource(id = R.drawable.baseline_share_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                val shareIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Product : ${productName}\n" + "Price : $itemPrice\n" + "Link : http://ecommerce.tokopaerbe.com/product/compose/${productId}"
                                    )
                                    type = "text/plain"
                                }
                                startActivity(Intent.createChooser(shareIntent, null))
                            })

                    Image(painter = imageResource,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                //Handle clickable here
                                lifecycleScope.launch {

                                    firebaseAnalytics = Firebase.analytics
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST) {
                                        if (productName != null) {
                                            param(FirebaseAnalytics.Param.ITEMS, productName)
                                        }
                                    }

                                    val productWishlist = model.getWishlistforDetail(productId)
                                    if (productWishlist.toString() == "null") {

                                        if (productName != null && productVariant != null && stock != null && price != null && listSearchResult != null && store != null && rating != null && sale != null) {
                                            isImageChanged = !isImageChanged
                                            model.addWishList(
                                                productId,
                                                productName,
                                                price,
                                                listSearchResult[0],
                                                store,
                                                rating.toFloat(),
                                                sale,
                                                stock,
                                                productVariant[0].variantName,
                                                1
                                            )
                                            Toast
                                                .makeText(
                                                    requireContext(), "Added to wishlist",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }

                                    } else if (productWishlist?.productId == productId) {
                                        model.deleteWishList(productId)
                                        isImageChanged = !isImageChanged
                                        Toast
                                            .makeText(
                                                requireContext(), "Remove from wishlist",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()

                                    }
                                }
                            })
                }

                Text(
                    text = productName.toString(),
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                )

                Row(Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Terjual $sold",
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                    Box(Modifier.padding(start = 8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.box),
                            contentDescription = null,
                            modifier = Modifier.size(width = 80.dp, height = 25.dp)
                        )
                        Row {
                            Image(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(width = 15.dp, height = 15.dp)
                                    .padding(top = 3.dp, start = 4.dp)
                            )
                            Text(
                                text = rating.toString(),
                                modifier = Modifier.padding(start = 4.dp),
                            )
                            Text(
                                text = "($review)",
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }
                    }
                }

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                )

                Text(
                    text = stringResource(id = R.string.pilihVarian),
                    fontFamily = FontFamily(Font(R.font.medium)),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                )

                FlowRow(modifier = Modifier.padding(top = 4.dp, start = 8.dp)) {
                    productVariant?.forEachIndexed { index, variant ->
                        InputChip(
                            selected = selectedVariantIndex.value == index, // Set selected based on the index
                            onClick = {
                                selectedVariantIndex.value = index
                                Log.d("cekIndex", selectedVariantIndex.value.toString())
                                if (selectedVariantIndex.value == 1) {
                                    val priceVariant = productVariant[1].variantPrice
                                    if (price != null) {
                                        priceState.value = price + priceVariant
                                        itemPriceState.value =
                                            formatPrice(priceState.value!!.toDouble())
                                        Log.d("cekPriceVariant", priceState.value.toString())
                                    }
                                } else {
                                    priceState.value = price
                                    Log.d("cekPriceVariant", priceState.value.toString())
                                    itemPriceState.value = formatPrice(price?.toDouble())
                                }
                            },
                            label = {
                                Text(
                                    text = variant.variantName,
                                    fontFamily = FontFamily(Font(R.font.medium)),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp, vertical = 6.dp
                                    ),
                                )
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Text(
                    text = stringResource(id = R.string.deskripsiProductTitle),
                    fontFamily = FontFamily(Font(R.font.medium)),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                )

                Text(
                    text = descProduct.toString(),
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                )

                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Row(Modifier.padding(top = 12.dp)) {
                    Text(
                        text = stringResource(id = R.string.ulasanPembeli),
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                    )
                    Text(
                        text = stringResource(id = R.string.lihatSemua),
                        fontFamily = FontFamily(Font(R.font.medium)),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                findNavController().navigate(
                                    R.id.action_detailProductCompose_to_reviewFragmentCompose,
                                    ReviewFragmentComposeArgs(productId).toBundle(),
                                    navOptions = null
                                )
                            }
                    )
                }

                Row(Modifier.padding(top = 8.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.star_24dp),
                        contentDescription = null,
                        modifier = Modifier.padding(top = 5.dp, start = 16.dp)
                    )
                    Text(
                        text = ratingGedeDibawah.toString(),
                        fontFamily = FontFamily(Font(R.font.semibold)),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.ratingKecildibawah),
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 13.dp)
                    )
                    Column(Modifier.padding(start = 32.dp)) {
                        Text(
                            text = satisfaction.toString(),
                            fontFamily = FontFamily(Font(R.font.bold)),
                            fontSize = 12.sp,
                        )
                        Text(
                            text = totalRating.toString(),
                            fontFamily = FontFamily(Font(R.font.poppins)),
                            fontSize = 12.sp,
                        )
                    }
                }
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                )
            }
        }
    }


    @Preview
    @Composable
    fun DetailProductPreview() {
        DetailProductScreen()
    }

}
