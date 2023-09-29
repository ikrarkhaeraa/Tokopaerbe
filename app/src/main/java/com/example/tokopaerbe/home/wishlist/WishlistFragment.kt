package com.example.tokopaerbe.home.wishlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.FragmentWishlistBinding
import com.example.tokopaerbe.room.WishlistEntity
import com.example.tokopaerbe.viewmodel.ViewModel
import com.example.tokopaerbe.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var factory: ViewModelFactory
    private val model: ViewModel by viewModels { factory }

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        factory = ViewModelFactory.getInstance(requireContext())

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getWishList().observe(viewLifecycleOwner) { wishList ->
            if (wishList.isNullOrEmpty()) {
                hideEmptyCartUI()
            } else {
                showCartItemsUI()
                binding.totalbarang.text = "${wishList.size} Barang"

                if (model.rvStateWishList) {
                    setLinearLayoutManager(wishList)
                } else {
                    setGridLayoutManager(wishList)
                }

                if (!model.rvStateWishList) {
                    binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)
                }

                binding.changeRV.setOnClickListener {
                    model.rvStateWishList = !model.rvStateWishList
                    toggleLayoutManager(wishList)
                }
            }
        }
    }

    private fun hideEmptyCartUI() {
        binding.divider.visibility = View.GONE
        binding.totalbarang.visibility = View.GONE
        binding.changeRV.visibility = View.GONE
        binding.imageView5.visibility = View.VISIBLE
        binding.textView5.visibility = View.VISIBLE
        binding.descempty.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showCartItemsUI() {
        binding.imageView5.visibility = View.GONE
        binding.textView5.visibility = View.GONE
        binding.descempty.visibility = View.GONE

        binding.divider.visibility = View.VISIBLE
        binding.totalbarang.visibility = View.VISIBLE
        binding.changeRV.visibility = View.VISIBLE
    }

    private fun toggleLayoutManager(wishList: List<WishlistEntity>) {
        if (!model.rvStateWishList) {
            setGridLayoutManager(wishList)
            binding.changeRV.setImageResource(R.drawable.baseline_grid_view_24)
        } else {
            setLinearLayoutManager(wishList)
            binding.changeRV.setImageResource(R.drawable.baseline_format_list_bulleted_24)
        }
    }

    private fun setLinearLayoutManager(wishList: List<WishlistEntity>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = WishlistAdapter(model, myCoroutineScope, this.requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(wishList)
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun setGridLayoutManager(wishList: List<WishlistEntity>) {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = WishListGridAdapter(model, myCoroutineScope, this.requireContext())
        binding.recyclerView.adapter = adapter
        adapter.submitList(wishList)
        binding.recyclerView.visibility = View.VISIBLE
    }
}
