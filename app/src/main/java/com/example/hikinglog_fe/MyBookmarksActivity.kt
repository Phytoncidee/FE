package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.BookMarkAccommodationAdapter
import com.example.hikinglog_fe.adapter.BookMarkEShopAdapter
import com.example.hikinglog_fe.adapter.BookMarkRestaurantAdapter
import com.example.hikinglog_fe.adapter.Top100Adapter
import com.example.hikinglog_fe.databinding.ActivityMyBookmarksBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.AccommodationBookmarkGetResponse
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EquipmentShopLResponse
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.PostWriteDTO
import com.example.hikinglog_fe.models.RestaurantBookmarkGetResponse
import com.example.hikinglog_fe.models.Top100Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBookmarksBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)!!

        // [[기본으로 산 즐겨찾기 조회]]
        MountainBookmark()

        // [[산 버튼 클릭]]
        binding.BtnMountain.setOnClickListener {
            // >> 버튼 색
            binding.BtnMountain.setImageResource(R.drawable.button_mybookmark_mountain)
            binding.BtnRestaurant.setImageResource(R.drawable.button_mybookmark_light_restaurant)
            binding.BtnAccommodation.setImageResource(R.drawable.button_mybookmark_light_accommodation)
            binding.BtnEquipment.setImageResource(R.drawable.button_mybookmark_light_equipment)

            // >> 조회
            MountainBookmark()
        }

        // [[음식점 버튼 클릭]]
        binding.BtnRestaurant.setOnClickListener {
            // >> 버튼 색
            binding.BtnMountain.setImageResource(R.drawable.button_mybookmark_light_mountain)
            binding.BtnRestaurant.setImageResource(R.drawable.button_mybookmark_restaurant)
            binding.BtnAccommodation.setImageResource(R.drawable.button_mybookmark_light_accommodation)
            binding.BtnEquipment.setImageResource(R.drawable.button_mybookmark_light_equipment)

            // >> 조회
            RestaurantBookmark()
        }

        // [[숙박시설 버튼 클릭]]
        binding.BtnAccommodation.setOnClickListener {
            // >> 버튼 색
            binding.BtnMountain.setImageResource(R.drawable.button_mybookmark_light_mountain)
            binding.BtnRestaurant.setImageResource(R.drawable.button_mybookmark_light_restaurant)
            binding.BtnAccommodation.setImageResource(R.drawable.button_mybookmark_accommodation)
            binding.BtnEquipment.setImageResource(R.drawable.button_mybookmark_light_equipment)

            // >> 조회
            AccommodationBookmark()
        }

        // [[등산용품점 버튼 클릭]]
        binding.BtnEquipment.setOnClickListener {
            // >> 버튼 색
            binding.BtnMountain.setImageResource(R.drawable.button_mybookmark_light_mountain)
            binding.BtnRestaurant.setImageResource(R.drawable.button_mybookmark_light_restaurant)
            binding.BtnAccommodation.setImageResource(R.drawable.button_mybookmark_light_accommodation)
            binding.BtnEquipment.setImageResource(R.drawable.button_mybookmark_equipment)

            // >> 조회
            EquipmentBookmark()
        }

    } //onCreate()

    private fun MountainBookmark() {
        // 효민이가 산 즐겨찾기 목록 조회, 삭제 구현해줘!!!
    }

    private fun RestaurantBookmark() {
        // [Retrofit 통신 요청: 음식점 즐겨찾기]
        val callB: Call<RestaurantBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getRestaurantBookmark(
            "Bearer $token",
            2147483647,
            0
        )
        callB.enqueue(object : Callback<RestaurantBookmarkGetResponse> {
            override fun onResponse(call: Call<RestaurantBookmarkGetResponse>, response: Response<RestaurantBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantBookmark: $response")
                    Log.d("mobileApp", "getRestaurantBookmark: ${response.body()!!.data.bookmarkList}")
                    // 즐겨찾기된 등산용품점 목록 recyclerview에 표시
                    binding.BookmarksRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.BookmarksRecyclerView.adapter = BookMarkRestaurantAdapter(this@MyBookmarksActivity, response.body()!!.data.bookmarkList, token, binding.BookmarksRecyclerView)
                    binding.BookmarksRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getRestaurantBookmark: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RestaurantBookmarkGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getRestaurantBookmark)", t)
            }
        })
    }

    private fun AccommodationBookmark() {
        // [Retrofit 통신 요청: 숙박시설 즐겨찾기]
        val callB: Call<AccommodationBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getAccommodationBookmark(
            "Bearer $token",
            2147483647,
            0
        )
        callB.enqueue(object : Callback<AccommodationBookmarkGetResponse> {
            override fun onResponse(call: Call<AccommodationBookmarkGetResponse>, response: Response<AccommodationBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationBookmark: $response")
                    Log.d("mobileApp", "getAccommodationBookmark: ${response.body()!!.data.bookmarkList}")
                    // 즐겨찾기된 등산용품점 목록 recyclerview에 표시
                    binding.BookmarksRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.BookmarksRecyclerView.adapter = BookMarkAccommodationAdapter(this@MyBookmarksActivity, response.body()!!.data.bookmarkList, token, binding.BookmarksRecyclerView)
                    binding.BookmarksRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getAccommodationBookmark: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AccommodationBookmarkGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getAccommodationBookmark)", t)
            }
        })
    }

    private fun EquipmentBookmark() {
        // [Retrofit 통신 요청: 등산용품점 즐겨찾기]
        val callB: Call<EShopBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getEShopBookmark(
            "Bearer $token",
            2147483647,
            0
        )
        callB.enqueue(object : Callback<EShopBookmarkGetResponse> {
            override fun onResponse(call: Call<EShopBookmarkGetResponse>, response: Response<EShopBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getEShopBookmark: $response")
                    Log.d("mobileApp", "getEShopBookmark: ${response.body()!!.data.bookmarkList}")
                    // 즐겨찾기된 등산용품점 목록 recyclerview에 표시
                    binding.BookmarksRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.BookmarksRecyclerView.adapter = BookMarkEShopAdapter(this@MyBookmarksActivity, response.body()!!.data.bookmarkList, token, binding.BookmarksRecyclerView)
                    binding.BookmarksRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getEShopBookmark: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<EShopBookmarkGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getEShopBookmark)", t)
            }
        })
    }
}