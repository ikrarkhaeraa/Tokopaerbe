package com.example.tokopaerbe.prelogin.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.tokopaerbe.R
import com.example.tokopaerbe.SectionsPagerAdapter
import com.example.tokopaerbe.databinding.FragmentLoginBinding
import com.example.tokopaerbe.databinding.FragmentMainOnBoardingBinding
import com.example.tokopaerbe.prelogin.login.LoginFragment
import com.example.tokopaerbe.prelogin.register.RegisterFragment
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class MainOnBoardingFragment : Fragment() {

    private var _binding: FragmentMainOnBoardingBinding? = null
    private val binding get() = _binding!!
    private val model: ViewModel by activityViewModels()

    private var currentFragmentPosition = 0
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        val sectionsPagerAdapter = SectionsPagerAdapter(this.requireActivity())
        binding.viewpager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewpager) { _, _ ->
        }.attach()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentFragmentPosition = position
                updateButtonVisibility()
            }
        })

        chooseButton()
    }

    private fun chooseButton() {

        binding.buttonGabungSekarang.setOnClickListener {
            model.userInstall()
            findNavController().navigate(R.id.action_onboardingFragment_to_registerFragment)
            firebaseAnalytics.logEvent("button_click") {
                param(FirebaseAnalytics.Param.METHOD, "Join Now Button")
            }
        }

        binding.buttonLewati.setOnClickListener {
            model.userInstall()
            findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            firebaseAnalytics.logEvent("button_click") {
                param(FirebaseAnalytics.Param.METHOD, "Skip Button")
            }
        }

        binding.buttonSelanjutnya.setOnClickListener {
            if (currentFragmentPosition < 2) {
                currentFragmentPosition++
                binding.viewpager.setCurrentItem(currentFragmentPosition, true)
            }
            firebaseAnalytics.logEvent("button_click") {
                param(FirebaseAnalytics.Param.METHOD, "Next Button")
            }
        }

        updateButtonVisibility()
    }

    private fun updateButtonVisibility() {
        if (currentFragmentPosition < 2) {
            binding.buttonSelanjutnya.visibility = VISIBLE
        } else {
            binding.buttonSelanjutnya.visibility = GONE
        }
    }

}
