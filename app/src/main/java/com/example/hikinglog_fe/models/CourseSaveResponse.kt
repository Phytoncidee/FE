package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class CourseSaveResponse(
    val tourName: String,
    val tourId: String,
    val preHikeAccomoId: String,
    val postHikeAccomoId: String,
    val preHikeRestaurantId: String,
    val postHikeRestaurantId: String
)