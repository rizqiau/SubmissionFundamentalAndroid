package com.example.submissionone.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionone.R
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.databinding.ItemUpcomingHomeBinding

class UpcomingHomeAdapter(
    private val eventList: List<ListUpcomingEventsItem>,
    private val onItemClick: (ListUpcomingEventsItem) -> Unit
) : RecyclerView.Adapter<UpcomingHomeAdapter.UpcomingViewHolder>() {

    inner class UpcomingViewHolder(val binding: ItemUpcomingHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingViewHolder {
        val binding = ItemUpcomingHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingViewHolder, position: Int) {
        val event = eventList[position]
        holder.binding.tvEventName.text = event.name
        Glide.with(holder.itemView.context)
            .load(event.mediaCover)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.binding.ivEventCover)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}

