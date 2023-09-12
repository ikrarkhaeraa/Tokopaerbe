package com.example.tokopaerbe.home.wishlist

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemCartBinding
import com.example.tokopaerbe.databinding.ItemWishlistBinding
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.room.WishlistEntity
import com.example.tokopaerbe.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class WishlistAdapter(
    private val model: ViewModel,
    private val coroutineScope: CoroutineScope,
    private val context: Context
) :
    ListAdapter<WishlistEntity, WishlistAdapter.ListViewHolder>(WishListEntityDiffCallback()) {


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding =
            ItemWishlistBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val wishlistEntity = getItem(position)
        holder.bind(wishlistEntity)

        val addCartButton = holder.binding.addCart
        addCartButton.setOnClickListener {
//            model.addCartProduct(
//                wishlistEntity.productId,
//                wishlistEntity.productName,
//                wishlistEntity.variantName,
//                wishlistEntity.stock,
//                wishlistEntity.productPrice,
//                1,
//                wishlistEntity.image,
//                false,
//            )

            coroutineScope.launch {

                val productCart = model.getCartforWishlist(wishlistEntity.productId)
                Log.d("cekProductCart", productCart?.productId.toString())

                if (productCart.toString() == "null") {
                    model.addCartProduct(
                        wishlistEntity.productId,
                        wishlistEntity.productName,
                        wishlistEntity.variantName,
                        wishlistEntity.stock,
                        wishlistEntity.productPrice,
                        1,
                        wishlistEntity.image,
                        false
                    )
                    Toast.makeText(
                       context,
                        "Added to cart",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (productCart?.productId == wishlistEntity.productId && productCart.quantity < productCart.stock) {
                    model.quantity(wishlistEntity.productId, productCart.quantity.plus(1))
                    Toast.makeText(
                        context,
                        "Quantity is update",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Stock is unavailable",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        val deleteIcon = holder.binding.deleteIcon
        deleteIcon.setOnClickListener {
            model.deleteWishList(wishlistEntity.productId)
        }
    }

    class ListViewHolder(var binding: ItemWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: WishlistEntity) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.image)
                    .into(itemImage)
                itemTitle.text = data.productName
                var itemPrice = formatPrice(data.productPrice.toDouble())
                binding.itemPrice.text = "Rp$itemPrice"
                store.text = data.store
                ratingTerjual.text = "${data.productRating} | Terjual ${data.sale}"
            }
        }

        private fun formatPrice(price: Double): String {
            val numberFormat = NumberFormat.getNumberInstance(
                Locale(
                    "id",
                    "ID"
                )
            )
            return numberFormat.format(price)
        }
    }

    private class WishListEntityDiffCallback : DiffUtil.ItemCallback<WishlistEntity>() {
        override fun areItemsTheSame(oldItem: WishlistEntity, newItem: WishlistEntity): Boolean {
            return oldItem.productId == newItem.productId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: WishlistEntity, newItem: WishlistEntity): Boolean {
            return oldItem == newItem
        }
    }

}