package com.example.submissionone.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionone.R
import com.example.submissionone.data.response.ListFinishedEventsItem
import com.example.submissionone.databinding.ItemFinishedBinding
import com.example.submissionone.ui.DetailActivity

class FinishedAdapter(private val finishedList: List<ListFinishedEventsItem>) :
    RecyclerView.Adapter<FinishedAdapter.FinishedViewHolder>() {

    inner class FinishedViewHolder(private val binding: ItemFinishedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(finishedEvent: ListFinishedEventsItem) {
            Log.d("FinishedAdapter", "Binding event: ${finishedEvent.name}")
            binding.tvEventName.text = finishedEvent.name
            binding.tvEventDate.text = finishedEvent.beginTime

            Glide.with(binding.root.context)
                .load(finishedEvent.mediaCover)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivEventCover)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("eventType", "finished")
                intent.putExtra("event", finishedEvent)
                binding.root.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedViewHolder {
        val binding = ItemFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FinishedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FinishedViewHolder, position: Int) {
        holder.bind(finishedList[position])
    }

    override fun getItemCount(): Int = finishedList.size
}