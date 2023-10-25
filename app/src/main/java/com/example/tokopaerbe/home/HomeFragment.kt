package com.example.tokopaerbe.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.databinding.FragmentHomeBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val model: ViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val myCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        binding.switch1.isChecked =
            AppCompatDelegate.getApplicationLocales().get(0)?.language == "in"

//        binding.testCrash.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
        binding.testCrash.visibility = GONE

        language()
        theme()
        logout()
    }

    private fun logout() {
        binding.logout.setOnClickListener {
            model.deleteAllCart()
            model.deleteAllNotif()
            model.deleteAllWishlist()

            Log.d("cekLogout", "LogoutSuccess")
            (requireActivity() as MainActivity).logout()

            firebaseAnalytics.logEvent("button_click") {
                param(FirebaseAnalytics.Param.METHOD, "Logout Button")
            }
        }
    }

    private fun language() {
        binding.switch1.apply {
            setOnClickListener {
                val language = if (isChecked) "in" else "en"
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }
    }

    private fun theme() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.getIsDarkState().collect { theme ->
                Log.d("cekTheme", theme.toString())
                binding.switch2.apply {
                    isChecked = theme
                    setOnClickListener {
                        launch {
                            model.darkTheme(isChecked)
                        }
                        if (isChecked) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }
    }

}
