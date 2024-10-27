package com.example.submissionone.ui

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.submissionone.R
import com.example.submissionone.databinding.ActivityMainBinding
import com.example.submissionone.preferences.SettingPreferences
import com.example.submissionone.preferences.SettingsViewModel
import com.example.submissionone.preferences.ViewModelFactory
import com.example.submissionone.preferences.dataStore
import com.example.submissionone.worker.EventNotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object {
        const val REQUEST_CODE_POST_NOTIFICATIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val pref = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingsViewModel = ViewModelProvider(this, ViewModelFactory(pref))[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        settingsViewModel.getNotificationSettings().observe(this) { isNotificationEnabled ->
            if (isNotificationEnabled) {
                try {
                    startNotificationWorker()
                } catch (e: ForegroundServiceStartNotAllowedException) {
                    Log.e("MainActivity", "Foreground service start not allowed due to time limit.", e)
                }
            } else {
                WorkManager.getInstance(this).cancelUniqueWork("EventNotificationWorker")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun startNotificationWorker() {
        try {
            val workRequest = PeriodicWorkRequestBuilder<EventNotificationWorker>(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "EventNotificationWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        } catch (e: ForegroundServiceStartNotAllowedException) {
            Log.e("MainActivity", "Foreground service start not allowed due to time limit.", e)
            Toast.makeText(this, "Tidak dapat memulai layanan karena batas waktu telah habis", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin notifikasi diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
