package com.example.hikinglog_fe.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RestaurantLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<Restaurant>
)
@Parcelize
data class Restaurant(
    val name: String,
    val contentId: Int,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: Double,
    val mapY: Double,
    val tel: String
) : Parcelable
