package com.example.submissionone.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: SettingPreferences) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSettings(): LiveData<Boolean> {
        return pref.getNotificationSetting().asLiveData()
    }

    fun saveNotificationSetting(isActive: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isActive)
        }
    }
}
