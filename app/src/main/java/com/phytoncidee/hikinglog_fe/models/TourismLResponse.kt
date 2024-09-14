package com.phytoncidee.hikinglog_fe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class TourismLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<TourSpot>
)
@Parcelize
data class TourSpot(
    val name: String,
    val contentId: Int,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: Double,
    val mapY: Double,
    val tel: String
) : Parcelable
