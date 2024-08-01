package com.example.hikinglog_fe.models

import com.google.gson.annotations.SerializedName
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name="response")
data class Top100Response(
    @Element
    val body : Top100Body
)

@Xml(name="body")
data class Top100Body(
    @Element
    val items : Top100Items
)

@Xml(name="items")
data class Top100Items( //안에 item 여러 개 -> mutableList
    @Element
    val item : MutableList<Top100Item>
)

@Xml(name="item")
data class Top100Item(
    @PropertyElement
    val frtrlNm : String?,
    @PropertyElement
    val mtnCd : String?,
    @PropertyElement
    val addrNm : String?,
    @PropertyElement
    val aslAltide : String?
) { //마지막 단계.. constructor로 생성
    constructor() : this(null, null, null, null)
}

//data class Top100Response(
//    val response: MountainResponse
//)
//
//data class MountainResponse(
//    @SerializedName("header") val header: ResponseHeader,
//    @SerializedName("body") val body: ResponseBody
//)
//
//data class ResponseHeader(
//    @SerializedName("resultCode") val resultCode: String,
//    @SerializedName("resultMsg") val resultMsg: String
//)
//
//data class ResponseBody(
//    @SerializedName("items") val items: ResponseItems,
//    @SerializedName("numOfRows") val numOfRows: Int,
//    @SerializedName("pageNo") val pageNo: Int,
//    @SerializedName("totalCount") val totalCount: Int
//)
//
//data class ResponseItems(
//    @SerializedName("item") val itemList: List<MountainItem>
//)
//
//data class MountainItem(
//    @SerializedName("mntiname") val mntiname: String,
//    @SerializedName("mntihigh") val mntihigh: String,
//    @SerializedName("mntiadd") val mntiadd: String,
//    @SerializedName("mntilistno") val mntilistno: String,
//    @SerializedName("mntiadmin") val mntiadmin: String,
//    @SerializedName("mntiadminnum") val mntiadminnum: String,
//    @SerializedName("mntidetails") val mntidetails: String,
//    @SerializedName("mntinfdt") val mntinfdt: String,
//    @SerializedName("mntisname") val mntisname: String,
//    @SerializedName("mntisummary") val mntisummary: String,
//    @SerializedName("mntitop") val mntitop: String
//)

//data class Top100Response(
//    val response: List<MountainResponse>
//)
//data class MountainResponse(
//    @SerializedName("response") val response: MyTop100Response
//)
//
//data class MyTop100Response(
//    @SerializedName("body") val body: MyTop100Body
//)
//
//data class MyTop100Body(
//    @SerializedName("items") val items: MyTop100Items
//)
//
//data class MyTop100Items(
//    @SerializedName("item") val items: MutableList<MyTop100Item>
//)
//
//data class MyTop100Item(
//    @SerializedName("mntiname") val mntiname: String,
//    @SerializedName("mntihigh") val mntihigh: String,
//    @SerializedName("mntiadd") val mntiadd: String,
//    @SerializedName("mntilistno") val mntilistno: String
//)




//data class MountainItem(
//    @SerializedName("mntiname") val mntiname: String,
//    @SerializedName("mntihigh") val mntihigh: String,
//    @SerializedName("mntiadd") val mntiadd: String,
//    @SerializedName("mntilistno") val mntilistno: String
//)
//
//data class MountainItems(
//    @SerializedName("item") val item: List<MountainItem>
//)
//
//data class MountainBody(
//    @SerializedName("items") val items: MountainItems
//)
//
//data class MountainResponse(
//    @SerializedName("body") val body: MountainBody
//)
//
//data class Top100Response(
//    @SerializedName("response") val response: MountainResponse
//)

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