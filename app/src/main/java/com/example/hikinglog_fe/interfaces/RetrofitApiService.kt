package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.AccommodationBookmarkDeleteResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkGetResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkPostResponse
import com.example.hikinglog_fe.models.AccommodationDResponse
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.CommentWriteDTO
import com.example.hikinglog_fe.models.CommentsGetResponse
import com.example.hikinglog_fe.models.CommunityPostLResponse
import com.example.hikinglog_fe.models.EShopBookmarkDeleteResponse
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EShopBookmarkPostResponse
import com.example.hikinglog_fe.models.EquipmentShopLResponse
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.MImageResponse
import com.example.hikinglog_fe.models.NationalMountainsResponse
import com.example.hikinglog_fe.models.PostAccommodationBMDTO
import com.example.hikinglog_fe.models.PostEShopBMDTO
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import com.example.hikinglog_fe.models.PostRestaurantBMDTO
import com.example.hikinglog_fe.models.PostWriteDTO
import com.example.hikinglog_fe.models.PostWriteResponseDTO
import com.example.hikinglog_fe.models.RestaurantBookmarkDeleteResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkGetResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkPostResponse
import com.example.hikinglog_fe.models.RestaurantDResponse
import com.example.hikinglog_fe.models.RestaurantLResponse
import com.example.hikinglog_fe.models.Top100Response
import com.example.hikinglog_fe.models.TourismLResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    ): Call<NationalMountainsResponse>

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

    // [등산용품점 즐겨찾기 등록]
    @POST("/api/bookmarks/onlinestore/{storeId}")
    fun postEShopBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?,
        @Body postEShopBMBody: PostEShopBMDTO
    ): Call<EShopBookmarkPostResponse>

    // [등산용품점 즐겨찾기 삭제]
    @DELETE("/api/bookmarks/onlinestore/{storeId}")
    fun deleteEShopBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?
    ): Call<EShopBookmarkDeleteResponse>


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

    // [숙박시설 즐겨찾기 조회]
    @GET("/api/bookmarks/accommodation")
    fun getAccommodationBookmark(
        @Header("Authorization") auth: String?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<AccommodationBookmarkGetResponse>

    // [숙박시설 즐겨찾기 등록]
    @POST("/api/bookmarks/accommodation/{storeId}")
    fun postAccommodationBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?,
        @Body postAccommodationBMBody: PostAccommodationBMDTO
    ): Call<AccommodationBookmarkPostResponse>

    // [숙박시설 즐겨찾기 삭제]
    @DELETE("/api/bookmarks/store/{storeId}")
    fun deleteAccommodationBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?
    ): Call<AccommodationBookmarkDeleteResponse>

    // [숙박시설 검색]
    @GET("api/store/search-stay?")
    fun searchStay(
        @Header("Authorization") auth: String?,
        @Query("keyword") keyword: String?
    ): Call<AccommodationLResponse>


    // [음식점 목록 조회]
    @GET("/api/store/restaurant-list")
    fun getRestaurantList(
        @Header("Authorization") auth: String?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?
    ): Call<RestaurantLResponse>

    // [음식점 상세 조회]
    @GET("/api/store/restaurant-detail")
    fun getRestaurantDetail(
        @Header("Authorization") auth: String?,
        @Query("contentId") contentId: Int?
    ): Call<RestaurantDResponse>

    // [음식점 즐겨찾기 조회]
    @GET("/api/bookmarks/restaurant")
    fun getRestaurantBookmark(
        @Header("Authorization") auth: String?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<RestaurantBookmarkGetResponse>

    // [음식점 즐겨찾기 등록]
    @POST("/api/bookmarks/restaurant/{storeId}")
    fun postRestaurantBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?,
        @Body postRestaurantBMBody: PostRestaurantBMDTO
    ): Call<RestaurantBookmarkPostResponse>

    // [음식점 즐겨찾기 삭제]
    @DELETE("/api/bookmarks/store/{storeId}")
    fun deleteRestaurantBookmark(
        @Header("Authorization") auth: String?,
        @Path("storeId") storeId: Int?
    ): Call<RestaurantBookmarkDeleteResponse>

    // [음식점 검색]
    @GET("api/store/search-restaurant?")
    fun searchRestaurant(
        @Header("Authorization") auth: String?,
        @Query("keyword") keyword: String?
    ): Call<RestaurantLResponse>


    // [관광지 목록 조회]
    @GET("/api/store/tour-list")
    fun getTourpList(
        @Header("Authorization") auth: String?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?
    ): Call<TourismLResponse>


    // [커뮤니티 글 목록 조회]
    @GET("/api/boards")
    fun getPostList(
        @Header("Authorization") auth: String?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<CommunityPostLResponse>

    // [커뮤니티 글 목록 작성]
    @Multipart //form-data 사용
    @POST("/api/boards")
    fun postCommunityPost(
        @Header("Authorization") auth: String?,
        @Part image: MultipartBody.Part?,
        @Part("data") data: PostWriteDTO
    ): Call<PostWriteResponseDTO<String>>

    // [커뮤니티 좋아요 등록]
    @POST("/api/boards/{boardId}/like")
    fun postPostLike(
        @Header("Authorization") auth: String?,
        @Path("boardId") boardId: Int?
    ): Call<PostLikeCommentResponse>

    // [커뮤니티 좋아요 삭제]
    @DELETE("/api/boards/{boardId}/like")
    fun deletePostLike(
        @Header("Authorization") auth: String?,
        @Path("boardId") boardId: Int?
    ): Call<PostLikeCommentResponse>

    // [커뮤니티 댓글 목록 조회]
    @GET("/api/boards/{boardId}/comments")
    fun getPostComments(
        @Header("Authorization") auth: String?,
        @Path("boardId") boardId: Int?,
        @Query("size") size: Int?,
        @Query("page") page: Int?
    ): Call<CommentsGetResponse>

    // [커뮤니티 댓글 등록]
    @POST("/api/boards/{boardId}/comments")
    fun postPostComment(
        @Header("Authorization") auth: String?,
        @Path("boardId") boardId: Int?,
        @Body content: CommentWriteDTO
    ): Call<PostLikeCommentResponse>

    // [커뮤니티 댓글 삭제]
    @DELETE("/api/boards/{boardId}/comments/{commentId}")
    fun deletePostComment(
        @Header("Authorization") auth: String?,
        @Path("boardId") boardId: Int?,
        @Path("commentId") commentId: Int?
    ): Call<PostLikeCommentResponse>

}