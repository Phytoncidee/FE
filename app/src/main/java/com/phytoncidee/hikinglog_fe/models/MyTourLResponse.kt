package com.phytoncidee.hikinglog_fe.models

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
