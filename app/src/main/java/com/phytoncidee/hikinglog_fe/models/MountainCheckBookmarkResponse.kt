package com.phytoncidee.hikinglog_fe.models

data class MountainCheckBookmarkResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MountainBookmarkData
)

data class MountainBookmarkData(
    val bookmarkList: MutableList<MountainBookmarkItem>,
    val hasNext: Boolean
)

data class MountainBookmarkItem(
    val createdAt: String,
    val updatedAt: String,
    val id: Int,
    val mntilistno: Long,
    val name: String,
    val image: String,
    val location: String,
    val mntiHigh: Double,
    val mntiInfo: String,
    val bookmarkType: String
)
