package com.example.tokopaerbe.home.transaction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.databinding.ItemRvTransactionBinding
import com.example.tokopaerbe.core.retrofit.response.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val itemClickListener: OnItemClickListener
) :
    ListAdapter<Transaction, TransactionAdapter.ListViewHolder>(TransactionResponseDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemRvTransactionBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)

        holder.binding.buttonUlas.setOnClickListener {
            itemClickListener.onItemClick(transaction.invoiceId)
        }
    }

    class ListViewHolder(var binding: ItemRvTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: Transaction) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.image)
                    .into(itemImage)
                productName.text = data.name
                var totalBarang = 0
                for (i in data.items.indices) {
                    totalBarang += data.items[i].quantity
                }
                binding.totalBarang.text = totalBarang.toString()
                var itemPrice = formatPrice(data.total.toDouble())
                binding.totalHarga.text = "Rp$itemPrice"

                if (data.rating == 0 || data.review.isEmpty()) {
                    binding.buttonUlas.visibility = VISIBLE
                } else {
                    binding.buttonUlas.visibility = GONE
                }
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

    private class TransactionResponseDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.invoiceId == newItem.invoiceId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(invoiceId: String)
    }
}
