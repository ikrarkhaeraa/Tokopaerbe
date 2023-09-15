package com.example.tokopaerbe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tokopaerbe.databinding.ActivityMainBinding
import com.example.tokopaerbe.home.checkout.StatusFragmentArgs
import com.example.tokopaerbe.home.store.ComposeDetailProductArgs
import com.example.tokopaerbe.home.transaction.ItemTransaction
import com.example.tokopaerbe.home.transaction.TransactionAdapter
import com.example.tokopaerbe.home.transaction.TransactionDataClass
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectIndexed
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
        cekTheme()
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
                navController.navigate(R.id.main_to_prelogin)
            }
            else if (userName.isEmpty()) {
                navController.navigate(R.id.action_loginFragment_to_profileFragment)
            }
            else {
                navController.navigate(R.id.prelogin_to_main)
            }

        }

    }

    private fun cekTheme() {

        lifecycleScope.launch {
            model.getIsDarkState().collect {theme ->
                if (theme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    fun logout() {
        model.userLogout()
        model.userInstall()
        navController.navigate(R.id.main_to_prelogin)
    }

    fun goToProduct(productId: String) {
//        navController.navigate(R.id.store_to_detail, DetailProductFragmentArgs(productId).toBundle(), navOptions = null)
        navController.navigate(R.id.store_to_detailCompose, ComposeDetailProductArgs(productId).toBundle(), navOptions = null)
    }

    fun goToStatus(item: TransactionDataClass) {
        navController.navigate(R.id.transaction_to_status, StatusFragmentArgs(item).toBundle(), navOptions = null)
    }

}