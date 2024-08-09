package com.example.hikinglog_fe.models

data class RegisterErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val code: String,
    val message: String
)
