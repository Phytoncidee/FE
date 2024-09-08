package com.example.hikinglog_fe.models

// 메인 데이터 클래스
data class CourseSaveDTO(
    val userId: String,
    val tourTitle: String,
    val mountainId: Int,
    val preHikeAccomoIds: Array<String>,
    val preHikeRestaurantIds: Array<String>,
    val postHikeAccomoIds: Array<String>,
    val postHikeRestaurantIds: Array<String>,
    val tourspotDetail: Array<TourspotDetail>,
    val restaurantDetails: Array<RestaurantDetail>
)

// Tourspot Detail 클래스
data class TourspotDetail(
    val name: String,
    val contentId: String,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: String,
    val mapY: String,
    val tel: String
)

// Restaurant Detail 클래스
data class RestaurantDetail(
    val name: String,
    val contentId: String,
    val add: String,
    val img: String,
    val mapX: String,
    val mapY: String,
    val tel: String,
    val intro: String
)

