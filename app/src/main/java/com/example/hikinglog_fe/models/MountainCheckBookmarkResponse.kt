package com.example.hikinglog_fe.models

data class MountainCheckBookmarkResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: BookmarkData
)

data class BookmarkData(
    val bookmarkList: List<BookmarkItem>,
    val hasNext: Boolean
)

data class BookmarkItem(
    val createdAt: String,
    val updatedAt: String,
    val id: Int,
    val mntilistno: Long,
    val image: String,
    val location: String,
    val mntiHigh: Long,
    val mntiInfo: String,
    val bookmarkType: String
)
