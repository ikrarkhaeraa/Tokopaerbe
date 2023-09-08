package com.example.tokopaerbe.home.store

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentModalBottomSheetBinding
import com.example.tokopaerbe.databinding.FragmentTransactionBinding
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class ModalBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentModalBottomSheetBinding? = null
    private val baseTheme: Int = R.style.Theme_Tokopaerbe
    private val binding get() = _binding!!

    private var selectedText1: String = ""
    private var selectedText2: String = ""
    private var textTerendah: String = ""
    private var textTertinggi: String = ""

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private var savedSortValue: String? = null
    private var savedBrandValue: String? = null
    private var savedTextTerendah: String? = null
    private var savedTextTertinggi: String? = null

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



        model.sort.observe(viewLifecycleOwner) {
            savedSortValue = it
            Log.d("cekValueBottomSheet", savedSortValue.toString())
            if (!savedSortValue.isNullOrEmpty()) {

                for (i in 0 until binding.chipgroup1.childCount) {
                    val chip = binding.chipgroup1.getChildAt(i) as? Chip
                    if (chip != null && chip.text.toString() == savedSortValue) {
                        chip.isChecked = true
                        Log.d("cekChipText", chip.text.toString())
                        break
                    }
                }

            }
        }

        model.brand.observe(viewLifecycleOwner) {
            savedBrandValue = it
            Log.d("cekValueBottomSheet", savedBrandValue.toString())
            if (!savedBrandValue.isNullOrEmpty()) {

                for (i in 0 until binding.chipgroup2.childCount) {
                    val chip = binding.chipgroup2.getChildAt(i) as? Chip
                    if (chip != null && chip.text.toString() == savedBrandValue) {
                        chip.isChecked = true
                        Log.d("cekChipText", chip.text.toString())
                        break
                    }
                }

            }
        }

//        model.textTerendah.observe(viewLifecycleOwner) {
//            savedTextTerendah = it
//            Log.d("cekValueBottomSheet", savedTextTerendah.toString())
//            binding.editTextTerendah.setText(savedTextTerendah)
//        }
//
//        model.textTertinggi.observe(viewLifecycleOwner) {
//            savedTextTertinggi = it
//            Log.d("cekValueBottomSheet", savedTextTertinggi.toString())
//            binding.editTextTertinggi.setText(savedTextTertinggi)
//        }


        binding.chipgroup1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                selectedText1 = selectedChip?.text.toString()
                model.setSortValue(selectedText1)
            } else {
                selectedText1 = ""
                model.setSortValue(selectedText1)
            }
        }


        binding.chipgroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                selectedText2 = selectedChip?.text.toString()
                model.setBrandValue(selectedText2)
            } else {
                selectedText2 = ""
                model.setBrandValue(selectedText2)
            }
        }


        binding.editTextTerendah.doOnTextChanged { text, _, _, _ ->
            textTerendah = text.toString()
        }


        binding.editTextTertinggi.doOnTextChanged { text, _, _, _ ->
            textTertinggi = text.toString()
        }


        binding.tampilkanproduk.setOnClickListener {
//            setFragmentResult("textFromChipGroup1", bundleOf("bundleKey" to selectedText1))
//            setFragmentResult("textFromChipGroup2", bundleOf("bundleKey" to selectedText2))
//            setFragmentResult("textTerendah", bundleOf("bundleKey" to textTerendah))
//            setFragmentResult("textTertinggi", bundleOf("bundleKey" to textTertinggi))
            val filter = bundleOf().apply {
                putString("selectedText1", selectedText1)
                putString("selectedText2", selectedText2)
                putString("textTerendah", textTerendah)
                putString("textTertinggi", textTertinggi)
            }
            Log.d("cekSelectedText", selectedText1)
            Log.d("cekSelectedText", selectedText2)
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