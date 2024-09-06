package com.example.hikinglog_fe.models

// 메인 데이터 클래스
data class CourseSaveDTO(
    val tourTitle: String,
    val mountainId: Int,
    val preHikeAccomoIds: List<String>,
    val preHikeRestaurantIds: List<String>,
    val postHikeAccomoIds: List<String>,
    val postHikeRestaurantIds: List<String>,
    val accomoDetails: List<AccomoDetail>,
    val restaurantDetails: List<RestaurantDetail>
)

// Accommodation Detail 클래스
data class AccomoDetail(
    val name: String,
    val contentId: String,
    val add: String,
    val img: String,
    val img2: String,
    val mapX: String,
    val mapY: String,
    val tel: String,
    val intro: String
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

