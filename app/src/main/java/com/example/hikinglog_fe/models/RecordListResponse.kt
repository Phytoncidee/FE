package com.example.hikinglog_fe.models

data class RecordListResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: List<HikingRecord>
)

data class HikingRecord(
    val number: Int,
    val time: Int,
    val date: String,
    val mname: String
)