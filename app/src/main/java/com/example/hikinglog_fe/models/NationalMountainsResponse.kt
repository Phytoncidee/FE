package com.example.hikinglog_fe.models


data class NationalMountainsResponse(
    val response: Response?
)
data class Response(
    val header: Header?,
    val body: Body?
)
data class Header(
    val resultCode: String?,
    val resultMsg: String?
)
data class Body(
    val items: Items?
)

data class Items(
    val item: List<Mountain>?
)

data class Mountain(
    val mntiadd: String?,
    val mntiadmin: String?,
    val mntiadminnum: String?,
    val mntidetails: String?,
    val mntihigh: Double?,
    val mntilistno: Long,
    val mntiname: String?,
    val mntinfdt: Long?,
    val mntisname: String?,
    val mntisummary: String?,
    val mntitop: String?
)