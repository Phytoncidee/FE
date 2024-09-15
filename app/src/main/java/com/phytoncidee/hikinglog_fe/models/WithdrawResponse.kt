package com.phytoncidee.hikinglog_fe.models

data class WithdrawResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: String
)
