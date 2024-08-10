package com.example.hikinglog_fe.models

data class TourismLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<TourSpot>
)

data class TourSpot(
    val name: String,
    val contentId: Int,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: Double,
    val mapY: Double,
    val tel: String
)
