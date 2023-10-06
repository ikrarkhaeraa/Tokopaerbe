package com.example.tokopaerbe.home.store

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.databinding.ItemSearchRecommendationBinding

class SearchAdapter(
    private val listSearchResult: List<String>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.ListViewHolder>() {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
//        val view: View =
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_search_recommendation, parent, false)
//        return ListViewHolder(view)
//    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemSearchRecommendationBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
//        val title = listSearchResult[position]
//        holder.resultName.text = title
        val title = listSearchResult[position]
        holder.bind(title)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(title)
        }
    }

    override fun getItemCount(): Int = listSearchResult.size

//    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var resultName: TextView = itemView.findViewById(R.id.item_title)
//    }

    class ListViewHolder(var binding: ItemSearchRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: String) {
            binding.apply {
                itemTitle.text = data
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }
}
