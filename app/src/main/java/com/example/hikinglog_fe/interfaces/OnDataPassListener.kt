package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.Restaurant
import com.example.hikinglog_fe.models.TourSpot

interface OnDataPassListener {
    fun preRestaurantToActivity(restaurant: Restaurant)
    fun postRestaurantToActivity(restaurant: Restaurant)
    fun preTourspotToActivity(tourspot: TourSpot)
    fun postTourspotToActivity(tourspot: TourSpot)
}