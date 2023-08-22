package com.example.tokopaerbe.prelogin.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentLoginBinding
import com.example.tokopaerbe.databinding.FragmentRegisterBinding
import com.example.tokopaerbe.home.MainFragment
import com.example.tokopaerbe.prelogin.login.LoginFragment
import com.example.tokopaerbe.prelogin.profile.ProfileFragment
import com.example.tokopaerbe.retrofit.UserSession
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory

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

    private var userName = ""
    private var userImage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            }

            buttonDaftar.setOnClickListener {

                model.postDataRegister(API_KEY, email, password, firebaseToken)

                model.signUp.observe(requireActivity()) {
                    Log.d("cekRegis", it.toString())
                    if (it.code == 200) {

                        val userSession = UserSession(
                            userName,
                            userImage,
                            it.data.accessToken,
                            it.data.refreshToken,
                            it.data.expiresAt,
                            isLogin = true,
                            isfirstInstall = false
                        )

                        // Save the user session
                        saveUserSession(userSession)
                        goToProfile()

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


    private fun saveUserSession(session: UserSession) {
        model.saveUserSession(session)
    }

    private fun goToProfile() {
        findNavController().navigate(R.id.action_registerFragment_to_profileFragment)
    }

}