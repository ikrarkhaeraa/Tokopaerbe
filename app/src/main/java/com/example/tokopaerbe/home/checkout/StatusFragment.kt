package com.example.tokopaerbe.home.checkout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentPilihPembayaranBinding
import com.example.tokopaerbe.databinding.FragmentStatusBinding
import com.example.tokopaerbe.home.transaction.ItemTransaction
import com.example.tokopaerbe.home.transaction.TransactionDataClass
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private var ratingBar: Int = 0
    private var review: String = ""

    private val args: StatusFragmentArgs? by navArgs()
    private var itemTransaction: TransactionDataClass? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemTransaction = args?.item

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            ratingBar = rating.toInt()
        }

        binding.reviewedittext.addTextChangedListener(reviewTextWatcher)

        if (model.fulfillment.value?.code == 200 && itemTransaction.toString() == "null") {
            model.fulfillment.observe(viewLifecycleOwner) {
                binding.idTransaksiValue.text = it.data.invoiceId
                binding.StatusValue.text = "Berhasil"
                binding.tanggalValue.text = it.data.date
                binding.waktuValue.text = it.data.time
                binding.metodePembayaranValue.text = it.data.payment
                binding.totalPembayaranValue.text = it.data.total.toString()

                binding.buttonSelesai.setOnClickListener { view ->
                    lifecycleScope.launch {
                        val token = model.getUserToken().first()
                        val auth = "Bearer $token"

                        model.postDataRating(auth, it.data.invoiceId, ratingBar, review)
                        Log.d("cekStatusData", ratingBar.toString())
                        Log.d("cekStatusData", review)

                        model.rating.observe(viewLifecycleOwner) { ratingResponse ->
                            if (ratingResponse.code == "200") {
                                findNavController().navigate(R.id.action_statusFragment_to_main_navigation)
                            }
                        }
                    }
                }
            }
        } else {
            Log.d("cekItemTransaction", itemTransaction.toString())
                binding.idTransaksiValue.text = itemTransaction?.invoiceId
                binding.StatusValue.text = "Berhasil"
                binding.tanggalValue.text = itemTransaction?.tanggalValue
                binding.waktuValue.text = itemTransaction?.waktuValue
                binding.metodePembayaranValue.text = itemTransaction?.metodePembayaranValue
                binding.totalPembayaranValue.text = itemTransaction?.totalPembayaranValue.toString()

                binding.buttonSelesai.setOnClickListener { view ->
                    lifecycleScope.launch {
                        val token = model.getUserToken().first()
                        val auth = "Bearer $token"

                        model.postDataRating(auth, itemTransaction!!.invoiceId, ratingBar, review)
                        Log.d("cekStatusData", ratingBar.toString())
                        Log.d("cekStatusData", review)

                        model.rating.observe(viewLifecycleOwner) { ratingResponse ->
                            if (ratingResponse.code == "200") {
                                findNavController().navigate(R.id.action_statusFragment_to_main_navigation)
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
                    findNavController().navigateUp()
                    Log.d("cekKlik", "cek2")
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
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

}