package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName

data class AccommodationDResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: AccommodationD
)

data class AccommodationD(
    val name: String,
    val contentId: Int,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: Double,
    val mapY: Double,
    val tel: String,
    val intro: String
)
