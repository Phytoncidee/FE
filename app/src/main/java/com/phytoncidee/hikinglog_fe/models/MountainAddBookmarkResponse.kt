package com.phytoncidee.hikinglog_fe.models

data class MountainAddBookmarkResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: String
)

data class MountainAddBookmarkErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val code: String,
    val message: String
)