package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.retrofit.RatingRequestBody
import com.example.tokopaerbe.core.utils.ErrorMessage.errorMessage
import com.example.tokopaerbe.core.utils.SealedClass
import com.example.tokopaerbe.databinding.FragmentStatusBinding
import com.example.tokopaerbe.home.transaction.TransactionDataClass
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private val model: ViewModel by activityViewModels()
    private var ratingBar: Int? = null
    private var review: String? = null

    private val args: StatusFragmentArgs? by navArgs()
    private var itemTransaction: TransactionDataClass? = null
    private var size: Int = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics
        itemTransaction = args?.item
        size = args?.size!!

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            ratingBar = rating.toInt()
        }

        binding.reviewedittext.addTextChangedListener(reviewTextWatcher)

        binding.idTransaksiValue.text = itemTransaction?.invoiceId
        binding.StatusValue.text = getString(R.string.statusValue)
        binding.tanggalValue.text = itemTransaction?.tanggalValue
        binding.waktuValue.text = itemTransaction?.waktuValue
        binding.metodePembayaranValue.text = itemTransaction?.metodePembayaranValue
        val totalPrice = formatPrice(itemTransaction?.totalPembayaranValue!!.toDouble())
        binding.totalPembayaranValue.text = "Rp$totalPrice"

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
            param(
                FirebaseAnalytics.Param.TRANSACTION_ID,
                itemTransaction!!.invoiceId
            )
            param(FirebaseAnalytics.Param.AFFILIATION, "Google Store")
            param(FirebaseAnalytics.Param.CURRENCY, "Rupiah")
            param(FirebaseAnalytics.Param.VALUE, itemTransaction?.totalPembayaranValue.toString())
            param(FirebaseAnalytics.Param.COUPON, "SUMMER_FUN")
            param(
                FirebaseAnalytics.Param.ITEMS,
                arrayOf(itemTransaction).toString()
            )
        }

        binding.buttonSelesai.setOnClickListener { view ->

            firebaseAnalytics.logEvent("button_click") {
                param(
                    FirebaseAnalytics.Param.METHOD,
                    "Finish Transaction Button"
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val token = model.getUserToken().first()
                val auth = "Bearer $token"
                val requestBody = RatingRequestBody(itemTransaction!!.invoiceId, ratingBar, review)
                model.postDataRating(auth, requestBody)
                Log.d("cekStatusData", ratingBar.toString())
                Log.d("cekStatusData", review.toString())
                model.ratingData.collect {
                    when (it) {
                        is SealedClass.Loading -> {
                            binding.buttonSelesai.visibility = INVISIBLE
                            showLoading(true)
                        }

                        is SealedClass.Success -> {
                            findNavController().navigate(R.id.action_statusFragment_to_main_navigation)
                        }

                        is SealedClass.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message.errorMessage(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {

                        }
                    }
                }

            }
        }

        Log.d("cekArgumen", itemTransaction.toString())

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (itemTransaction.toString() == "null") {
                    Log.d("cekKlik", "cek1")
                    findNavController().navigate(R.id.action_statusFragment_to_main_navigation)
                } else {
                    for (i in 1..size) {
                        findNavController().navigateUp()
                    }
                    Log.d("cekKlik", "cek2")
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val reviewTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            review = binding.reviewedittext.text.toString()
        }

        override fun afterTextChanged(s: Editable?) {
            // Not used in this case
        }
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
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
