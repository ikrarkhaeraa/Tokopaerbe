package com.example.tokopaerbe.home.store

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentModalBottomSheetBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentModalBottomSheetBinding? = null
    private val baseTheme: Int = R.style.Theme_Tokopaerbe
    val binding get() = _binding!!

    private var selectedText1: String = ""
    private var selectedText2: String = ""
    private var textTerendah: String = ""
    private var textTertinggi: String = ""

    private val model: ViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val theme = ContextThemeWrapper(requireContext(), baseTheme)
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
            } else {
                selectedText1 = ""
            }
        }

        binding.chipgroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                selectedText2 = selectedChip?.text.toString()
            } else {
                selectedText2 = ""
            }
        }

        binding.editTextTerendah.doOnTextChanged { text, _, _, _ ->
            textTerendah = text.toString()
        }

        binding.editTextTertinggi.doOnTextChanged { text, _, _, _ ->
            textTertinggi = text.toString()
        }

        binding.tampilkanproduk.setOnClickListener {
            model.sort = selectedText1
            model.brand = selectedText2
            model.textTerendah = textTerendah
            model.textTertinggi = textTertinggi

            val filter = bundleOf().apply {
                putString("selectedText1", selectedText1)
                putString("selectedText2", selectedText2)
                putString("textTerendah", textTerendah)
                putString("textTertinggi", textTertinggi)
            }
            Log.d("cekSelectedText", selectedText1.toString())
            Log.d("cekSelectedText", selectedText2.toString())
            setFragmentResult("filter", filter)

            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_LIST_ID, "Filter")
                param(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Filter")
                param(FirebaseAnalytics.Param.ITEMS, arrayOf(filter))
            }

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
