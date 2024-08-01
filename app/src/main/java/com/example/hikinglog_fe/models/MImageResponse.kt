package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

data class MImageResponse(
    @SerializedName("response") val response: MIResponse
)

data class MIResponse(
    @SerializedName("header") val header: MIHeader,
    @SerializedName("body") val body: MIBody
)

data class MIHeader(
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultMsg") val resultMsg: String
)

data class MIBody(
    @SerializedName("items") val items: MIItems,
    @SerializedName("numOfRows") val numOfRows: Int,
    @SerializedName("pageNo") val pageNo: Int,
    @SerializedName("totalCount") val totalCount: Int
)

data class MIItems(
    @SerializedName("item") val itemList: MutableList<MIItem>
)

data class MIItem(
    @SerializedName("imgfilename") val imgfilename: String,
    @SerializedName("imgname") val imgname: String,
    @SerializedName("imgno") val imgno: Int
)

