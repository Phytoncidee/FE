package com.phytoncidee.hikinglog_fe.models

data class GetRegionTop100Response(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<RegionTop100Mountains>
)

data class RegionTop100Mountains(
    val lot: Double,
    val frtrlId: String,
    val ctpvNm: String,
    val crtrDt: String,
    val aslAltide: Double,
    val addrNm: String,
    val frtrlNm: String,
    val mtnCd: String,
    val lat: Double
)