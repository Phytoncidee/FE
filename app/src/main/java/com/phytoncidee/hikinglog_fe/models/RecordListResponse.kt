package com.phytoncidee.hikinglog_fe.models

data class RecordListResponse(
    val status: Int,
    val code: String,
    val message: String,
    val data: MutableList<HikingRecord>
)

data class HikingRecord(
    val rid: Int,       // 해당 등산 기록의 고유 ID (이걸로 등산 기록 상세 조회 및 삭세할 때 사용)
    val number: Int,    // 해당 기록이 해당 산을 몇번째로 등반했는지
    val time: Int,      // 등반 시간 (단위: 분)
    val date: String,   // 등반한 날짜
    val mname: String   // 등반한 산 이름
)