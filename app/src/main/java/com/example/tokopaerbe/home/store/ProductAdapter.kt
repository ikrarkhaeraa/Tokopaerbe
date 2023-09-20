package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.MainActivity
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemProductBinding
import com.example.tokopaerbe.retrofit.response.Product
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.lang.reflect.Array
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter (private val onProductClick: (Product) -> Unit) : PagingDataAdapter<Product, ProductAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.productName == newItem.productName
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val productData = getItem(position)
        if (productData != null) {
            holder.bind(productData)
            holder.itemView.setOnClickListener {
                onProductClick(productData)

                firebaseAnalytics = Firebase.analytics
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(FirebaseAnalytics.Param.ITEM_LIST_ID, productData.productId)
                    param(FirebaseAnalytics.Param.ITEM_LIST_NAME, productData.productName)
                    param(FirebaseAnalytics.Param.ITEMS, productData.toString())
                }


            }
        }
    }


    class ListViewHolder(var binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(productData:Product){
            binding.apply{
                Glide.with(itemView)
                    .load(productData.image)
                    .into(binding.itemImage)
                binding.itemTitle.text = productData.productName
                val itemPrice = formatPrice(productData.productPrice.toDouble())
                binding.itemPrice.text = "Rp$itemPrice"
                binding.store.text = productData.store
                binding.ratingTerjual.text = "${productData.productRating} | Terjual ${productData.sale}"
            }
        }

        private fun formatPrice(price: Double): String {
            val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID")) // Use the appropriate locale for your formatting
            return numberFormat.format(price)
        }
    }

}