package com.example.tokopaerbe.home.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemCartBinding
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.viewmodel.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(private val model: ViewModel) :
    ListAdapter<CartEntity, CartAdapter.ListViewHolder>(CartEntityDiffCallback()) {

    private var totalToggleValue = 1
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val cartEntity = getItem(position)
        holder.bind(cartEntity)

        val togglePlus = holder.binding.togglePlus
        val toggleMinus = holder.binding.toggleMinus
        val toggleTotal = holder.binding.toggleTotal

        val initialTotal = cartEntity.quantity
        toggleTotal.text = initialTotal.toString()

        val checkBox = holder.binding.checkBox2
        checkBox.isChecked = cartEntity.isChecked

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                model.isChecked(cartEntity.productId, true)
            } else {
                model.isChecked(cartEntity.productId, false)
            }
        }


        val deleteIcon = holder.binding.deleteIcon
        deleteIcon.setOnClickListener {
            model.deleteCartProduct(cartEntity.productId)

            firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART) {
                param(FirebaseAnalytics.Param.CURRENCY, "Rupiah")
                param(FirebaseAnalytics.Param.VALUE, cartEntity.productPrice.toString())
                param(FirebaseAnalytics.Param.ITEMS, arrayOf(cartEntity).toString())
            }

        }

        togglePlus.setOnClickListener {
            val currentTotal = toggleTotal.text.toString().toInt()
            val newTotal = (currentTotal + 1).coerceAtMost(cartEntity.stock)
            toggleTotal.text = newTotal.toString()

            totalToggleValue = newTotal
            model.quantity(cartEntity.productId, totalToggleValue)
        }

        toggleMinus.setOnClickListener {
            val currentTotal = toggleTotal.text.toString().toInt()
            val newTotal = (currentTotal - 1).coerceAtLeast(1)
            toggleTotal.text = newTotal.toString()

            totalToggleValue = newTotal
            model.quantity(cartEntity.productId, totalToggleValue)
        }

    }

    class ListViewHolder(var binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: CartEntity) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.image)
                    .into(itemImage)
                itemTitle.text = data.productName
                variant.text = data.variantName

                if (data.stock < 5) {
                    stock.text = "Sisa ${data.stock}"
                    stock.setTextColor(itemView.context.getColor(R.color.error))
                } else {
                    stock.text = "Sisa ${data.stock}"
                }

                var itemPrice = formatPrice(data.productPrice.toDouble())
                binding.itemPrice.text = "Rp$itemPrice"
                checkBox2.isChecked = data.isChecked
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

    private class CartEntityDiffCallback : DiffUtil.ItemCallback<CartEntity>() {
        override fun areItemsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem.productId == newItem.productId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
            return oldItem == newItem
        }
    }

}