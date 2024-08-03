package com.example.hikinglog_fe.models

data class LoginResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: String
)
