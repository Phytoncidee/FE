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
import com.phytoncidee.hikinglog_fe.AccommodationDetailActivity
import com.phytoncidee.hikinglog_fe.RetrofitConnection
import com.phytoncidee.hikinglog_fe.databinding.ItemAccommodationBinding
import com.phytoncidee.hikinglog_fe.models.AccommodationBookmarkDeleteResponse
import com.phytoncidee.hikinglog_fe.models.AccommodationBookmarkGetBookmark
import com.phytoncidee.hikinglog_fe.models.AccommodationBookmarkGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookMarkAccommodationHolder(val binding: ItemAccommodationBinding): RecyclerView.ViewHolder(binding.root)
class BookMarkAccommodationAdapter(val context: Context, val datas:MutableList<AccommodationBookmarkGetBookmark>?, private val token: String?, private val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookMarkAccommodationHolder(ItemAccommodationBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as BookMarkAccommodationHolder).binding
        val model = datas!![position]

        // [[숙박시설 정보]]
        binding.accommodationName.text = model.name
        binding.accommodationAdd.text = model.location

        if (model.image != "") {
            // <숙박시설 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.image}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.imgAccommodation)
        }


        // [[리사이클러 뷰 클릭 -> 숙박시설 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            Intent(context, AccommodationDetailActivity::class.java).apply {
                putExtra("contentId", model.storeId)
            }.run { context.startActivity(this) }
        }


        // [[숙박시설 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<AccommodationBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getAccommodationBookmark(
            "Bearer $token",
            2147483647,
            0
        )
        callB.enqueue(object : Callback<AccommodationBookmarkGetResponse> {
            override fun onResponse(call: Call<AccommodationBookmarkGetResponse>, response: Response<AccommodationBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 숙박시설이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].storeId == model.storeId){
                            isBookmarked = true // false -> true 변경
                            Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d("mobileApp", "${model.storeId}: isBookmarked가 true로 유지되는 중!!")
                    } else { // 미등록
                        binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    }

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

//        // [즐겨찾기 등록된 상태 -> 삭제 -> 재조회]
        binding.BtnAccBookmark.setOnClickListener {

            if(isBookmarked == true){ // 등록

                binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // <즐겨찾기 삭제>
                val callB: Call<AccommodationBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteAccommodationBookmark(
                    "Bearer $token",
                    model.storeId
                )
                callB.enqueue(object : Callback<AccommodationBookmarkDeleteResponse> {
                    override fun onResponse(call: Call<AccommodationBookmarkDeleteResponse>, response: Response<AccommodationBookmarkDeleteResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deleteAccommodationBookmark: $response")
                            // >> 즐겨찾기한 숙박 시설 목록 재조회<<
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
                                        recyclerView.layoutManager = LinearLayoutManager(context)
                                        recyclerView.adapter = BookMarkAccommodationAdapter(context, response.body()!!.data.bookmarkList, token, recyclerView)
                                        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
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
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "deleteAccommodationBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<AccommodationBookmarkDeleteResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(deleteAccommodationBookmark)", t)
                    }
                })

                Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()

            }
        }
    }
}