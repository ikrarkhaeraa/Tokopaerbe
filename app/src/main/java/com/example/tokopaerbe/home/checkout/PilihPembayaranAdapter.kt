package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemCartBinding
import com.example.tokopaerbe.databinding.ItemCheckoutBinding
import com.example.tokopaerbe.databinding.ItemMetodePembayaranBinding
import com.example.tokopaerbe.retrofit.response.Payment
import com.example.tokopaerbe.retrofit.response.PaymentResponse
import com.example.tokopaerbe.room.CartEntity
import com.example.tokopaerbe.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class PilihPembayaranAdapter :
    ListAdapter<Payment, PilihPembayaranAdapter.ListViewHolder>(CartEntityDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemMetodePembayaranBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val payment = getItem(position)
        holder.bind(payment)

        holder.binding.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        val adapter = MetodePembayaranAdapter()
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