package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.databinding.ItemPlatformPembayaranBinding
import com.example.tokopaerbe.core.retrofit.response.PaymentMethod
import com.squareup.picasso.Picasso

class MetodePembayaranAdapter(
    private val itemClickListener: OnItemClickListener
) :
    ListAdapter<PaymentMethod, MetodePembayaranAdapter.ListViewHolder>(CartEntityDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemPlatformPembayaranBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val payment = getItem(position)
        holder.bind(payment)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(payment.image, payment.label)
        }
    }

    class ListViewHolder(var binding: ItemPlatformPembayaranBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: PaymentMethod) {
            binding.apply {
                Picasso.get().load(data.image).into(itemImage)
                namaPlatform.text = data.label
                if (!data.status) {
                    cardView.alpha = 0.4f
                    cardView.isEnabled = false
                }
            }
        }
    }

    private class CartEntityDiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem.label == newItem.label
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(image: String, name: String)
    }
}
