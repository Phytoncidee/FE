package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class PostLikeResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String
)