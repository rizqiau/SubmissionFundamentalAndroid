package com.example.submissionone.ui

import android.app.ForegroundServiceStartNotAllowedException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.submissionone.R
import com.example.submissionone.databinding.FragmentSettingsBinding
import com.example.submissionone.preferences.SettingPreferences
import com.example.submissionone.preferences.SettingsViewModel
import com.example.submissionone.preferences.ViewModelFactory
import com.example.submissionone.preferences.dataStore
import com.example.submissionone.worker.EventNotificationWorker
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSettingsBinding.bind(view)

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory(pref)
        settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchDarkMode.setOnCheckedChangeListener(null)
            binding.switchDarkMode.isChecked = isDarkModeActive
            binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.saveThemeSetting(isChecked)
                AppCompatDelegate.setDefaultNightMode(
                    if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        settingsViewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationActive ->
            binding.switchNotification?.setOnCheckedChangeListener(null)
            binding.switchNotification?.isChecked = isNotificationActive
            binding.switchNotification?.setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.saveNotificationSetting(isChecked)
                if (isChecked) {
                    try {
                        checkAndStartNotificationWorker()
                    } catch (e: ForegroundServiceStartNotAllowedException) {
                        Log.e("SettingsFragment", "Foreground service start not allowed due to time limit.", e)
                        Toast.makeText(context, "Tidak dapat memulai layanan karena batas waktu telah habis", Toast.LENGTH_LONG).show()
                    }
                } else {
                    WorkManager.getInstance(requireContext()).cancelAllWorkByTag("event_notification_worker")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkAndStartNotificationWorker() {
        WorkManager.getInstance(requireContext()).getWorkInfosByTagLiveData("event_notification_worker")
            .observe(viewLifecycleOwner) { workInfoList ->
                val isWorkerActive = workInfoList.any { workInfo ->
                    workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
                }

                if (!isWorkerActive) {
                    scheduleNotificationWorker()
                }
            }
    }

    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<EventNotificationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("event_notification_worker")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "event_notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
