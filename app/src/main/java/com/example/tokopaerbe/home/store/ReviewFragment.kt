package com.example.tokopaerbe.home.store

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.databinding.FragmentReviewBinding
import com.example.tokopaerbe.retrofit.response.Review
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReviewFragment : Fragment() {

    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }
    private val args: DetailProductFragmentArgs by navArgs()
    private var productId: String = ""
    private lateinit var listReview: List<Review>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reviewFragment.visibility = GONE
        showLoading(true)

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)

            showLoading(false)
            binding.reviewFragment.visibility = VISIBLE

            val toolbar: androidx.appcompat.widget.Toolbar = binding.reviewToolbar

            val navigationIcon: View = toolbar

            navigationIcon.setOnClickListener {
                findNavController().navigateUp()
            }

            productId = args.productId

            lifecycleScope.launch {
                val it = model.getUserToken().first()
                val token = "Bearer $it"
                model.getReviewData(token, productId)
            }

            model.review.observe(viewLifecycleOwner) {
                Log.d("cekReview", it.toString())

                listReview = it.data
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = ReviewAdapter(listReview)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        factory = ViewModelFactory.getInstance(requireContext())
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
