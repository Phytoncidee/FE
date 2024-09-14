package com.phytoncidee.hikinglog_fe.models

data class ProfileResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: ProfileData
)



