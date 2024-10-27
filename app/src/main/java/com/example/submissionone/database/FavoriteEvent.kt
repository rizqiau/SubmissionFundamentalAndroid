package com.example.submissionone.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,
    var ownerName: String = "",
    var beginTime: String = "",
    var cityName: String = "",
    var quota: Int = 0,
    var registrants: Int = 0,
    var description: String = "",
    var link: String = ""
) : Parcelable