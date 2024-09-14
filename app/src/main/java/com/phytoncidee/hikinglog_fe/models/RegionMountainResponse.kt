package com.phytoncidee.hikinglog_fe.models

data class RegionMountainResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<MountainsByRegion>
)

data class MountainsByRegion(
    val mntiadminnum: String,
    val mntihigh: Double,      // 산의 높이
    val mntiadmin: String,
    val mntisummary: String,
    val mntinfdt: String,
    val mntisname: String,
    val mntidetails: String,   // 산 상세 정보
    val mntilistno: String,      // 산 목록 번호
    val mntiadd: String,       // 산의 주소
    val mntiname: String,      // 산의 이름
    val mntitop: String
)