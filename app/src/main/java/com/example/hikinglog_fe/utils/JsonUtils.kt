package com.example.hikinglog_fe.utils

import com.example.hikinglog_fe.models.Top100Response
import com.google.gson.Gson

// Json 변환 함수
fun parseEscapedJson(response: List<String>): List<Top100Response> {
    val gson = Gson()
    return response.map { gson.fromJson(it, Top100Response::class.java) }
}