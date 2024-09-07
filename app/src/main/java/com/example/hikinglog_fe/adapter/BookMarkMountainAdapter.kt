package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.MountainBookmarkItem
import com.example.hikinglog_fe.models.MountainAddBookmarkRequest
import com.example.hikinglog_fe.models.MountainAddBookmarkResponse
import com.example.hikinglog_fe.models.MountainCheckBookmarkResponse
import com.example.hikinglog_fe.models.MountainDeleteBookmarkResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookMarkMountainHolder(val binding: ItemMountainBinding): RecyclerView.ViewHolder(binding.root)

class BookMarkMountainAdapter(val context: Context, val datas:MutableList<MountainBookmarkItem>?, private val token: String?, private val recyclerView: RecyclerView,  private val apiService: ApiService): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BookMarkMountainHolder(ItemMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as BookMarkMountainHolder).binding
        val model = datas!![position]

        // 산 정보
        binding.mntiname.text = model.name
        binding.mntiadd.text = model.location
        binding.mntihigh.text = model.mntiHigh.toString()


        if (model.image != "") {
            // <숙박시설 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.image}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.mntImg)
        }

        // 산 상세 페이지로 이동
//        binding.root.setOnClickListener {
//            Intent(context, MountainInfoActivity::class.java).apply {
//                putExtra("name", model.name)
//                putExtra("listno", model.mntilistno)
//            }.run { context.startActivity(this) }
//        }


        // 산 즐겨찾기
        var isBookmarked = false

        // 산 즐겨찾기 조회
        val call = apiService.getMtnBookmark("Bearer $token", size = 2147483647, page = 0)
        call.enqueue(object : Callback<MountainCheckBookmarkResponse> {
            override fun onResponse(
                call: Call<MountainCheckBookmarkResponse>,
                response: Response<MountainCheckBookmarkResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getMountainBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size - 1) { // bookmarkList에 해당 산이 존재하는지 확인
                        if (response.body()!!.data.bookmarkList[i].mntilistno == model.mntilistno) {
                            isBookmarked = true // false -> true 변경
                            Log.d(
                                "mobileApp",
                                "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!"
                            )
                        }
                    }

                    // 즐겨찾기 버튼 표시
                    if (isBookmarked == true) { // 등록
                        binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d(
                            "mobileApp",
                            "${model.mntilistno}: isBookmarked가 true로 유지되는 중!!"
                        )
                    } else { // 미등록
                        binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    }
                } else {
                    // 오류처리
                    Log.e("mobileApp", "getMountainBookmark: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MountainCheckBookmarkResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMountainBookmark)", t)
            }
        })

        // [즐겨찾기 등록된 상태 -> 삭제 -> 재조회]
        binding.btnMBookmark.setOnClickListener {
            if (isBookmarked == true) {    // 등록
                binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // 즐겨찾기 삭제
                apiService.deleteMtnBookmark("Bearer $token", model.mntilistno)
                    .enqueue(object : Callback<MountainDeleteBookmarkResponse>{
                        override fun onResponse(
                            call: Call<MountainDeleteBookmarkResponse>,
                            response: Response<MountainDeleteBookmarkResponse>
                        ) {
                            if(response.isSuccessful){
                                Log.d("mobileApp", "deleteMountainBookmark: $response")
                                // 즐겨찾기한 산 목록 재조회
                                apiService.getMtnBookmark("Bearer $token", 2147483647, 0)
                                    .enqueue(object : Callback<MountainCheckBookmarkResponse>{
                                        override fun onResponse(
                                            call: Call<MountainCheckBookmarkResponse>,
                                            response: Response<MountainCheckBookmarkResponse>
                                        ) {
                                            if(response.isSuccessful) {
                                                Log.d("mobileApp", "getMountainBookmark: $response")
                                                Log.d("mobileApp", "getMountainBookmark: ${response.body()!!.data.bookmarkList}")
                                                // 즐겨찾기된 등산용품점 목록 recyclerview에 표시
                                                recyclerView.layoutManager = LinearLayoutManager(context)
                                                recyclerView.adapter = BookMarkMountainAdapter(context, response.body()!!.data.bookmarkList, token, recyclerView, apiService)
                                                recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                                            } else {
                                                // 오류 처리
                                                Log.e("mobileApp", "getMountainBookmark: ${response.code()}")
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<MountainCheckBookmarkResponse>,
                                            t: Throwable
                                        ) {
                                            // 네트워크 오류 처리
                                            Log.e("mobileApp", "Failed to fetch data(getMountainBookmark)", t)
                                        }

                                    })
                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "deleteMountainBookmark: ${response.code()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<MountainDeleteBookmarkResponse>,
                            t: Throwable
                        ) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(deleteMountainBookmark)", t)
                        }

                    })

                Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()
            }
        }
    }
}