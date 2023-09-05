import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.databinding.FragmentDialogSearchBinding
import com.example.tokopaerbe.home.store.ProductAdapter
import com.example.tokopaerbe.home.store.SearchAdapter
import com.example.tokopaerbe.home.store.StoreFragment
import com.example.tokopaerbe.pagging.PaggingModel
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DialogSearchFragment : DialogFragment(), SearchAdapter.OnItemClickListener {

    private var _binding: FragmentDialogSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private lateinit var listSearchResult: List<String>
    private var clickedTitle: String? = null


    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogSearchBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())
        return binding.root
    }

    override fun onItemClick(title: String) {
        // Save the clicked title to the member variable
        clickedTitle = title
        setFragmentResult("searchText", bundleOf("bundleKey" to clickedTitle))
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchedittext.requestFocus()
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.searchedittext, InputMethodManager.SHOW_IMPLICIT)

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        val queryTextFlow = MutableStateFlow("")

        coroutineScope.launch {
            queryTextFlow
                .debounce(1000) // Adjust the debounce time as needed (in milliseconds)
                .collect { query ->
                    showLoading(true)
                    lifecycleScope.launch {
                        val userToken = model.getUserToken().first()
                        val token = "Bearer $userToken"
                        Log.d("cekTokeninSearchFragment", token)
                        Log.d("cekQuery", query)

                        model.postDataSearch(token, query)

                        if (isAdded) {
                            showData()
                        }
                    }
                }
        }

        binding.searchedittext.doOnTextChanged { text, _, _, _ ->
            queryTextFlow.value = text.toString()
        }

        binding.searchedittext.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // User pressed Enter, handle the text here
                val searchText = binding.searchedittext.text.toString()
                // Do something with searchText, e.g., call a search function
                Log.d("SearchText", searchText)
                setFragmentResult("searchText", bundleOf("bundleKey" to searchText))


                // Dismiss the keyboard
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchedittext.windowToken, 0)

                // Dismiss the dialog
                dismiss()

                return@setOnKeyListener true
            }
            false
        }

    }

    private fun showData(){
        model.search.observe(viewLifecycleOwner) {
            if (isAdded && it.code == 200) {
                showLoading(false)
                listSearchResult = it.data
                binding.recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
                binding.recyclerView.adapter = SearchAdapter(listSearchResult, this)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear the focus from the searchedittext
        binding.searchedittext.clearFocus()
    }


}