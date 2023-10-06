package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.core.retrofit.response.Review
import com.example.tokopaerbe.databinding.ItemReviewBinding

class ReviewAdapter(
    private val listReview: List<Review>
) : RecyclerView.Adapter<ReviewAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
//        val (userName, photo, ratingBar, review) = listReview[position]
//        holder.userName.text = userName
//        Picasso.get().load(photo).into(holder.userImage)
//        holder.ratingBar.rating = ratingBar.toFloat()
//        holder.review.text = review
        val review = listReview[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = listReview.size

//    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var userName: TextView = itemView.findViewById(R.id.reviewerName)
//        var userImage: ImageView = itemView.findViewById(R.id.item_image)
//        var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
//        var review: TextView = itemView.findViewById(R.id.review)
//    }

    class ListViewHolder(var binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: Review) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.userImage)
                    .into(itemImage)
                reviewerName.text = data.userName
                ratingBar.rating = data.userRating.toFloat()
                review.text = data.userReview
            }
        }
    }
}
