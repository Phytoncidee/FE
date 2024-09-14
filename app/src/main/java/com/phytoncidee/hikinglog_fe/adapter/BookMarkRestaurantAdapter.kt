package com.phytoncidee.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.phytoncidee.hikinglog_fe.RestaurantDetailActivity
import com.phytoncidee.hikinglog_fe.RetrofitConnection
import com.phytoncidee.hikinglog_fe.databinding.ItemRestaurantBinding
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkDeleteResponse
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkGetBookmark
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookMarkRestaurantHolder(val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root)
class BookMarkRestaurantAdapter(val context: Context, val datas:MutableList<RestaurantBookmarkGetBookmark>?, private val token: String?,private val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookMarkRestaurantHolder(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as BookMarkRestaurantHolder).binding
        val model = datas!![position]

        // [[음식점 정보]]
        binding.restaurantName.text = model.name
        binding.restaurantAdd.text = model.location


        if (model.image != "") {
            // <음식점 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.image}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.ImgRestaurant)
        }


        // [[리사이클러 뷰 클릭 -> 음식점 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            Intent(context, RestaurantDetailActivity::class.java).apply {
                putExtra("contentId", model.storeId) // 산 이름 전달
            }.run { context.startActivity(this) }
        }


        // [[음식점 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<RestaurantBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getRestaurantBookmark(
            "Bearer $token",
            2147483647,
            0
        )
        callB.enqueue(object : Callback<RestaurantBookmarkGetResponse> {
            override fun onResponse(call: Call<RestaurantBookmarkGetResponse>, response: Response<RestaurantBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 숙박시설이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].storeId == model.storeId){
                            isBookmarked = true // false -> true 변경
                            Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d("mobileApp", "${model.storeId}: isBookmarked가 true로 유지되는 중!!")
                    } else { // 미등록
                        binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    }

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

//        // [즐겨찾기 등록된 상태 -> 삭제 -> 재조회]
        binding.BtnResBookmark.setOnClickListener {

            if(isBookmarked == true){ // 등록

                binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // <즐겨찾기 삭제>
                val callB: Call<RestaurantBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteRestaurantBookmark(
                    "Bearer $token",
                    model.storeId
                )
                callB.enqueue(object : Callback<RestaurantBookmarkDeleteResponse> {
                    override fun onResponse(call: Call<RestaurantBookmarkDeleteResponse>, response: Response<RestaurantBookmarkDeleteResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deleteRestaurantBookmark: $response")
                            // >> 즐겨찾기한 숙박 시설 목록 재조회<<
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
                                        recyclerView.layoutManager = LinearLayoutManager(context)
                                        recyclerView.adapter = BookMarkRestaurantAdapter(context, response.body()!!.data.bookmarkList, token, recyclerView)
                                        recyclerView.addItemDecoration(
                                            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                                        )
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
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "deleteRestaurantBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<RestaurantBookmarkDeleteResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(deleteRestaurantBookmark)", t)
                    }
                })

                Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()

            }
        }
    }
}