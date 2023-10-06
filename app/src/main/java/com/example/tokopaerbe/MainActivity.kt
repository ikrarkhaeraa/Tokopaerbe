package com.example.tokopaerbe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tokopaerbe.databinding.ActivityMainBinding
import com.example.tokopaerbe.home.checkout.StatusFragmentArgs
import com.example.tokopaerbe.home.store.ComposeDetailProductArgs
import com.example.tokopaerbe.home.transaction.TransactionDataClass
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        private const val REQUEST_CODE_PERMISSIONS = 200
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        factory = ViewModelFactory.getInstance(this)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        Firebase.messaging.subscribeToTopic("promo")
            .addOnCompleteListener { task ->
                var msg = "Subscribe Success"
                if (!task.isSuccessful) {
                    msg = "Subscribe Failed"
                }
                Log.d("cekSubs", msg)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }

        model.getRefreshResponseCode().observe(this) {
            if (it == 401) {
                logout()
            }
        }

//        checkUserStatus()
        cekTheme()
    }

    private fun checkUserStatus() {
        lifecycleScope.launch {
            val userFirstInstallState = model.getUserFirstInstallState().first()
            val userLoginState = model.getUserLoginState().first()
            val userName = model.getUserName().first()

            if (userFirstInstallState) {
//                model.userInstall()
                navController.navigate(R.id.action_loginFragment_to_onBoardingFragment)
            } else if (!userLoginState && !userFirstInstallState) {
                navController.navigate(R.id.main_to_prelogin)
            } else if (userName.isEmpty()) {
                navController.navigate(R.id.action_loginFragment_to_profileFragment)
            } else {
                navController.navigate(R.id.prelogin_to_main)
            }
        }
    }

    private fun cekTheme() {
        lifecycleScope.launch {
            model.getIsDarkState().collect { theme ->
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
        model.storeSearchText = null
        model.storeSelectedText1 = null
        model.storeSelectedText2 = null
        model.storeTextTerendah = null
        model.storeTextTertinggi = null
        model.sort = ""
        model.brand = ""
        model.textTerendah = ""
        model.textTertinggi = ""
        //        model.userInstall()
        navController.navigate(R.id.main_to_prelogin)
    }

    fun goToProduct(productId: String) {
//        navController.navigate(R.id.store_to_detail, DetailProductFragmentArgs(productId).toBundle(), navOptions = null)
        navController.navigate(
            R.id.store_to_detailCompose,
            ComposeDetailProductArgs(productId).toBundle(),
            navOptions = null
        )
    }

    fun goToStatus(item: TransactionDataClass, size: Int) {
        navController.navigate(
            R.id.transaction_to_status,
            StatusFragmentArgs(item, size).toBundle(),
            navOptions = null
        )
    }
}
