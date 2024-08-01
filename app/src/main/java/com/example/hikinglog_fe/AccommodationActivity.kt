package com.example.hikinglog_fe

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.adapter.EquipmentShopAdapter
import com.example.hikinglog_fe.databinding.ActivityAccommodationBinding
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.EquipmentShopLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccommodationActivity : AppCompatActivity() {
    lateinit var binding : ActivityAccommodationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccommodationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Retrofit 통신 요청: 등산용품점 목록]
        val call: Call<AccommodationLResponse> = RetrofitConnection.jsonNetServ.getAccommodationList(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzQ5NjM5MiwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.TKguWwv_0JcaNgtzinEpn7GRLYusUUnX9s6ZlOiFS00HJOMKbSGdGfbrUNqyrGExqdEQuOGy2Z11ZZUvF28jAg",
            127.01612551862054,
            37.6525631765458
            // 현재 위도, 경도 받아서 넘겨주는 처리 필요
        )

        // [Retrofit 통신 응답: 등산용품점 목록]
        call.enqueue(object : Callback<AccommodationLResponse> {
            override fun onResponse(call: Call<AccommodationLResponse>, response: Response<AccommodationLResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationList: $response")

                    // <리사이클러뷰에 표시>
                    binding.accommodationRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.accommodationRecyclerView.adapter = AccommodationAdapter(this@AccommodationActivity, response.body()!!.data)
                    binding.accommodationRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getAccommodationList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AccommodationLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getAccommodationList)", t)
            }
        })

    } //onCreate()
}