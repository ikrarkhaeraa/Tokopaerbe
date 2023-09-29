package com.example.tokopaerbe.home.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tokopaerbe.R

class SearchAdapter(
    private val listSearchResult: List<String>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<SearchAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_recommendation, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val title = listSearchResult[position]
        holder.resultName.text = title

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(title)
        }
    }

    override fun getItemCount(): Int = listSearchResult.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resultName: TextView = itemView.findViewById(R.id.item_title)
    }

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }
}
