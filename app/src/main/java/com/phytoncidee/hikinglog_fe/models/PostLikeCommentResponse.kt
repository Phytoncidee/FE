package com.phytoncidee.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class PostLikeCommentResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String
)