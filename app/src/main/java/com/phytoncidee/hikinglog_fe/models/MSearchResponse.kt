package com.phytoncidee.hikinglog_fe.models

import com.google.gson.annotations.SerializedName

data class MSearchResponse(
    @SerializedName("response") val response: MSResponse
)

data class MSResponse(
    @SerializedName("header") val header: MSHeader,
    @SerializedName("body") val body: MSBody
)

data class MSHeader(
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String
)

data class MSBody(
    @SerializedName("items") val items: MSItems,
    @SerializedName("numOfRows") val numOfRows: Int,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("totalCount") val totalCount: Int
)

data class MSItems(
    @SerializedName("item") val itemList: List<MSItem>
)

data class MSItem(
    @SerializedName("mntiadd") val mntiadd: String,
    @SerializedName("mntiadmin") val mntiadmin: String,
    @SerializedName("mntiadminnum") val mntiadminnum: String,
    @SerializedName("mntidetails") val mntidetails: String,
    @SerializedName("mntihigh") val mntihigh: String,
    @SerializedName("mntilistno") val mntilistno: String,
    @SerializedName("mntiname") val mntiname: String,
    @SerializedName("mntinfdt") val mntinfdt: String,
    @SerializedName("mntisname") val mntisname: String,
    @SerializedName("mntisummary") val mntisummary: String,
    @SerializedName("mntitop") val mntitop: String
)