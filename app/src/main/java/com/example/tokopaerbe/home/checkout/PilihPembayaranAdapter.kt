package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.databinding.ItemMetodePembayaranBinding
import com.example.tokopaerbe.core.retrofit.response.Payment

class PilihPembayaranAdapter(
    private val itemClickListener: MetodePembayaranAdapter.OnItemClickListener
) :
    ListAdapter<Payment, PilihPembayaranAdapter.ListViewHolder>(CartEntityDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemMetodePembayaranBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val payment = getItem(position)
        holder.bind(payment)

        holder.binding.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = MetodePembayaranAdapter(itemClickListener)
        holder.binding.recyclerView.adapter = adapter
        adapter.submitList(payment.item)
    }

    class ListViewHolder(var binding: ItemMetodePembayaranBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: Payment) {
            binding.apply {
                metodePembayaran.text = data.title
                recyclerView.apply {
                    data.item
                }
            }
        }
    }

    private class CartEntityDiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.item == newItem.item
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }
    }
}
