package com.phytoncidee.hikinglog_fe.models

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
