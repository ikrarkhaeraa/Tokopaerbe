package com.example.tokopaerbe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tokopaerbe.databinding.ActivityMainBinding
import com.example.tokopaerbe.home.store.DetailProductFragmentArgs
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private val navHostFragment: NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nhf_main) as NavHostFragment
    }
    private val navController by lazy {
        navHostFragment.navController
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        setContentView(binding.root)
        supportActionBar?.hide()

        checkUserStatus()
    }

    private fun checkUserStatus() {

        lifecycleScope.launch {
            val userFirstInstallState = model.getUserFirstInstallState().first()
            val userLoginState = model.getUserLoginState().first()
            val userName = model.getUserName().first()

            if (userFirstInstallState) {
                model.userInstall()
                navController.navigate(R.id.action_loginFragment_to_onBoardingFragment)
            }
            else if (!userLoginState && !userFirstInstallState) {
                navController.navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
            else if (userName.isEmpty()) {
                navController.navigate(R.id.action_onboardingFragment_to_profileFragment)
            }
            else {
                navController.navigate(R.id.prelogin_to_main)
            }

        }

    }

    fun logout() {
        model.userLogout()
        navController.navigate(R.id.main_to_prelogin)
    }

    fun goToProduct(productId: String) {
        navController.navigate(R.id.store_to_detail, DetailProductFragmentArgs(productId).toBundle(), navOptions = null)
    }

}