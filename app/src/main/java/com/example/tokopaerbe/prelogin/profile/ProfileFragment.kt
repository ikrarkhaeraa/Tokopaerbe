package com.example.tokopaerbe.prelogin.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentProfileBinding
import com.example.tokopaerbe.retrofit.user.UserProfile
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var getMyFile: File

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val delayMillis = 5000L

    companion object {
        const val CAMERA_X_RESULT = 700

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    requireContext(), "Tidak mendapatkan permission.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.nameedittext.addTextChangedListener(nameTextWatcher)
        binding.buttonSelesai.isEnabled = false

        binding.circle.setOnClickListener {
            val items = arrayOf(getString(R.string.Kamera), getString(R.string.Galeri))

            MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.pilihGambar))
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> startTakePhoto()
                        1 -> startGallery()
                    }
                }.show()
        }

        sendData()
    }


    private fun saveUserProfile(sessionProfile: UserProfile) {
        model.saveSessionProfile(sessionProfile)
    }


    private fun sendData() {
        binding.buttonSelesai.setOnClickListener {
            showLoading(true)

            lifecycleScope.launch {
                val it = model.getUserToken().first()
                val auth = "Bearer $it"
                Log.d("cekit", it)
                Log.d("cekAUTH", auth)

                val username = binding.nameedittext.text.toString()
                val userName = MultipartBody.Part.createFormData("userName", username)

                val fileRequestBody = getMyFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("userImage", getMyFile.name, fileRequestBody)

                if (it.isNotEmpty()) {
                    model.postDataProfile(auth, userName, imagePart)

                    lifecycleScope.launch {
                        val it = model.profile.first()
                        if (it.code == 200) {

                            val userProfile =
                                UserProfile(
                                    it.data.userName,
                                    it.data.userImage,
                                )
                            // Save the user session
                            saveUserProfile(userProfile)

                            GlobalScope.launch(Dispatchers.Main) {
                                delay(delayMillis)
                                goToHome()
                            }
                        }

                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.AUTHinvalid),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }
    }

    private fun goToHome() {
        showLoading(false)
        findNavController().navigate(R.id.prelogin_to_main)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        createTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.tokopaerbe.fileprovider", // Update with the correct authority
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getMyFile = myFile
            val result = BitmapFactory.decodeFile(getMyFile?.path)
            Glide.with(requireContext()).load(result).circleCrop().into(binding.circle)
            binding.icon.visibility = GONE
        }
    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getMyFile = myFile
            val result = BitmapFactory.decodeFile(getMyFile?.path)
            Glide.with(requireContext()).load(result).circleCrop().into(binding.circle)
            binding.icon.visibility = GONE
        }
    }

    private fun updateSubmitButtonState() {
        val isBothFieldsNotEmpty = binding.nameedittext.text?.isNotEmpty() == true
        binding.buttonSelesai.isEnabled = isBothFieldsNotEmpty
    }

    private val nameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used in this case
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateSubmitButtonState()
        }

        override fun afterTextChanged(s: Editable?) {
            // Not used in this case
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