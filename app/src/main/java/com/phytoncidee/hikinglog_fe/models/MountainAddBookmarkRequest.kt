package com.phytoncidee.hikinglog_fe.models

data class MountainAddBookmarkRequest(
    val name: String,
    val location: String,
    val info: String,
    val high: Double,
    val image: String
)
