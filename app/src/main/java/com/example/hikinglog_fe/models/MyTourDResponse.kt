package com.example.hikinglog_fe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MyTourDResponse(
    val tourId: Int,
    val tourTitle: String,
    val mountainId: Int,
    val preHikeTour: MutableList<MyTourDAccommo>,
    val preHikeRestaurant: MutableList<MyTourDRestaurant>,
    val postHikeTour: MutableList<MyTourDAccommo>,
    val postHikeRestaurant: MutableList<MyTourDRestaurant>,
    val status: String
)

data class MyTourDAccommo(
    val sid: Int,
    val contentId: Int,
    val location: String,
    val phone: String,
    val sname: String,
    val simage: String
)

data class MyTourDRestaurant(
    val sid: Int,
    val contentId: Int,
    val location: String,
    val phone: String,
    val sname: String,
    val simage: String
)