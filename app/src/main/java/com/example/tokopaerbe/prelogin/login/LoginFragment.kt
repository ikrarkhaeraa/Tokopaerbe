package com.example.tokopaerbe.prelogin.login

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentLoginBinding
import com.example.tokopaerbe.retrofit.user.UserLogin
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var isEmailValid = false
    private var isPasswordValid = false

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private lateinit var email: String
    private lateinit var password: String
    private var API_KEY = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
    private var firebaseToken = ""
    private val delayMillis = 5000L
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            val userFirstInstallState = model.getUserFirstInstallState().first()
            if (userFirstInstallState) {
//                model.userInstall()
                findNavController().navigate(R.id.action_loginFragment_to_onBoardingFragment)
            }
        }

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics

        val text = getString(R.string.persetujuan)
        val spannableStringBuilder = SpannableStringBuilder(text)

        val startTerms = text.indexOf(resources.getString(R.string.syaratdanketentuan))
        val endTerms = startTerms + resources.getString(R.string.syaratdanketentuan).length
        val startPrivacy = text.indexOf(resources.getString(R.string.kebijakanprivasi))
        val endPrivacy = startPrivacy + resources.getString(R.string.kebijakanprivasi).length

        val startTerms2 = text.indexOf(resources.getString(R.string.syaratdanketentuan))
        val endTerms2 = startTerms + resources.getString(R.string.syaratdanketentuan).length
        val startPrivacy2 = text.indexOf(resources.getString(R.string.kebijakanprivasi))
        val endPrivacy2 = startPrivacy + resources.getString(R.string.kebijakanprivasi).length

        val colorSpanTerms = ForegroundColorSpan(resources.getColor(R.color.primaryColor))
        val colorSpanPrivacy = ForegroundColorSpan(resources.getColor(R.color.primaryColor))

        spannableStringBuilder.setSpan(colorSpanTerms, startTerms, endTerms, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(colorSpanPrivacy, startPrivacy, endPrivacy, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableStringBuilder.setSpan(colorSpanTerms, startTerms2, endTerms2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(colorSpanPrivacy, startPrivacy2, endPrivacy2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.persetujuan.text = spannableStringBuilder

        binding.emailedittext.addTextChangedListener(emailTextWatcher)
        binding.passwordedittext.addTextChangedListener(passwordTextWatcher)
        binding.buttonMasuk.isEnabled = false

        chooseButton()
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateEmail()
            updateSubmitButtonState()
        }

        override fun afterTextChanged(s: Editable?) {
            // Not used in this case
        }
    }

    private val passwordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validatePassword()
            updateSubmitButtonState()
        }

        override fun afterTextChanged(s: Editable?) {
            // Not used in this case
        }
    }

    private fun validateEmail() {
        email = binding.emailedittext.text.toString().trim()

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textFieldEmail.error = getString(R.string.EmailTidakValid)
            isEmailValid = false
        } else {
            binding.textFieldEmail.error = null
            isEmailValid = true
        }
    }

    private fun validatePassword() {
        password = binding.passwordedittext.text.toString()

        if (!password.isEmpty() && password.length < 8) {
            binding.textFieldPassword.error = getString(R.string.PasswordTidakValid)
            isPasswordValid = false
        } else {
            binding.textFieldPassword.error = null
            isPasswordValid = true
        }
    }

    private fun updateSubmitButtonState() {
        val isBothFieldsValid = isEmailValid && isPasswordValid
        val isBothFieldsNotEmpty =
            binding.emailedittext.text?.isNotEmpty() == true && binding.passwordedittext.text?.isNotEmpty() == true

        binding.buttonMasuk.isEnabled = isBothFieldsValid && isBothFieldsNotEmpty
    }

    private fun chooseButton() {
        binding.apply {

            buttonMasuk.setOnClickListener {

                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                    param(FirebaseAnalytics.Param.METHOD, email)
                }

                showLoading(true)
                model.postDataLogin(API_KEY, email, password, firebaseToken)

                lifecycleScope.launch {
                    val it = model.signIn.first()

                    Log.d("cekLogin", it.toString())

                    if (it.code == 200) {
                        model.userLogin()
                        saveUserLogin(
                            UserLogin(
                                it.data.userName,
                                it.data.userImage,
                                it.data.accessToken,
                                it.data.refreshToken,
                                it.data.expiresAt
                            )
                        )

                        GlobalScope.launch(Dispatchers.Main) {
                            delay(delayMillis)
                            login()
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.loginInvalid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }

            buttonDaftar.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                firebaseAnalytics.logEvent("button_click") {
                    param(FirebaseAnalytics.Param.METHOD, "Register Button")
                }
            }
        }
    }

    private fun saveUserLogin(sessionLogin: UserLogin) {
        model.saveSessionLogin(sessionLogin)
    }

    private fun login() {

        lifecycleScope.launch {
            val userName = model.getUserName().first()

            Log.d("loginResponse", userName)
            showLoading(false)
            if (userName.isEmpty()) {
                findNavController().navigate(R.id.action_loginFragment_to_profileFragment)

            } else {
                findNavController().navigate(R.id.prelogin_to_main)
            }
//            findNavController().navigate(R.id.prelogin_to_main)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}