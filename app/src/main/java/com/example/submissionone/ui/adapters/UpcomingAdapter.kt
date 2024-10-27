package com.example.submissionone.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionone.R
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.databinding.ItemUpcomingBinding
import com.example.submissionone.ui.DetailActivity

class UpcomingAdapter(private val upcomingList: List<ListUpcomingEventsItem>) :
    RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder>() {

    inner class UpcomingViewHolder(private val binding: ItemUpcomingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(upcomingEvent: ListUpcomingEventsItem) {
            Log.d("UpcomingAdapter", "Binding event: ${upcomingEvent.name}")

            binding.tvEventName.text = upcomingEvent.name
            binding.tvEventDate.text = upcomingEvent.beginTime

            Glide.with(binding.root.context)
                .load(upcomingEvent.mediaCover)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivEventCover)

            binding.root.setOnClickListener {
                Log.d("UpcomingAdapter", "Item clicked: ${upcomingEvent.name}")
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("eventType", "upcoming")
                intent.putExtra("event", upcomingEvent)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingViewHolder {
        val binding = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingViewHolder, position: Int) {
        holder.bind(upcomingList[position])
    }

    override fun getItemCount(): Int = upcomingList.size
}