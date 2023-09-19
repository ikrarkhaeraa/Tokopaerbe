package com.example.tokopaerbe.home.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentHomeBinding
import com.example.tokopaerbe.databinding.FragmentMainBinding
import com.example.tokopaerbe.databinding.FragmentTransactionBinding
import com.example.tokopaerbe.home.checkout.CheckoutAdapter
import com.example.tokopaerbe.home.checkout.CheckoutDataClass
import com.example.tokopaerbe.home.checkout.CheckoutFragmentArgs
import com.example.tokopaerbe.home.checkout.ListCheckout
import com.example.tokopaerbe.home.checkout.StatusFragmentArgs
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransactionFragment : Fragment(), TransactionAdapter.OnItemClickListener {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(false)

        lifecycleScope.launch {
            token = model.getUserToken().first()
            auth = "Bearer $token"
            model.getTransactionData(auth)
        }

        itemTransaction = ArrayList()
        model.transaction.observe(viewLifecycleOwner) {
            if (it.code == 200) {
                showLoading(true)
                binding.imageView5.visibility = GONE
                binding.textView5.visibility = GONE
                binding.descempty.visibility = GONE
                binding.buttonRefresh.visibility = GONE

                showLoading(false)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = TransactionAdapter(this)
                binding.recyclerView.adapter = adapter
                adapter.submitList(it.data)

                it.data.map { transaction ->
                    invoiceId = transaction.invoiceId
                    StatusValue = "Berhasil"
                    tanggalValue = transaction.date
                    waktuValue = transaction.time
                    metodePembayaranValue =  transaction.payment
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
            } else {
                binding.buttonRefresh.setOnClickListener {
                    model.getTransactionData(auth)
                }
            }
        }

    }

    override fun onItemClick(invoiceId: String) {
        item = ItemTransaction(itemTransaction)

        Log.d("cekItemTransaction", item.itemTransaction.size.toString())

        item.itemTransaction.map {
            for(i in item.itemTransaction.indices) {
                if (invoiceId == item.itemTransaction[i].invoiceId) {
                    (requireActivity() as MainActivity).goToStatus(item.itemTransaction[i])
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

}