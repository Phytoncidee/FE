package com.example.hikinglog_fe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MyTourDResponse(
    val tourId: Int,
    val tourTitle: String,
    val mountainId: Int,
    val preHikeAccomo: List<MyTourDAccommo>,
    val preHikeRestaurant: List<MyTourDRestaurant>,
    val postHikeAccomo: List<MyTourDAccommo>,
    val postHikeRestaurant: List<MyTourDRestaurant>,
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