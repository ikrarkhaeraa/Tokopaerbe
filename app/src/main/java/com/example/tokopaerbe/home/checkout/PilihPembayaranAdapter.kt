package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemCheckoutBinding
import com.example.tokopaerbe.databinding.ItemMetodePembayaranBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

//class PilihPembayaranAdapter(
//
//) : RecyclerView.Adapter<PilihPembayaranAdapter.ListViewHolder>() {
//
//    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
//        val binding = ItemMetodePembayaranBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
//        return ListViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int = listCheckoutProduct.size
//
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
//        val (productImage, productName, productVariant, productStock, productPrice, productQuantity) = listCheckoutProduct[position]
//        Picasso.get().load(productImage).into(holder.binding.itemImage)
//        holder.binding.itemTitle.text = productName
//        holder.binding.variant.text = productVariant
//    }
//
//    class ListViewHolder(var binding: ItemMetodePembayaranBinding) : RecyclerView.ViewHolder(binding.root)
//
//
//
//}