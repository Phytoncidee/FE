package com.phytoncidee.hikinglog_fe.models

data class WeatherResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: Weather
)

data class Weather(
    val temperature: Double,
    val rain: String,
    val wind: String,
    val dust: String
)
