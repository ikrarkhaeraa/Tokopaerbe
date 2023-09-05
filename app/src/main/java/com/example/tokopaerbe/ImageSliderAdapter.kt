package com.example.tokopaerbe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding1Fragment
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding2Fragment
import com.example.tokopaerbe.prelogin.onboarding.OnBoarding3Fragment
import com.squareup.picasso.Picasso

class ImageSliderAdapter(
    private val listImage: List<String>
) : RecyclerView.Adapter<ImageSliderAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_item, parent, false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val photo = listImage[position]
        Picasso.get().load(photo).into(holder.productImage)
    }

    override fun getItemCount(): Int = listImage.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.productImage)
    }


}