package com.example.hikinglog_fe.interfaces

import com.example.hikinglog_fe.models.LoginRequest
import com.example.hikinglog_fe.models.LoginResponse
import com.example.hikinglog_fe.models.RegisterRequest
import com.example.hikinglog_fe.models.RegisterResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("api/member/login")
    fun loginUser(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("/api/member/join")
    fun registerUser(
        @Body request: RegisterRequest,
    ): Call<RegisterResponse>

    companion object {
        private const val BASE_URL = "http://192.168.0.10:8080/"    // http://localhost:8080/
        val gson : Gson =   GsonBuilder().setLenient().create();

        fun create() : AuthApi {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(AuthApi::class.java)
        }
    }
}