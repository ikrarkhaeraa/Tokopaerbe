package com.example.tokopaerbe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.tokopaerbe.databinding.ActivityMainBinding
import com.example.tokopaerbe.home.MainFragment
import com.example.tokopaerbe.prelogin.login.LoginFragment
import com.example.tokopaerbe.prelogin.onboarding.MainOnBoardingFragment
import com.example.tokopaerbe.prelogin.profile.ProfileFragment
import com.example.tokopaerbe.prelogin.register.RegisterFragment
import com.example.tokopaerbe.retrofit.UserSession
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory

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

//        checkUserStatus()
    }

    private fun checkUserStatus() {
        model.getUserSession().observe(this) {
            if (!it.isLogin) {
                navController.navigate(R.id.action_onboardingFragment_to_loginFragment)
            }
            else if (it.userName.isEmpty()) {
                navController.navigate(R.id.action_onboardingFragment_to_profileFragment)
            }
            else {
                goToHomePage()
            }
        }

    }


    private fun goToProfile() {
        val fragmentManager = supportFragmentManager
        val profileFragment = ProfileFragment()
        val fragment =
            fragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)
        if (fragment !is ProfileFragment) {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame_container,
                    profileFragment,
                    ProfileFragment::class.java.simpleName
                )
                .commit()
        }
    }


    private fun goToOnBoarding() {
        val fragmentManager = supportFragmentManager
        val onboardingFragment = MainOnBoardingFragment()
        val fragment =
            fragmentManager.findFragmentByTag(MainOnBoardingFragment::class.java.simpleName)
        if (fragment !is MainOnBoardingFragment) {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame_container,
                    onboardingFragment,
                    MainOnBoardingFragment::class.java.simpleName
                )
                .commit()
        }
    }

    private fun goToLogin() {
        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment =
            fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)
        if (fragment !is LoginFragment) {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame_container,
                    loginFragment,
                    LoginFragment::class.java.simpleName
                )
                .commit()
        }
    }

    private fun goToHomePage() {
        val fragmentManager = supportFragmentManager
        val mainFragment = MainFragment()
        val fragment =
            fragmentManager.findFragmentByTag(MainFragment::class.java.simpleName)
        if (fragment !is MainFragment) {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame_container,
                    mainFragment,
                    MainFragment::class.java.simpleName
                )
                .commit()
        }
    }

    fun logout() {
        navController.navigate(R.id.main_to_prelogin)
    }

}