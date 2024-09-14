package com.phytoncidee.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
data class RestaurantBookmarkGetResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: RestaurantBookmarkGetData
)

data class RestaurantBookmarkGetData(
    @SerializedName("bookmarkList") val bookmarkList: MutableList<RestaurantBookmarkGetBookmark>,
    @SerializedName("hasNext") val hasNext: Boolean
)

data class RestaurantBookmarkGetBookmark(
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("id") val id: Int,
    @SerializedName("storeId") val storeId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("location") val location: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("bookmarkType") val bookmarkType: String
)