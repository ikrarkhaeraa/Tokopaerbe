package com.example.tokopaerbe.prelogin.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tokopaerbe.databinding.FragmentOnBoarding3Binding

class OnBoarding3Fragment : Fragment() {

    private var _binding: FragmentOnBoarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnBoarding3Binding.inflate(inflater, container, false)
        return binding.root
    }
}
