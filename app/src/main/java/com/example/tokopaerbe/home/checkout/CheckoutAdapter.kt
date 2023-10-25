package com.example.tokopaerbe.home.checkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.R
import com.example.tokopaerbe.databinding.ItemCheckoutBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class CheckoutAdapter(
    private val listCheckoutProduct: List<CheckoutDataClass>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<CheckoutAdapter.ListViewHolder>() {

    private var totalToggleValue = 1
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding =
            ItemCheckoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listCheckoutProduct.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (productId, productImage, productName, productVariant, productStock, productPrice, productQuantity) = listCheckoutProduct[position]
        Picasso.get().load(productImage).placeholder(R.drawable.image_loading).into(holder.binding.itemImage)
        holder.binding.itemTitle.text = productName
        holder.binding.variant.text = productVariant
        holder.binding.toggleTotal.text = productQuantity.toString()

        if (productStock < 5) {
            holder.binding.stock.text = "Sisa $productStock"
            holder.binding.stock.setTextColor(holder.itemView.context.getColor(R.color.error))
        } else {
            holder.binding.stock.text = "Sisa $productStock"
        }

        val itemPrice = formatPrice(productPrice.toDouble())
        holder.binding.itemPrice.text = "Rp$itemPrice"

        val togglePlus = holder.binding.togglePlus
        val toggleMinus = holder.binding.toggleMinus
        val toggleTotal = holder.binding.toggleTotal

        toggleTotal.text = productQuantity.toString()

        togglePlus.setOnClickListener {
            val currentTotal = toggleTotal.text.toString().toInt()
            val newTotal = (currentTotal + 1).coerceAtMost(productStock)
            toggleTotal.text = newTotal.toString()

            totalToggleValue = newTotal

            listCheckoutProduct[position].productQuantity = totalToggleValue
            clickListener.onItemClick(position, listCheckoutProduct[position])
        }

        toggleMinus.setOnClickListener {
            val currentTotal = toggleTotal.text.toString().toInt()
            val newTotal = (currentTotal - 1).coerceAtLeast(1)
            toggleTotal.text = newTotal.toString()

            totalToggleValue = newTotal

            listCheckoutProduct[position].productQuantity = totalToggleValue
            clickListener.onItemClick(position, listCheckoutProduct[position])
        }
    }

    class ListViewHolder(var binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root)

    private fun formatPrice(price: Double): String {
        val numberFormat = NumberFormat.getNumberInstance(
            Locale(
                "id",
                "ID"
            )
        )
        return numberFormat.format(price)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: CheckoutDataClass)
    }
}
