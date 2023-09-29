package com.example.tokopaerbe.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by activityViewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_fragment_activity_home)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            val userLoginState = model.getUserLoginState().first()
            val userName = model.getUserName().first()
            if (!userLoginState) {
                findNavController().navigate(R.id.main_to_prelogin)
            } else if (userLoginState && userName.isEmpty()) {
                findNavController().navigate(R.id.action_main_to_profileFragment)
            }
        }

        val bottomNav: BottomNavigationView? = binding.navView
        val railView: NavigationRailView? = binding.navView600
        val navView: NavigationView? = binding.navView840
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_store,
                R.id.navigation_wishlist,
                R.id.navigation_transaction
            )
        )

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_1 -> {
                    findNavController().navigate(R.id.action_main_to_notificationsFragment)
                }

                R.id.menu_item_2 -> {
                    findNavController().navigate(R.id.action_main_to_cartFragment)
                }

                R.id.menu_item_3 -> {
                    // ke menu
                }
            }
            true
        }

        val appCompatActivity = requireActivity() as AppCompatActivity
        setupActionBarWithNavController(appCompatActivity, navController, appBarConfiguration)
        bottomNav?.setupWithNavController(navController)
        bottomNav?.itemIconTintList = null
        railView?.setupWithNavController(navController)
        railView?.itemIconTintList = null
        navView?.setupWithNavController(navController)
        navView?.itemIconTintList = null

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

        val badgeDrawableNotif = BadgeDrawable.create(requireContext())
        BadgeUtils.attachBadgeDrawable(badgeDrawableNotif, binding.topAppBar, R.id.menu_item_1)
        lifecycleScope.launch {
            while (true) {
                if (isAdded) {
                    model.getUnreadNotificatios(false).observe(viewLifecycleOwner) {
                        Log.d("cekNotifSize", it?.size.toString())
                        if (it.isNullOrEmpty()) {
                            badgeDrawableNotif.isVisible = false
                        } else {
                            badgeDrawableNotif.isVisible = true
                            badgeDrawableNotif.number = it.size
                        }
                    }
                }
                if (!isAdded) {
                    break
                }
                delay(500)
            }
        }

        model.getWishList().observe(viewLifecycleOwner) {
            val badgeDrawableWishList = binding.navView?.getOrCreateBadge(R.id.navigation_wishlist)
            val badgeDrawableWishList600 =
                binding.navView600?.getOrCreateBadge(R.id.navigation_wishlist)
            if (it.isNullOrEmpty()) {
                badgeDrawableWishList?.isVisible = false
                badgeDrawableWishList600?.isVisible = false
            } else {
                badgeDrawableWishList?.isVisible = true
                badgeDrawableWishList?.number = it.size
                badgeDrawableWishList600?.isVisible = true
                badgeDrawableWishList600?.number = it.size
            }
        }
    }
}
