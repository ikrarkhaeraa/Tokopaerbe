package com.example.tokopaerbe.home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tokopaerbe.R
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import java.time.format.TextStyle


class ComposeDetailProduct : Fragment() {

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val args: DetailProductFragmentArgs by navArgs()
    private var productId: String = ""
    private lateinit var listSearchResult: List<String>
    private var isIconBorder = true
    private var index: Int = 0

    private lateinit var listCheckout: ArrayList<CheckoutDataClass>
    private var productCheckout: ListCheckout = ListCheckout(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DetailProductScreen()
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun DetailProductScreen() {

        val scrollState = rememberScrollState()

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Product Detail",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    Image(painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                // Handle click action here
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
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.beliLangsung))
                        }
                        Button(
                            onClick = {
                                // Handle button click here
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.keranjang))
                        }
                    }
                }
            }

        ) {
            it.apply {
                // Content of your screen goes here
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.thumbnail),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(309.dp)
                            .padding(top = 64.dp)
                    )

                    Row(Modifier.padding(top = 12.dp)) {
                        Text(
                            text = stringResource(id = R.string.price),
                            fontFamily = FontFamily(Font(R.font.semibold)),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .weight(1f),
                            color = colorResource(id = R.color.lightonsurfacevariant)
                        )
                        Image(painter = painterResource(id = R.drawable.baseline_share_24),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {

                                })
                        Image(painter = painterResource(id = R.drawable.baseline_favorite_border_24),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {

                                })
                    }

                    Text(
                        text = stringResource(id = R.string.spec),
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                        color = colorResource(id = R.color.lightonsurfacevariant)
                    )

                    Row(Modifier.padding(top = 10.dp)) {
                        Text(
                            text = stringResource(id = R.string.sold),
                            fontFamily = FontFamily(Font(R.font.poppins)),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp),
                            color = colorResource(id = R.color.lightonsurfacevariant)
                        )
                        Box(Modifier.padding(start = 8.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.box),
                                contentDescription = null,
                                modifier = Modifier.size(width = 70.dp, height = 21.dp)
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
                                    text = stringResource(id = R.string.productRating),
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = colorResource(id = R.color.lightonsurfacevariant)
                                )
                                Text(
                                    text = stringResource(id = R.string.totalRating),
                                    modifier = Modifier.padding(start = 6.dp),
                                    color = colorResource(id = R.color.lightonsurfacevariant)
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
                        color = colorResource(id = R.color.lightonsurfacevariant)
                    )

                    FlowRow(modifier = Modifier.padding(top = 8.dp, start = 8.dp)) {
                        repeat(2) {
                            InputChip(
                                selected = false,
                                onClick = { /*TODO*/ },
                                label = {
                                    Text(
                                        text = stringResource(id = R.string.labelVarian),
                                        fontFamily = FontFamily(Font(R.font.medium)),
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = colorResource(id = R.color.lightonsurfacevariant)
                                    )
                                },
                                modifier = Modifier.padding(start = 8.dp))
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
                        color = colorResource(id = R.color.lightonsurfacevariant)
                    )

                    Text(
                        text = stringResource(id = R.string.descProduct),
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                        color = colorResource(id = R.color.lightonsurfacevariant)
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
                            color = colorResource(id = R.color.lightonsurfacevariant)
                        )
                        Text(
                            text = stringResource(id = R.string.lihatSemua),
                            fontFamily = FontFamily(Font(R.font.medium)),
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.primaryColor),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }

                    Row(Modifier.padding(top = 8.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.star_24dp),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 5.dp, start = 16.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.productRating),
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
                                text = stringResource(id = R.string.totalSatisfaction),
                                fontFamily = FontFamily(Font(R.font.bold)),
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.lightonsurfacevariant)
                            )
                            Text(
                                text = stringResource(id = R.string.ratingTitle),
                                fontFamily = FontFamily(Font(R.font.poppins)),
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.lightonsurfacevariant)
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
    }


    @Preview
    @Composable
    fun DetailProductPreview() {
        DetailProductScreen()
    }

}