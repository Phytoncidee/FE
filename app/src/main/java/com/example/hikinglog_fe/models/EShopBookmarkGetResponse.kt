package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class EShopBookmarkGetResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: EShopBookmarkGetData
)

data class EShopBookmarkGetData(
    @SerializedName("bookmarkList") val bookmarkList: MutableList<EShopBookmarkGetBookmark>,
    @SerializedName("hasNext") val hasNext: Boolean
)

data class EShopBookmarkGetBookmark(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("storeId") val storeId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("link") val link: String,
    @SerializedName("bookmarkType") val bookmarkType: String
)