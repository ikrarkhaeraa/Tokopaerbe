package com.example.tokopaerbe.home.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.R
import com.example.tokopaerbe.retrofit.response.Review
import com.squareup.picasso.Picasso

class ReviewAdapter(
    private val listReview: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (userName, photo, ratingBar, review) = listReview[position]
        holder.userName.text = userName
        Picasso.get().load(photo).into(holder.userImage)
        holder.ratingBar.rating = ratingBar.toFloat()
        holder.review.text = review
    }

    override fun getItemCount(): Int = listReview.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.reviewerName)
        var userImage: ImageView = itemView.findViewById(R.id.item_image)
        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        var review: TextView = itemView.findViewById(R.id.review)
    }
}
