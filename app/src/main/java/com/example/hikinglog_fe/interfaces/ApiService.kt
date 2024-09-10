package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.DeleteRecordResponse
import com.example.hikinglog_fe.models.DirectRecordRequest
import com.example.hikinglog_fe.models.DirectRecordResponse
import com.example.hikinglog_fe.models.HikingRecordDetailResponse
import com.example.hikinglog_fe.models.LoginRequest
import com.example.hikinglog_fe.models.LoginResponse
import com.example.hikinglog_fe.models.MountainAddBookmarkRequest
import com.example.hikinglog_fe.models.MountainAddBookmarkResponse
import com.example.hikinglog_fe.models.MountainCheckBookmarkResponse
import com.example.hikinglog_fe.models.MountainDeleteBookmarkResponse
import com.example.hikinglog_fe.models.MountainDetailResponse
import com.example.hikinglog_fe.models.NationalMountainsImageResponse
import com.example.hikinglog_fe.models.NationalMountainsResponse
import com.example.hikinglog_fe.models.ProfileResponse
import com.example.hikinglog_fe.models.RecordListResponse
import com.example.hikinglog_fe.models.RegionMountainResponse
import com.example.hikinglog_fe.models.RegisterRequest
import com.example.hikinglog_fe.models.RegisterResponse
import com.example.hikinglog_fe.models.TrailResponse
import com.example.hikinglog_fe.models.WeatherResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ApiService {
    @POST("api/member/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/api/member/join")
    fun registerUser(
        @Body request: RegisterRequest,
    ): Call<RegisterResponse>

    @GET("/api/member/profile")
    fun getProfile(
        @Header("Authorization") token: String
    ): Call<ProfileResponse>

    @GET("/api/getM")
    fun getMountain(
        @Header("Authorization") token: String
    ): Call<NationalMountainsResponse>

    @GET("/api/getI/{mountain_Code}")
    fun getMountainImage(
        @Header("Authorization") token: String,
        @Path("mountain_Code") mountainCode: Long
    ): Call<NationalMountainsImageResponse>

    @GET("/api/getM/{mountain_Name}")
    fun getMountainInfo(
        @Header("Authorization") token: String,
        @Path("mountain_Name") mountainName: String
    ): Call<NationalMountainsResponse>

    @GET("/api/mountain/detail")
    fun getMountainDetail(
        @Header("Authorization") token: String,
        @Query("name") name: String?,
        @Query("number") number: String?
    ): Call<MountainDetailResponse>

    // 등산기록 저장
    @POST("api/record/hiking")
    fun recordHiking(
        @Header("Authorization") token: String,
        @Body request: DirectRecordRequest
    ): Call<DirectRecordResponse>

    // 등산기록 목록
    @GET("/api/record/list")
    fun getHikingRecords(
        @Header("Authorization") token: String
    ): Call<RecordListResponse>

    // 등산기록 삭제
    @DELETE("api/record/delete")
    fun deleteHikingRecords(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<DeleteRecordResponse>

    // 등산기록 상세
    @GET("/api/record/detail")
    fun getDetailRecord(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<HikingRecordDetailResponse>

    // 산 즐겨찾기 조회
    @GET("/api/bookmarks/mountain")
    fun getMtnBookmark(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Call<MountainCheckBookmarkResponse>

    // 산 즐겨찾기 등록
    @POST("/api/bookmarks/mountain/{mountainId}")
    fun addMtnBookmark(
        @Header("Authorization") token: String,
        @Path("mountainId") mountainId: Long,
        @Body request: MountainAddBookmarkRequest
    ): Call<MountainAddBookmarkResponse>

    @DELETE("/api/bookmarks/mountain/{mountainId}")
    fun deleteMtnBookmark(
        @Header("Authorization") token: String,
        @Path("mountainId") mountainId: Long
    ): Call<MountainDeleteBookmarkResponse>

    @GET("/api/getTrail")
    fun getTrail(
        @Header("Authorization") token: String,
        @Query("mntiname") mntiname: String,
        @Query("mntiadd") mntiadd: String
    ): Call<TrailResponse>

    // 날씨
    @GET("api/weather")
    fun getWeather(
        @Header("Authorization") token: String,
        @Query("address") address: String
    ): Call<WeatherResponse>

    // 전체 산 지역별 목록
    @GET("/api/getMByRegion/{region_array_index}")
    fun getMtnByRegion(
        @Header("Authorization") token: String,
        @Path("region_array_index") region_array_index: Int
    ): Call<RegionMountainResponse>

    companion object {
        private const val BASE_URL = "http://192.168.0.25:8080/"    // http://localhost:8080/
        val gson : Gson =   GsonBuilder().setLenient().create()

        private val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(100, TimeUnit.SECONDS)    // 읽기 타임아웃
            .writeTimeout(100, TimeUnit.SECONDS)   // 쓰기 타임아웃
            .build()

        fun create() : ApiService {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService::class.java)
        }
    }
}