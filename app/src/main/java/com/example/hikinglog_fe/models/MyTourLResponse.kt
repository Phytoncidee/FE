package com.example.hikinglog_fe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MyTourLResponse(
    val tourId: Int,
    val tourTitle: String,
    val mountainId: Int,
    val preHikeAccomoIds: List<String>,
    val preHikeRestaurantIds: List<String>,
    val postHikeAccomoIds: List<String>,
    val postHikeRestaurantIds: List<String>,
    val userId: Int?,
    val status: String?
)
