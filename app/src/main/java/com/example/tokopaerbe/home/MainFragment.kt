package com.example.tokopaerbe.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentMainBinding
import com.example.tokopaerbe.prelogin.profile.ProfileFragment
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_fragment_activity_home)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_store, R.id.navigation_wishlist, R.id.navigation_transaction
            )
        )
        val appCompatActivity = requireActivity() as AppCompatActivity
        setupActionBarWithNavController(appCompatActivity, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null

        model.getUserSession().observe(requireActivity()) {
            val username = it.userName
            binding.topAppBar.title = username
        }
    }

}