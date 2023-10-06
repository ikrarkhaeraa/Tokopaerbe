package com.example.tokopaerbe.prelogin.register

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.retrofit.user.UserRegister
import com.example.tokopaerbe.databinding.FragmentRegisterBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var isEmailValid = false
    private var isPasswordValid = false

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private lateinit var email: String
    private lateinit var password: String
    private var API_KEY = "6f8856ed-9189-488f-9011-0ff4b6c08edc"
    private var firebaseToken = ""
    private val delayMillis = 3000L
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.messaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("cekTask", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                firebaseToken = token

                // Log and toast
                Log.d("cekFCMToken", firebaseToken)
                Toast.makeText(requireContext(), token, Toast.LENGTH_SHORT).show()
            },
        )

        val text = getString(R.string.persetujuanDaftar)
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

        spannableStringBuilder.setSpan(
            colorSpanTerms,
            startTerms,
            endTerms,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            colorSpanPrivacy,
            startPrivacy,
            endPrivacy,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilder.setSpan(
            colorSpanTerms,
            startTerms2,
            endTerms2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            colorSpanPrivacy,
            startPrivacy2,
            endPrivacy2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.persetujuan.text = spannableStringBuilder

        binding.emailedittext.addTextChangedListener(emailTextWatcher)
        binding.passwordedittext.addTextChangedListener(passwordTextWatcher)
        binding.buttonDaftar.isEnabled = false

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

        binding.buttonDaftar.isEnabled = isBothFieldsValid && isBothFieldsNotEmpty
    }

    private fun chooseButton() {
        binding.apply {
            buttonMasuk.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                firebaseAnalytics.logEvent("button_click") {
                    param(FirebaseAnalytics.Param.METHOD, "Login Button")
                }
            }

            buttonDaftar.setOnClickListener {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                    param(FirebaseAnalytics.Param.METHOD, email)
                }

                showLoading(true)
                model.postDataRegister(API_KEY, email, password, firebaseToken)

                lifecycleScope.launch {
                    val it = model.signUp.first()

                    Log.d("cekRegisterResponse", it.data.toString())

                    if (it.code == 200) {
                        model.userLogin()

                        saveUserRegister(
                            UserRegister(
                                it.data.accessToken,
                                it.data.refreshToken,
                                it.data.expiresAt
                            )
                        )

                        GlobalScope.launch(Dispatchers.Main) {
                            delay(delayMillis)
                            goToProfile()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.registerInvalid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun saveUserRegister(sessionRegister: UserRegister) {
        model.saveSessionRegister(sessionRegister)
    }

    private fun goToProfile() {
        showLoading(false)
        findNavController().navigate(R.id.action_registerFragment_to_profileFragment)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
