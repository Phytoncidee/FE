package com.example.hikinglog_fe.models

data class HikingRecordDetailResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: DetailRecord
)

data class DetailRecord(
    val rid: Int,
    val number: Int,
    val time: Int,
    val date: String,
    val mname: String
)
