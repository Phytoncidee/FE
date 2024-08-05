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
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.Top100Response

class Top100ViewHolder(val binding: ItemMountainBinding): RecyclerView.ViewHolder(binding.root)
class Top100Adapter(val context: Context, val datas:MutableList<Top100Item>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Top100ViewHolder(ItemMountainBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as Top100ViewHolder).binding
        val model = datas!![position]

        // [[산 정보]]
        binding.mntiname.text = model.frtrlNm
        binding.mntihigh.text = model.aslAltide
        binding.mntiadd.text = model.addrNm


        // [[산 이미지]]
        // <Retrofit 통신 요청_산 검색>
        val callI : Call<MSearchResponse> = RetrofitConnection.jsonNetServ.getMountain(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVng",
            model.frtrlNm
        )

        // <Retrofit 통신 응답_산 검색: (해당 이름으로 검색 -> mntilistno 얻어서 이미지 api 통신)>
        callI.enqueue(object : Callback<MSearchResponse> {
                override fun onResponse(call: Call<MSearchResponse>, response: Response<MSearchResponse>) {

                    if (response.isSuccessful) {

                        Log.d("mobileApp", "MSearchResponse: $response")
//                        Log.d("mobileApp", "MSearchResponse: ${response.body()}")

                        // <Retrofit 통신 요청_산 이미지: mntilistno 통해 imgfilename 얻어옴>
                        val call : Call<MImageResponse> = RetrofitConnection.jsonNetServ.getMountainImage(
                            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVng",
                            response.body()!!.response!!.body!!.items!!.itemList[0]!!.mntilistno
                        )

                        // <Retrofit 통신 응답_산 이미지>
                        call.enqueue(object : Callback<MImageResponse> {
                            override fun onResponse(call: Call<MImageResponse>, response: Response<MImageResponse>) {

                                if (response.isSuccessful) {

                                    Log.d("mobileApp", "MImageResponse: $response")
                                    Log.d("mobileApp", "MImageResponse: ${response.body()!!.response.body.items.itemList[0].imgfilename}")

                                    Glide.with(binding.root)
                                        .load("http://www.forest.go.kr/images/data/down/mountain/${response.body()!!.response.body.items.itemList[0].imgfilename}")
                                        .override(50, 50) // 이미지 크기 조정
                                        .into(binding.mntImg)

                                } else {
                                    // 오류 처리
                                    Log.e("mobileApp", "Error: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<MImageResponse>, t: Throwable) {
                                // 네트워크 오류 처리
                                Log.e("mobileApp", "Failed to fetch data(getMountainImage)", t)
                            }
                        })


                    } else {
                        // 오류 처리
                        Log.e("mobileApp", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MSearchResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(getMountain)", t)
                }
            })


        // [[산 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<MBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getMountainBookmark(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzI2MTk0MSwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.7jJ8Y5eu95xmPEIrh1Q2KjLgxLnAOVFolMMHK7bI6QLRMdoIpAyd8kOPmVungVa_N_GzbCsDKglTKjTwCzdVng",
            5,
            0
        )
        callB.enqueue(object : Callback<MBookmarkGetResponse> {
            override fun onResponse(call: Call<MBookmarkGetResponse>, response: Response<MBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "MBookmarksResponse: $response")

                    // >> 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 산의 이름이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].name == model.frtrlNm){
                            isBookmarked = true // false -> true 변경
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                    } else { // 미등록
                        binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    }

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "MBookmarksError: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<MBookmarkGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMountainBookmarks)", t)
            }
        })

//        // [즐겨찾기 등록/삭제 -> 표시]
//        binding.btnMBookmark.setOnClickListener {
//            if(){ // 미등록
//                binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_on)
//                // <즐겨찾기 등록>
//
//                Toast.makeText(context, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()
//            } else { // 등록
//                binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
//                // <즐겨찾기 삭제>
//
//                Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()
//            }
//        }


        // [[리사이클러 뷰 클릭 -> 산 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            Intent(context, MountainInfoActivity::class.java).apply {
                putExtra("mountainName", binding.mntiname.text) // 산 이름 전달
            }.run { context.startActivity(this) }
        }
    }
}