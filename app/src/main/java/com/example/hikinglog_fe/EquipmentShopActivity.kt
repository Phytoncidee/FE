package com.example.hikinglog_fe

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.EquipmentShopAdapter
import com.example.hikinglog_fe.adapter.Top100Adapter
import com.example.hikinglog_fe.databinding.ActivityEquipmentShopBinding
import com.example.hikinglog_fe.models.EquipmentShopLResponse
import com.example.hikinglog_fe.models.Top100Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EquipmentShopActivity : AppCompatActivity() {
    lateinit var binding : ActivityEquipmentShopBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Retrofit 통신 요청: 등산용품점 목록]
        val call: Call<EquipmentShopLResponse> = RetrofitConnection.jsonNetServ.getEquipmentShopList(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVnk"
        )

        // [Retrofit 통신 응답: 등산용품점 목록]
        call.enqueue(object : Callback<EquipmentShopLResponse> {
            override fun onResponse(call: Call<EquipmentShopLResponse>, response: Response<EquipmentShopLResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getEquipmentShopList: $response")

                    // <리사이클러뷰에 표시>
                    binding.equipmentShopRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.equipmentShopRecyclerView.adapter = EquipmentShopAdapter(this@EquipmentShopActivity, response.body()!!.data)
                    binding.equipmentShopRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getEquipmentShopList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<EquipmentShopLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getEquipmentShopList)", t)
            }
        })

    } // onCreate()
}