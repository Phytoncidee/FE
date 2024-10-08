package com.phytoncidee.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.phytoncidee.hikinglog_fe.adapter.EquipmentShopAdapter
import com.phytoncidee.hikinglog_fe.databinding.ActivityEquipmentShopBinding
import com.phytoncidee.hikinglog_fe.models.EquipmentShopLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EquipmentShopActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var binding : ActivityEquipmentShopBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquipmentShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // [Retrofit 통신 요청: 등산용품점 목록]
        val call: Call<EquipmentShopLResponse> = RetrofitConnection.jsonNetServ.getEquipmentShopList(
            "Bearer $token"
        )

        // [Retrofit 통신 응답: 등산용품점 목록]
        call.enqueue(object : Callback<EquipmentShopLResponse> {
            override fun onResponse(call: Call<EquipmentShopLResponse>, response: Response<EquipmentShopLResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getEquipmentShopList: $response")

                    // <리사이클러뷰에 표시>
                    binding.equipmentShopRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.equipmentShopRecyclerView.adapter = EquipmentShopAdapter(this@EquipmentShopActivity, response.body()!!.data, token)
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