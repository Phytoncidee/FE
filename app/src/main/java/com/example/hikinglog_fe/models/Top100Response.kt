package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName

data class MountainItem(
    @SerializedName("mntiname") val mntiname: String,
    @SerializedName("mntihigh") val mntihigh: Double,
    @SerializedName("mntiadd") val mntiadd: String
)

data class MountainItems(
    @SerializedName("item") val item: List<MountainItem>
)

data class MountainBody(
    @SerializedName("items") val items: MountainItems
)

data class MountainResponse(
    @SerializedName("body") val body: MountainBody
)

data class Top100Response(
    @SerializedName("response") val response: MountainResponse
)

//data class Top100Response(
//    @SerializedName("mntiname") val mntiname: String,
//    @SerializedName("mntihigh") val mntihigh: String,
//    @SerializedName("mntiadd") val mntiadd: String,
//)
//data class myTop100Item(val mntiname : String, val mntihigh : String,val mntiadd : String)
//data class myTop100Items(val item : MutableList<myTop100Item>)
//data class myTop100Body(val items : myTop100Items)
//data class myTop100Respose(val body : myTop100Body)
//data class Top100Response(val response : myTop100Respose)