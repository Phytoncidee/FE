package com.phytoncidee.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class CommentsGetResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: CommentsGetData
)

data class CommentsGetData(
    @SerializedName("commentList") val commentList: MutableList<Comment>,
    @SerializedName("hasNext") val hasNext: Boolean
)

data class Comment(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("userid") val userid: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("userImage") val userImage: String
)