package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.adapter.RestaurantAdapter
import com.example.hikinglog_fe.databinding.ActivityAccommodationBinding
import com.example.hikinglog_fe.databinding.ActivityRestaurantBinding
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.RestaurantLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantActivity : AppCompatActivity() {
    lateinit var binding : ActivityRestaurantBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // [Retrofit 통신 요청: 음식점 목록]
        val call: Call<RestaurantLResponse> = RetrofitConnection.jsonNetServ.getRestaurantList(
            "Bearer $token",
            127.01612551862054,
            37.6525631765458
            // 현재 위도, 경도 받아서 넘겨주는 처리 필요
        )

        // [Retrofit 통신 응답: 음식점 목록]
        call.enqueue(object : Callback<RestaurantLResponse> {
            override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantList: $response")

                    // <리사이클러뷰에 표시>
                    binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.restaurantRecyclerView.adapter = RestaurantAdapter(this@RestaurantActivity, response.body()!!.data, token)
                    binding.restaurantRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getRestaurantList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getRestaurantList)", t)
            }
        })

    } //onCreate()
}