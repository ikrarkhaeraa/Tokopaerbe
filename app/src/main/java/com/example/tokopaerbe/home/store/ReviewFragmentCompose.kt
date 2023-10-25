package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import com.example.mycompose.ui.theme.MyComposeTheme
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.retrofit.response.Review
import com.example.tokopaerbe.core.retrofit.user.UserLogin
import com.example.tokopaerbe.core.utils.ErrorMessage.errorMessage
import com.example.tokopaerbe.core.utils.SealedClass
import com.example.tokopaerbe.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewFragmentCompose : Fragment() {

    private val model: ViewModel by activityViewModels()
    private val args: ComposeDetailProductArgs by navArgs()
    private var productId: String = ""
    private var listReview: List<Review>? = null
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyComposeTheme {
                    ReviewFragmentScreenViewModel()
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ReviewFragmentScreenViewModel() {
        productId = args.productIdCompose
        Log.d("cekReviewCompose", productId)
        LaunchedEffect(key1 = Unit) {
            model.getReviewData(productId)
        }

        var showDetail by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        ProgressBarDemo()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                delay(1000)
                showDetail = true
            }
        }

        if (showDetail) {
            val reviewData = model.reviewData.collectAsStateWithLifecycle().value
            when (reviewData) {
                is SealedClass.Init -> {

                }
                is SealedClass.Loading -> {
                    ProgressBarDemo()
                }
                is SealedClass.Success -> {
                    listReview = reviewData.data.data
                    Log.d("cekListReview", listReview.toString())

                    ReviewFragmentScreen()
                }
                is SealedClass.Error -> {
                    Toast.makeText(requireContext(), reviewData.message.errorMessage(), Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }

    }

    @Composable
    fun ProgressBarDemo() {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = colorResource(id = R.color.primaryColor)
                )
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ErrorStateScreen() {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.detailProduk),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                // Handle click action here
                                findNavController().navigateUp()
                            }
                    )
                },
            )
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smartphone),
                    modifier = Modifier.size(128.dp),
                    contentDescription = null
                )

                Text(
                    text = resources.getString(R.string.errorTitle),
                    fontFamily = FontFamily(Font(R.font.medium)),
                    fontSize = 32.sp,
                )

                Text(
                    text = resources.getString(R.string.errorDesc),
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontSize = 16.sp,
                    modifier = Modifier
                )

                Button(
                    onClick = {
                        model.getReviewData(productId)
                    }
                ) {
                    Text(text = stringResource(id = R.string.refreshButtonError))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReviewFragmentScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.ulasanPembeli),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clickable {
                                    // Handle click action here
                                    findNavController().navigateUp()
                                }
                        )
                    },
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                LazyColumn(Modifier.padding(start = 16.dp)) {
                    listReview?.size?.let {
                        items(it) { index ->
                            Row(Modifier.padding(top = 16.dp)) {
                                Image(
                                    painter = rememberImagePainter(
                                        data = listReview!![index].userImage,
                                        builder = {
                                            crossfade(true)
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp)
                                        .clip(CircleShape)
                                )

                                Column(Modifier.padding(start = 8.dp)) {
                                    (
                                        Text(
                                            text = listReview!![index].userName
                                        )
                                        )

                                    Row {
                                        for (i in 1..listReview!![index].userRating) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_star_17),
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = listReview!![index].userReview,
                                fontFamily = FontFamily(Font(R.font.poppins)),
                                fontSize = 12.sp,
                            )

                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun ReviewFragmentPreview() {
        ReviewFragmentScreen()
    }
}
