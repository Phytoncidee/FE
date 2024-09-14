package com.phytoncidee.hikinglog_fe.models

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

