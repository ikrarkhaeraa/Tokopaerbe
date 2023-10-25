package com.example.tokopaerbe.home.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.core.utils.SealedClass
import com.example.tokopaerbe.databinding.FragmentTransactionBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionFragment : Fragment(), TransactionAdapter.OnItemClickListener {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!
    private val model: ViewModel by activityViewModels()
    private var token: String = ""
    private var auth: String = ""

    var invoiceId: String = ""
    var StatusValue: String = ""
    var tanggalValue: String = ""
    var waktuValue: String = ""
    var metodePembayaranValue: String = ""
    var totalPembayaranValue: Int = 0

    private lateinit var itemTransaction: ArrayList<TransactionDataClass>
    private var item: ItemTransaction = ItemTransaction(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideErrorState()
        itemTransaction = ArrayList()

        viewLifecycleOwner.lifecycleScope.launch {
            token = model.getUserToken().first()
            auth = "Bearer $token"
            model.postDataTransaction(auth)
            model.transactionData.collect {
                when (it) {
                    is SealedClass.Init -> {
                        Log.d("cekTransactionState", "masuk Init")
                        hideErrorState()
                    }

                    is SealedClass.Loading -> {
                        Log.d("cekTransactionState", "masuk Loading")
                        hideErrorState()
                        showLoading(true)
                        binding.recyclerView.visibility = GONE
                    }

                    is SealedClass.Success -> {
                        val data = it.data.data
                        Log.d("cekTransactionState", "masuk Success")
                        Log.d("transactionData", it.data.code.toString())
                        showLoading(false)
                        hideErrorState()
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        val adapter = TransactionAdapter(this@TransactionFragment)
                        binding.recyclerView.adapter = adapter
                        adapter.submitList(data)

                        data.map { transaction ->
                            invoiceId = transaction.invoiceId
                            StatusValue = "Berhasil"
                            tanggalValue = transaction.date
                            waktuValue = transaction.time
                            metodePembayaranValue = transaction.payment
                            totalPembayaranValue = transaction.total
                            val product = TransactionDataClass(
                                invoiceId,
                                StatusValue,
                                tanggalValue,
                                waktuValue,
                                metodePembayaranValue,
                                totalPembayaranValue
                            )
                            itemTransaction.add(product)
                        }

                    }

                    is SealedClass.Error -> {
                        Log.d("cekTransactionState", "masuk Error")
                        showLoading(false)
                        showErrorState()
//                        Toast.makeText(requireContext(), it.message.errorMessage(), Toast.LENGTH_SHORT).show()
                    }

                    else -> {

                    }
                }
            }
        }

    }

    override fun onItemClick(invoiceId: String) {
        item = ItemTransaction(itemTransaction)
        val size = itemTransaction.size

        Log.d("cekItemTransaction", item.itemTransaction.size.toString())

        item.itemTransaction.map {
            for (i in item.itemTransaction.indices) {
                if (invoiceId == item.itemTransaction[i].invoiceId) {
                    (requireActivity() as MainActivity).goToStatus(item.itemTransaction[i], size)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun refresh() {
        binding.imageView5.visibility = GONE
        binding.textView5.visibility = GONE
        binding.descempty.visibility = GONE
        binding.buttonRefresh.visibility = GONE
        model.postDataTransaction(auth)
    }

    private fun showErrorState() {
        binding.imageView5.visibility = VISIBLE
        binding.textView5.visibility = VISIBLE
        binding.descempty.visibility = VISIBLE
        binding.buttonRefresh.visibility = VISIBLE
        binding.recyclerView.visibility = GONE
        binding.buttonRefresh.setOnClickListener {
            refresh()
        }
    }

    private fun hideErrorState() {
        binding.imageView5.visibility = GONE
        binding.textView5.visibility = GONE
        binding.descempty.visibility = GONE
        binding.buttonRefresh.visibility = GONE
        binding.recyclerView.visibility = VISIBLE
    }

}
