package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.Restaurant
import com.example.hikinglog_fe.models.TourSpot

interface OnDataPassListener {
    // adapter -> activity 추가
    fun preRestaurantToActivity(preRestaurant: Restaurant)
    fun postRestaurantToActivity(postRestaurant: Restaurant)
    fun preTourspotToActivity(preTourspot: TourSpot)
    fun postTourspotToActivity(postTourspot: TourSpot)

    // adapter -> activity 취소
    fun CancelpreRestaurant(preRestaurant: Restaurant)
    fun CancelpostRestaurantToActivity(postRestaurant: Restaurant)
    fun CancelpreTourspotToActivity(preTourspot: TourSpot)
    fun CancelpostTourspotToActivity(postTourspot: TourSpot)
}