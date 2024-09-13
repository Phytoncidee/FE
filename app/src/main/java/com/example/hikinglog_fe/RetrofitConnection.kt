package com.example.hikinglog_fe

import com.example.hikinglog_fe.interfaces.RetrofitApiService
import com.google.gson.GsonBuilder
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


// http://localhost:8080
// [Retrofit 객체 생성]
class RetrofitConnection {
    companion object{ //companion: 전역 변수 형태로 생각
        private const val BASE_URL = "http://3.39.65.172:8080/"    // http://localhost:8080/
        //private const val BASE_URL = "http://localhost:8080"

        //JsonReader.setLeninet(true) 오류 해결
        val gson = GsonBuilder()
            .setLenient()
            .create()

        // 크롤링으로 인한 시간 지연 해결(10초 -> 30초)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)  // 연결 타임아웃
            .readTimeout(100, TimeUnit.SECONDS)     // 읽기 타임아웃
            .writeTimeout(100, TimeUnit.SECONDS)    // 쓰기 타임아웃
            .build()

        // [json 통신 위한 retrofit]
        val jsonNetServ : RetrofitApiService
        val jsonRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)// 크롤링으로 인한 시간 지연 해결
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        // [xml 통신 위한 retrofit]
        val xmlNetServ : RetrofitApiService
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()

        val xmlRetrofit : Retrofit
            get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(TikXmlConverterFactory.create(parser)) //전달받은 문자열 Xml로 변환
                .build()

        // NetworkService 초기화
        init{
            jsonNetServ = jsonRetrofit.create(RetrofitApiService::class.java)
            xmlNetServ = xmlRetrofit.create(RetrofitApiService::class.java)
        }
    }
}