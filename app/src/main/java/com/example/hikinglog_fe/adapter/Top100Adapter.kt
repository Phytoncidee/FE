package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.models.MImageResponse
import com.example.hikinglog_fe.models.MSearchResponse
import com.example.hikinglog_fe.models.Top100Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.NationalMountainsActivity
import com.example.hikinglog_fe.databinding.ItemTop100MountainBinding
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.Top100Response

class Top100ViewHolder(val binding: ItemTop100MountainBinding): RecyclerView.ViewHolder(binding.root)
class Top100Adapter(val context: Context, val datas:MutableList<Top100Item>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Top100ViewHolder(ItemTop100MountainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as Top100ViewHolder).binding
        val model = datas!![position]

        // [[산 정보]]
        binding.mntiname.text = model.frtrlNm
        binding.mntihigh.text = model.aslAltide
        binding.mntiadd.text = model.addrNm


        // [[리사이클러 뷰 클릭 -> 산 상세 페이지로 이동]] ===수정===> [산 목록 페이지_검색창에 산 이름 입력된 형태로 이동]
        binding.root.setOnClickListener {
            Intent(context, NationalMountainsActivity::class.java).apply {
                putExtra("mountainName", binding.mntiname.text) // 산 이름 전달
            }.run { context.startActivity(this) }
        }
    }
}


//        // [[산 이미지]]
//        // <Retrofit 통신 요청_산 검색>
//        val callI : Call<MSearchResponse> = RetrofitConnection.jsonNetServ.getMountain(
//            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVng",
//            model.frtrlNm
//        )
//
//        // <Retrofit 통신 응답_산 검색: (해당 이름으로 검색 -> mntilistno 얻어서 이미지 api 통신)>
//        callI.enqueue(object : Callback<MSearchResponse> {
//                override fun onResponse(call: Call<MSearchResponse>, response: Response<MSearchResponse>) {
//
//                    if (response.isSuccessful) {
//
//                        Log.d("mobileApp", "MSearchResponse: $response")
////                        Log.d("mobileApp", "MSearchResponse: ${response.body()}")
//
//                        // <Retrofit 통신 요청_산 이미지: mntilistno 통해 imgfilename 얻어옴>
//                        val call : Call<MImageResponse> = RetrofitConnection.jsonNetServ.getMountainImage(
//                            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVng",
//                            response.body()!!.response!!.body!!.items!!.itemList[0]!!.mntilistno
//                        )
//
//                        // <Retrofit 통신 응답_산 이미지>
//                        call.enqueue(object : Callback<MImageResponse> {
//                            override fun onResponse(call: Call<MImageResponse>, response: Response<MImageResponse>) {
//
//                                if (response.isSuccessful) {
//
//                                    Log.d("mobileApp", "MImageResponse: $response")
//                                    Log.d("mobileApp", "MImageResponse: ${response.body()!!.response.body.items.itemList[0].imgfilename}")
//
//                                    Glide.with(binding.root)
//                                        .load("http://www.forest.go.kr/images/data/down/mountain/${response.body()!!.response.body.items.itemList[0].imgfilename}")
//                                        .override(50, 50) // 이미지 크기 조정
//                                        .into(binding.mntImg)
//
//                                } else {
//                                    // 오류 처리
//                                    Log.e("mobileApp", "Error: ${response.code()}")
//                                }
//                            }
//
//                            override fun onFailure(call: Call<MImageResponse>, t: Throwable) {
//                                // 네트워크 오류 처리
//                                Log.e("mobileApp", "Failed to fetch data(getMountainImage)", t)
//                            }
//                        })
//
//
//                    } else {
//                        // 오류 처리
//                        Log.e("mobileApp", "Error: ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<MSearchResponse>, t: Throwable) {
//                    // 네트워크 오류 처리
//                    Log.e("mobileApp", "Failed to fetch data(getMountain)", t)
//                }
//            })