package com.example.tokopaerbe.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentMainBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


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

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    override fun  onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_store, R.id.navigation_wishlist, R.id.navigation_transaction
            )
        )

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_item_1 -> {
                    // ke notif
                } R.id.menu_item_2 -> {
                    findNavController().navigate(R.id.action_main_to_cartFragment)
                } R.id.menu_item_3 -> {
                    // ke menu
                }
            }
            true
        }

        val appCompatActivity = requireActivity() as AppCompatActivity
        setupActionBarWithNavController(appCompatActivity, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null

        lifecycleScope.launch {
            val userName = model.getUserName().first()
            binding.topAppBar.title = userName
        }

        val badgeDrawable = BadgeDrawable.create(requireContext())
        BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.topAppBar, R.id.menu_item_2)
        model.getCartProduct().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                badgeDrawable.isVisible = false
            } else {
                badgeDrawable.isVisible = true
                badgeDrawable.number = it.size
            }
        }


        model.getWishList().observe(viewLifecycleOwner) {
            val badgeDrawableWishList = binding.navView.getOrCreateBadge(R.id.navigation_wishlist)
            if (it.isNullOrEmpty()) {
                badgeDrawableWishList.isVisible = false
            } else {
                badgeDrawableWishList.isVisible = true
                badgeDrawableWishList.number = it.size
            }
        }

    }

}