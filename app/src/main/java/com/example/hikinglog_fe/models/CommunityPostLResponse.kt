package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName

data class CommunityPostLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: PostData
)

data class PostData(
    val boardList: MutableList<CommunityPost>,
    val hasNext: Boolean
)

data class CommunityPost(
    val createdAt: String,
    val updatedAt: String,
    val id: Int,
    val content: String,
    val image: String,
    val tag: String,
    val likeNum: Int,
    val liked: Boolean,
    val commentNum: Int,
    val userid: String,
)
