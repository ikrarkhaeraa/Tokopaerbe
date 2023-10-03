package com.example.tokopaerbe.home.notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokopaerbe.R
import com.example.tokopaerbe.core.room.NotificationsEntity
import com.example.tokopaerbe.databinding.ItemNotificationsBinding
import com.google.android.material.color.MaterialColors

class NotificationAdapter(
//    val model: ViewModel
    private val onNotificationsClick: (NotificationsEntity) -> Unit
) : ListAdapter<NotificationsEntity, NotificationAdapter.ListViewHolder>(
    NotificationsEntityDiffCallback()
) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemNotificationsBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val notifEntity = getItem(position)
        holder.bind(notifEntity)

        holder.itemView.setOnClickListener {
//            model.notifIsChecked(notifEntity.notifId, true)
            onNotificationsClick(notifEntity)
        }
    }

    class ListViewHolder(var binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(data: NotificationsEntity) {
            binding.apply {
                Glide.with(itemView.context).load(data.notifImage).into(itemImage)
                itemTitle.text = data.notifType
                transactionSuccess.text = data.notifTitle
                body.text = data.notifBody
                timedate.text = "${data.notifDate}, ${data.notifTime}"

                if (!data.isChecked) {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.bgSelesai
                        )
                    )
                } else {
                    cardView.setCardBackgroundColor(
                        MaterialColors.getColor(
                            itemView, android.R.attr.windowBackground)
                    )
                }
            }
        }
    }

    private class NotificationsEntityDiffCallback : DiffUtil.ItemCallback<NotificationsEntity>() {
        override fun areItemsTheSame(
            oldItem: NotificationsEntity, newItem: NotificationsEntity
        ): Boolean {
            return oldItem.notifId == newItem.notifId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: NotificationsEntity, newItem: NotificationsEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
