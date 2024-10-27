package com.example.submissionone.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.submissionone.R
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.data.response.UpcomingEventResponse
import com.example.submissionone.ui.MainActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class EventNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        createNotificationChannel()

        val response = fetchNextEvent()
        response?.let { event ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                try {
                    showNotification(event.name, event.beginTime)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }

        val currentTimeMillis = System.currentTimeMillis()
        val maxDurationMillis = 6 * 60 * 60 * 1000L

        if (currentTimeMillis >= maxDurationMillis) {
            Log.d("EventNotificationWorker", "Maximum service duration reached. Stopping worker.")
            return Result.success()
        }
        return Result.retry()
    }


    @SuppressLint("MissingPermission")
    override fun onStopped() {
        Log.d("EventNotificationWorker", "Timeout reached or worker stopped.")

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notification = NotificationCompat.Builder(applicationContext, "event_channel")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Service Stopped")
            .setContentText("Event notification service has stopped due to timeout.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }


    private fun fetchNextEvent(): ListUpcomingEventsItem? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://event-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(EventService::class.java)
        val response = service.getUpcomingEvents(limit = 1).execute()

        return if (response.isSuccessful && response.body() != null) {
            response.body()?.listEvents?.firstOrNull()
        } else {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(eventName: String, eventDate: String) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, "event_channel")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Upcoming Event: $eventName")
            .setContentText("Scheduled on: $eventDate")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Scheduled on: $eventDate"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenIntent, true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(1, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        val name = "Event Notifications"
        val descriptionText = "Notifications for upcoming events"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("event_channel", name, importance).apply {
            description = descriptionText
        }

        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

interface EventService {
    @GET("events")
    fun getUpcomingEvents(@Query("active") active: Int = -1, @Query("limit") limit: Int): retrofit2.Call<UpcomingEventResponse>
}
