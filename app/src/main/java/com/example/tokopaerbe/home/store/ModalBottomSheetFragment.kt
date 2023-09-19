package com.example.tokopaerbe.home.store

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentModalBottomSheetBinding
import com.example.tokopaerbe.databinding.FragmentTransactionBinding
import com.example.tokopaerbe.retrofit.user.ValueBottomSheet
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentModalBottomSheetBinding? = null
    private val baseTheme: Int = R.style.Theme_Tokopaerbe
    val binding get() = _binding!!

    private var selectedText1: String = ""
    private var selectedText2: String = ""
    private var textTerendah: String = ""
    private var textTertinggi: String = ""

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val theme = ContextThemeWrapper(requireContext(), baseTheme)
        factory = ViewModelFactory.getInstance(requireContext())
        _binding = FragmentModalBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        binding.reset.visibility = GONE

        selectedText1 = model.sort
        selectedText2 = model.brand
        textTerendah = model.textTerendah
        textTertinggi = model.textTertinggi


        if (selectedText1.isNotEmpty() || selectedText2.isNotEmpty() || textTerendah.isNotEmpty() || textTertinggi.isNotEmpty()) {
            binding.reset.visibility = VISIBLE
        }

        Log.d("cekcek", selectedText1)
        Log.d("cekcek", selectedText2)
        Log.d("cekcek", textTerendah)
        Log.d("cekcek", textTertinggi)

        if (selectedText1.isNotEmpty()) {
            for (i in 0 until binding.chipgroup1.childCount) {
                val chip = binding.chipgroup1.getChildAt(i) as? Chip
                if (chip != null && chip.text.toString() == selectedText1) {
                    chip.isChecked = true
                    Log.d("cekChipText", chip.text.toString())
                    Log.d("cekChip", chip.isChecked.toString())
                    break
                }
            }
        }

        if (selectedText2.isNotEmpty()) {
            for (i in 0 until binding.chipgroup2.childCount) {
                val chip = binding.chipgroup2.getChildAt(i) as? Chip
                if (chip != null && chip.text.toString() == selectedText2) {
                    chip.isChecked = true
                    Log.d("cekChipText", chip.text.toString())
                    Log.d("cekChip", chip.isChecked.toString())
                    break
                }
            }
        }


        if (textTerendah.isNotEmpty()) {
            binding.editTextTerendah.setText(textTerendah)
        }

        if (textTertinggi.isNotEmpty()) {
            binding.editTextTertinggi.setText(textTertinggi)
        }


        binding.chipgroup1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                selectedText1 = selectedChip?.text.toString()
                model.sort = selectedText1
            } else {
                selectedText1 = ""
                model.sort = selectedText1
            }

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_NAME, selectedText1)
            }

        }


        binding.chipgroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                selectedText2 = selectedChip?.text.toString()
                model.brand = selectedText2
            } else {
                selectedText2 = ""
                model.brand = selectedText2
            }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_NAME, selectedText2)
            }
        }


        binding.editTextTerendah.doOnTextChanged { text, _, _, _ ->
            textTerendah = text.toString()
            model.textTerendah = textTerendah
        }


        binding.editTextTertinggi.doOnTextChanged { text, _, _, _ ->
            textTertinggi = text.toString()
            model.textTertinggi = textTertinggi
        }


        binding.tampilkanproduk.setOnClickListener {

            val filter = bundleOf().apply {
                putString("selectedText1", selectedText1)
                putString("selectedText2", selectedText2)
                putString("textTerendah", textTerendah)
                putString("textTertinggi", textTertinggi)
            }
            Log.d("cekSelectedText", selectedText1.toString())
            Log.d("cekSelectedText", selectedText2.toString())
            setFragmentResult("filter", filter)

            dismiss()
        }

        binding.reset.setOnClickListener {
            binding.chipgroup1.clearCheck()
            binding.chipgroup2.clearCheck()
            binding.editTextTerendah.text?.clear()
            binding.editTextTertinggi.text?.clear()
        }

    }

}