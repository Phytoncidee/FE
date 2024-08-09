package com.example.hikinglog_fe.models

import android.provider.ContactsContract.CommonDataKinds.Email

data class LoginRequest(
    val email: String,
    val password: String
)
