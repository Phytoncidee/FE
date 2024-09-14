package com.phytoncidee.hikinglog_fe.models

data class DeleteRecordResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: String
)
