package com.phytoncidee.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class MBookmarkGetResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: MBookmarkGetData
)

data class MBookmarkGetData(
    @SerializedName("bookmarkList") val bookmarkList: MutableList<MBookmarkGetBookmark>,
    @SerializedName("hasNext") val hasNext: Boolean
)

data class MBookmarkGetBookmark(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("mntilistno") val mntilistno: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("location") val location: String,
    @SerializedName("mntiHigh") val mntiHigh: Double,
    @SerializedName("mntiInfo") val mntiInfo: String,
    @SerializedName("bookmarkType") val bookmarkType: String
)