package com.example.submissionone.data.retrofit

import com.example.submissionone.data.response.FinishedEventsResponse
import com.example.submissionone.data.response.UpcomingEventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getUpcomingEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String? = null
    ): UpcomingEventResponse

    @GET("events")
    suspend fun getFinishedEvents(
        @Query("active") active: Int = 0,
        @Query("q") query: String? = null
    ): FinishedEventsResponse
}
