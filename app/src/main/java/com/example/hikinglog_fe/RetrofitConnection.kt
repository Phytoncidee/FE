package com.example.hikinglog_fe

import com.example.hikinglog_fe.interfaces.RetrofitApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// http://localhost:8080
// [Retrofit 객체 생성]
class RetrofitConnection {
    companion object{ //companion: 전역 변수 형태로 생각
        private const val BASE_URL = "http://10.0.2.2:8080"
        //private const val BASE_URL = "http://localhost:8080"

        // json 통신 위한 retrofit
        val jsonNetServ : RetrofitApiService
        val jsonRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        // NetworkService 초기화
        init{
            jsonNetServ = jsonRetrofit.create(RetrofitApiService::class.java)
        }
    }
}