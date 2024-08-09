package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName

data class EquipmentShopLResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<EquipmentShop>
)

data class EquipmentShop(
    val image: String,
    val name: String,
    val link: String,
    val id: Int
)
