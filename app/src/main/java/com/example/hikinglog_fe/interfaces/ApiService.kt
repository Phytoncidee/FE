package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.LoginRequest
import com.example.hikinglog_fe.models.LoginResponse
import com.example.hikinglog_fe.models.NationalMountainsImageResponse
import com.example.hikinglog_fe.models.NationalMountainsResponse
import com.example.hikinglog_fe.models.ProfileResponse
import com.example.hikinglog_fe.models.RegisterRequest
import com.example.hikinglog_fe.models.RegisterResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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


    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"    // http://localhost:8080/
        val gson : Gson =   GsonBuilder().setLenient().create();

        fun create() : ApiService {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService::class.java)
        }
    }
}