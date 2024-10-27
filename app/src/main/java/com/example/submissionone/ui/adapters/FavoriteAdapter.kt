package com.example.submissionone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionone.database.FavoriteEvent
import com.example.submissionone.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val events: List<FavoriteEvent>,
    private val onItemClick: (FavoriteEvent) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class FavoriteEventViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: FavoriteEvent) {
            binding.tvEventName.text = event.name
            binding.tvEventDate.text = event.name

            Glide.with(binding.root.context)
                .load(event.mediaCover)
                .into(binding.ivEventCover)

            binding.root.setOnClickListener {
                onItemClick(event)
            }
        }
    }
}
