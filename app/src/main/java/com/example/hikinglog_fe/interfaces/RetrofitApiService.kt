package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.Top100Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

// http://localhost:8080/api/getTop100Mountains
interface RetrofitApiService {

    // [100대 명산]
    //@Headers("Content-Type: application/json;charset=UTF-8")
    @GET("/api/getTop100Mountains")
    fun getTop100Mountains(
        @Header("Authorization") auth: String?
    ): Call<List<String>>

}