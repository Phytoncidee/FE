package com.phytoncidee.hikinglog_fe.models

data class TrailResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: List<List<Double>>
)
