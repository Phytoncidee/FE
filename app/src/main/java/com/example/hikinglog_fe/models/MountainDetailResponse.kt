package com.example.hikinglog_fe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MountainDetailResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MountainDetail
)

data class MountainDetail(
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