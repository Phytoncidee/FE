package com.phytoncidee.hikinglog_fe.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val birth: String,
    val sex: String,
    val phone: String,
)
