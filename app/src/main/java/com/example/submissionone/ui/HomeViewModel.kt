package com.example.submissionone.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionone.data.response.FinishedEventsResponse
import com.example.submissionone.data.response.ListFinishedEventsItem
import com.example.submissionone.data.response.ListUpcomingEventsItem
import com.example.submissionone.data.response.UpcomingEventResponse
import com.example.submissionone.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _upcomingEvents = MutableStateFlow<List<ListUpcomingEventsItem>>(emptyList())
    val upcomingEvents: StateFlow<List<ListUpcomingEventsItem>> get() = _upcomingEvents

    private val _finishedEvents = MutableStateFlow<List<ListFinishedEventsItem>>(emptyList())
    val finishedEvents: StateFlow<List<ListFinishedEventsItem>> get() = _finishedEvents

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchUpcomingEvents(active: Int = 1) {
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                val apiService = ApiConfig.getApiService()
                val response: UpcomingEventResponse = apiService.getUpcomingEvents(active)
                if (!response.error) {
                    _upcomingEvents.value = response.listEvents
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("HomeViewModel", "Error fetching Upcoming Events", e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun searchUpcomingEvents(query: String) {
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                val apiService = ApiConfig.getApiService()
                val response: UpcomingEventResponse = apiService.getUpcomingEvents(active = -1, query = query)
                if (!response.error) {
                    _upcomingEvents.value = response.listEvents
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("HomeViewModel", "Error searching Upcoming Events", e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun fetchFinishedEvents(active: Int = 0) {
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                val apiService = ApiConfig.getApiService()
                val response: FinishedEventsResponse = apiService.getFinishedEvents(active)
                if (!response.error) {
                    _finishedEvents.value = response.listEvents
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("HomeViewModel", "Error fetching Finished Events", e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun searchFinishedEvents(query: String) {
        viewModelScope.launch {
            _isLoading.emit(true)
            try {
                val apiService = ApiConfig.getApiService()
                val response: FinishedEventsResponse = apiService.getFinishedEvents(active = 0, query = query)
                if (!response.error) {
                    _finishedEvents.value = response.listEvents
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("HomeViewModel", "Error searching Finished Events", e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }
}