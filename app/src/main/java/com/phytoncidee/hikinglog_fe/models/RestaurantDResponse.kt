package com.phytoncidee.hikinglog_fe.models

data class RestaurantDResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: RestaurantD
)

data class RestaurantD(
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
