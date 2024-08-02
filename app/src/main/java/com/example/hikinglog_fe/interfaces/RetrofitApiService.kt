package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.AccommodationDResponse
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.CommunityPostLResponse
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EquipmentShopLResponse
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.MImageResponse
import com.example.hikinglog_fe.models.MSearchResponse
import com.example.hikinglog_fe.models.RestaurantDResponse
import com.example.hikinglog_fe.models.RestaurantLResponse
import com.example.hikinglog_fe.models.Top100Response
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

// http://localhost:8080/api/getTop100Mountains
interface RetrofitApiService {

    // [100대 명산]
    @GET("/api/getTop100Mountains")
    fun getTop100Mountains(
        @Header("Authorization") auth: String?
    ): Call<Top100Response>

    // [전체 산 검색]
    @GET("/api/getM/{mountain_Name}")
    fun getMountain(
        @Header("Authorization") auth: String?,
        @Path("mountain_Name") mName: String?
    ): Call<MSearchResponse>

    // [산 이미지]
    @GET("/api/getI/{mountain_Code}")
    fun getMountainImage(
        @Header("Authorization") auth: String?,
        @Path("mountain_Code") mCode: String?
    ): Call<MImageResponse>

    // [산 즐겨찾기 조회]
    @GET("/api/bookmarks/mountain")
    fun getMountainBookmark(
        @Header("Authorization") auth: String?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<MBookmarkGetResponse>

    // [등산용품점 목록 조회]
    @GET("/api/store/store-list")
    fun getEquipmentShopList(
        @Header("Authorization") auth: String?
    ): Call<EquipmentShopLResponse>

    // [등산용품점 즐겨찾기 조회]
    @GET("/api/bookmarks/onlinestore")
    fun getEShopBookmark(
        @Header("Authorization") auth: String?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<EShopBookmarkGetResponse>

    // [숙박시설 목록 조회]
    @GET("/api/store/stay-list")
    fun getAccommodationList(
        @Header("Authorization") auth: String?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?
    ): Call<AccommodationLResponse>

    // [숙박시설 상세 조회]
    @GET("/api/store/stay-detail")
    fun getAccommodationDetail(
        @Header("Authorization") auth: String?,
        @Query("contentId") contentId: Int?
    ): Call<AccommodationDResponse>

    // [음식점 목록 조회]
    @GET("/api/store/restaurant-list")
    fun getRestaurantList(
        @Header("Authorization") auth: String?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?
    ): Call<RestaurantLResponse>

    // [음식점 상세 조회]
    @GET("api/store/restaurant-detail")
    fun getRestaurantDetail(
        @Header("Authorization") auth: String?,
        @Query("contentId") contentId: Int?
    ): Call<RestaurantDResponse>

    // [커뮤니티 글 목록 조회]
    @GET("/api/boards")
    fun getPostList(
        @Header("Authorization") auth: String?,
        @Body raw: JsonObject
    ): Call<CommunityPostLResponse>

}