package com.phytoncidee.hikinglog_fe.models

data class ProfileGetResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: Profile
)

// 프로필 데이터를 담는 클래스
data class Profile(
    val userid: Int,
    val email: String,
    val name: String,
    val birth: String,
    val sex: String,
    val phone: String,
    val image: String
)

