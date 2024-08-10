package com.example.hikinglog_fe.models

data class NationalMountainsImageResponse(
    val response: ImageResponseDetails?
)

data class ImageResponseDetails(
    val header: Header?,
    val body: ImageBody?
)

data class ImageBody(
    val items: ImageItems?,
    val numOfRows: Int?,
    val pageNo: Int?,
    val totalCount: Int?
)

data class ImageItems(
    val item: List<ImageItem>?
)

data class ImageItem(
    val imgfilename: String?,
    val imgname: String?,
    val imgno: Int?
)

