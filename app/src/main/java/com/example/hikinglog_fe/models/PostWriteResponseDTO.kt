package com.example.hikinglog_fe.models
data class PostWriteResponseDTO<T>(
    val status: Int,
    val code: String,
    val message: String,
    val data: String
)
