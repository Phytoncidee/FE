package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemEquipmentshopBinding
import com.example.hikinglog_fe.models.EShopBookmarkDeleteResponse
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EShopBookmarkPostResponse
import com.example.hikinglog_fe.models.EquipmentShop
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.PostEShopBMDTO
import com.example.hikinglog_fe.models.PostWriteDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentShopHolder(val binding: ItemEquipmentshopBinding): RecyclerView.ViewHolder(binding.root)
class EquipmentShopAdapter(val context: Context, val datas:MutableList<EquipmentShop>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EquipmentShopHolder(ItemEquipmentshopBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as EquipmentShopHolder).binding
        val model = datas!![position]

        // [[등산용품점 정보]]
        binding.eShopName.text = model.name

        // [[리사이클러 뷰 클릭 -> 등산용품점 링크 페이지로 이동]]
        binding.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.link))
            startActivity(context, intent, null)
        }


        // [[등산용품점 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<EShopBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getEShopBookmark(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMkBuYXZlci5jb20iLCJ1aWQiOjIsImV4cCI6MTcyMzQ0MDY2MCwiZW1haWwiOiJ1c2VyMkBuYXZlci5jb20ifQ.9wuDnkFoPsN16gUNvDroqkDx59R49P1FQqb6PzmixLFAvswgyYZHzzVhfGhT4lI-J2XjjGw9zLZ3jqN7Ywp-KQ",
            5,
            0
        )
        callB.enqueue(object : Callback<EShopBookmarkGetResponse> {
            override fun onResponse(call: Call<EShopBookmarkGetResponse>, response: Response<EShopBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getEShopBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 산의 이름이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].storeId == model.id){
                            isBookmarked = true // false -> true 변경
                            Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.BtnESBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d("mobileApp", "${model.id}: isBookmarked가 true로 유지되는 중!!")
                    } else { // 미등록
                        binding.BtnESBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    }

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

//        // [즐겨찾기 등록/삭제 -> 표시]
        binding.BtnESBookmark.setOnClickListener {

            if(isBookmarked == false){ // 미등록

                binding.BtnESBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                isBookmarked = true

                // <즐겨찾기 등록>
                val newEshopBM = PostEShopBMDTO(name = model.name, link = model.link, image = model.image)

                val callB: Call<EShopBookmarkPostResponse> = RetrofitConnection.jsonNetServ.postEShopBookmark(
                    "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMkBuYXZlci5jb20iLCJ1aWQiOjIsImV4cCI6MTcyMzQ0MDY2MCwiZW1haWwiOiJ1c2VyMkBuYXZlci5jb20ifQ.9wuDnkFoPsN16gUNvDroqkDx59R49P1FQqb6PzmixLFAvswgyYZHzzVhfGhT4lI-J2XjjGw9zLZ3jqN7Ywp-KQ",
                    model.id,
                    newEshopBM
                )
                callB.enqueue(object : Callback<EShopBookmarkPostResponse> {
                    override fun onResponse(call: Call<EShopBookmarkPostResponse>, response: Response<EShopBookmarkPostResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "postEShopBookmark: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "postEShopBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<EShopBookmarkPostResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(postEShopBookmark)", t)
                    }
                })
                Toast.makeText(context, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()

            }
            else { // 등록

                binding.BtnESBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // <즐겨찾기 삭제>
                val callB: Call<EShopBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteEShopBookmark(
                    "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMkBuYXZlci5jb20iLCJ1aWQiOjIsImV4cCI6MTcyMzQ0MDY2MCwiZW1haWwiOiJ1c2VyMkBuYXZlci5jb20ifQ.9wuDnkFoPsN16gUNvDroqkDx59R49P1FQqb6PzmixLFAvswgyYZHzzVhfGhT4lI-J2XjjGw9zLZ3jqN7Ywp-KQ",
                    model.id
                )
                callB.enqueue(object : Callback<EShopBookmarkDeleteResponse> {
                    override fun onResponse(call: Call<EShopBookmarkDeleteResponse>, response: Response<EShopBookmarkDeleteResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deleteEShopBookmark: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "deleteEShopBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<EShopBookmarkDeleteResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(deleteEShopBookmark)", t)
                    }
                })

                Toast.makeText(context, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()

            }
        }
    }
}