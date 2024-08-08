package com.example.hikinglog_fe.models
data class PostWriteResponseDTO<T>(
    val code: String,
    val message: String,
    val data: T
)
