package com.example.hikinglog_fe.models

import org.w3c.dom.Text
import java.io.File

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val birth: String,
    val sex: String,
    val phone: String,
)
