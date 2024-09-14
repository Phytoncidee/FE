package com.phytoncidee.hikinglog_fe.models

data class AccommodationLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<Accommodation>
)

data class Accommodation(
    val name: String,
    val contentId: Int,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: Double,
    val mapY: Double,
    val tel: String
)
