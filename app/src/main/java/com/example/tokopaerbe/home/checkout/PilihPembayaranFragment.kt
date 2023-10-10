package com.example.tokopaerbe.home.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.retrofit.response.PaymentResponse
import com.example.tokopaerbe.databinding.FragmentPilihPembayaranBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson

class PilihPembayaranFragment : Fragment(), MetodePembayaranAdapter.OnItemClickListener {

    private var _binding: FragmentPilihPembayaranBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private var clickedLabel: String? = null
    private var clickedImage: String? = null
    private lateinit var jsonModel: PaymentResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPilihPembayaranBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = binding.cartToolbar

        val navigationIcon: View = toolbar

        navigationIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PilihPembayaranAdapter(this)
        binding.recyclerView.adapter = adapter

//        model.payment.observe(viewLifecycleOwner) {
//            Log.d("cekPayment", it.toString())
//            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
//            val adapter = PilihPembayaranAdapter(this)
//            binding.recyclerView.adapter = adapter
//            adapter.submitList(it.data)
//        }

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default)

        remoteConfig.fetchAndActivate().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val updated = task.result
                Log.d("cekRemoteConfig", "Config params updated: $updated")
                Toast.makeText(
                    requireContext(),
                    "Fetch and activate succeeded",
                    Toast.LENGTH_SHORT,
                ).show()

                val gson = Gson()
                val stringJson = remoteConfig.getString("Payment")

                if (stringJson.isNotEmpty()) {
                    jsonModel = gson.fromJson(stringJson, PaymentResponse::class.java)
                    Log.d("cekPayment", jsonModel.toString())
                    adapter.submitList(jsonModel.data)
                } else {
                    // probably your remote param not exists
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Fetch failed",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d("cekUpdateKey", "Updated keys: " + configUpdate.updatedKeys)
                if (configUpdate.updatedKeys.contains("Payment")) {
                    remoteConfig.activate().addOnCompleteListener {
                        Log.d("cekRealtime", "masuk")
                        val gson = Gson()
                        val stringJson = remoteConfig.getString("Payment")
                        jsonModel = gson.fromJson(stringJson, PaymentResponse::class.java)
                        adapter.submitList(jsonModel.data)
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w("cekUpdateKey", "Config update error with code: " + error.code, error)
            }
        })
    }

    override fun onItemClick(image: String, name: String) {
        clickedImage = image
        clickedLabel = name
        Log.d("cekclicked", clickedLabel.toString())

        val choosenMethod = bundleOf().apply {
            putString("image", clickedImage)
            putString("label", clickedLabel)
        }

        setFragmentResult("choosenMethod", choosenMethod)
        Log.d("cekChoosenMethod", choosenMethod.toString())

        findNavController().navigateUp()
    }
}
