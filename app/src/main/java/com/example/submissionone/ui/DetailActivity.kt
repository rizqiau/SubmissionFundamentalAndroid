package com.example.submissionone.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.submissionone.R
import com.example.submissionone.data.response.ListFinishedEventsItem
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.database.FavoriteEvent
import com.example.submissionone.database.FavoriteRoomDatabase
import com.example.submissionone.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var isFavourite = false
    private lateinit var eventId: String
    private lateinit var event: FavoriteEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventType = intent.getStringExtra("eventType")

        when (eventType) {
            "finished" -> {
                val finishedEvent: ListFinishedEventsItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("event", ListFinishedEventsItem::class.java)
                } else {
                    intent.getParcelableExtra("event")
                }
                finishedEvent?.let {
                    eventId = it.id.toString()
                    event = FavoriteEvent(
                        id = eventId,
                        name = it.name,
                        ownerName = it.ownerName,
                        beginTime = it.beginTime,
                        cityName = it.cityName,
                        quota = it.quota,
                        registrants = it.registrants,
                        description = it.description,
                        mediaCover = it.mediaCover,
                        link = it.link
                    )
                    displayEventDetails(it.name, it.ownerName, it.beginTime, it.cityName, it.quota - it.registrants, it.description, it.mediaCover, it.link)
                    checkIfFavorite(event)
                }
            }
            "upcoming" -> {
                val upcomingEvent: ListUpcomingEventsItem? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("event", ListUpcomingEventsItem::class.java)
                } else {
                    intent.getParcelableExtra("event")
                }
                upcomingEvent?.let {
                    eventId = it.id.toString()
                    event = FavoriteEvent(
                        id = eventId,
                        name = it.name,
                        ownerName = it.ownerName,
                        beginTime = it.beginTime,
                        cityName = it.cityName,
                        quota = it.quota,
                        registrants = it.registrants,
                        description = it.description,
                        mediaCover = it.mediaCover,
                        link = it.link
                    )
                    displayEventDetails(it.name, it.ownerName, it.beginTime, it.cityName, it.quota - it.registrants, it.description, it.mediaCover, it.link)
                    checkIfFavorite(event)
                }
            }
            "favorite" -> {
                val favoriteEvent: FavoriteEvent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("event", FavoriteEvent::class.java)
                } else {
                    intent.getParcelableExtra("event")
                }
                favoriteEvent?.let {
                    eventId = it.id
                    event = it
                    displayEventDetails(
                        it.name,
                        it.ownerName,
                        it.beginTime,
                        it.cityName,
                        it.quota - it.registrants,
                        it.description,
                        it.mediaCover ?: "",
                        it.link
                    )
                    checkIfFavorite(it)
                }
            }
        }

        binding.btnFavourite.setOnClickListener {
            toggleFavorite(event)
        }
    }

    private fun toggleFavorite(event: FavoriteEvent) {
        isFavourite = !isFavourite
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (isFavourite) {
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                binding.btnFavourite.setImageResource(R.drawable.ic_favorite_light)
            } else {
                binding.btnFavourite.setImageResource(R.drawable.ic_favorite_dark)
            }
            saveToFavorites(event)
        } else {
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                binding.btnFavourite.setImageResource(R.drawable.ic_favorite_border_light)
            } else {
                binding.btnFavourite.setImageResource(R.drawable.ic_favorite_border_dark)
            }
            removeFromFavorites(event)
        }
    }

    private fun saveToFavorites(event: FavoriteEvent) {
        val db = FavoriteRoomDatabase.getDatabase(this)
        val dao = db.favoriteEventDao()
        lifecycleScope.launch {
            dao.insertFavorite(event)
        }
    }

    private fun removeFromFavorites(event: FavoriteEvent) {
        val db = FavoriteRoomDatabase.getDatabase(this)
        val dao = db.favoriteEventDao()
        lifecycleScope.launch {
            dao.deleteFavorite(event)
        }
    }

    private fun checkIfFavorite(event: FavoriteEvent) {
        val db = FavoriteRoomDatabase.getDatabase(this)
        val dao = db.favoriteEventDao()
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        lifecycleScope.launch {
            val favorite = dao.getFavoriteById(event.id)
            if (favorite != null) {
                isFavourite = true
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    binding.btnFavourite.setImageResource(R.drawable.ic_favorite_light)
                } else {
                    binding.btnFavourite.setImageResource(R.drawable.ic_favorite_dark)
                }
            } else {
                isFavourite = false
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    binding.btnFavourite.setImageResource(R.drawable.ic_favorite_border_light)
                } else {
                    binding.btnFavourite.setImageResource(R.drawable.ic_favorite_border_dark)
                }
            }
        }
    }

    private fun displayEventDetails(
        name: String,
        ownerName: String,
        beginTime: String,
        cityName: String,
        quota: Int,
        description: String,
        imageUrl: String,
        link: String
    ) {
        val dateTime = beginTime.split(" ")
        val date = if (dateTime.isNotEmpty()) dateTime[0] else ""
        val timeWithoutSeconds = if (dateTime.size > 1) dateTime[1].substring(0, 5) else ""

        binding.tvEventName.text = name
        binding.tvOwnerName.text = ownerName
        binding.tvEventCity.text = cityName
        binding.tvBeginTime.text = getString(R.string.event_time, date, timeWithoutSeconds)
        binding.tvQuota.text = getString(R.string.event_quota, quota)
        binding.tvDescription.text = Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
        binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivEventCover)

        binding.btnOpenLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }
    }
}
