package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.AccommodationDetailActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemAccommodationBinding
import com.example.hikinglog_fe.models.Accommodation
import com.example.hikinglog_fe.models.AccommodationBookmarkDeleteResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkGetResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkPostResponse
import com.example.hikinglog_fe.models.EShopBookmarkDeleteResponse
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EShopBookmarkPostResponse
import com.example.hikinglog_fe.models.PostAccommodationBMDTO
import com.example.hikinglog_fe.models.PostEShopBMDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccommodationHolder(val binding: ItemAccommodationBinding): RecyclerView.ViewHolder(binding.root)
class AccommodationAdapter(val context: Context, val datas:MutableList<Accommodation>?, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccommodationHolder(ItemAccommodationBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AccommodationHolder).binding
        val model = datas!![position]

        // [[숙박시설 정보]]
        binding.accommodationName.text = model.name
        binding.accommodationAdd.text = model.add

        if (model.img != "") {
            // <숙박시설 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.img}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.imgAccommodation)
        }
        else {
            if (model.img2 != "") {
                // <숙박시설 이미지 표시(Glide)>
                Glide.with(binding.root)
                    .load("${model.img2}")
                    .override(100, 100) // 이미지 크기 조정
                    .into(binding.imgAccommodation)
            }
        }


        // [[리사이클러 뷰 클릭 -> 숙박시설 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            Intent(context, AccommodationDetailActivity::class.java).apply {
                putExtra("contentId", model.contentId) // 산 이름 전달
            }.run { context.startActivity(this) }
        }


        // [[숙박시설 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<AccommodationBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getAccommodationBookmark(
            "Bearer $token",
            5,
            0
        )
        callB.enqueue(object : Callback<AccommodationBookmarkGetResponse> {
            override fun onResponse(call: Call<AccommodationBookmarkGetResponse>, response: Response<AccommodationBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 숙박시설이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].storeId == model.contentId){
                            isBookmarked = true // false -> true 변경
                            Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d("mobileApp", "${model.contentId}: isBookmarked가 true로 유지되는 중!!")
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

//        // [즐겨찾기 등록/삭제 -> 표시]
        binding.BtnAccBookmark.setOnClickListener {

            if(isBookmarked == false){ // 미등록

                binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                isBookmarked = true

                // <즐겨찾기 등록>
                val newAccommodationBM = PostAccommodationBMDTO(name = model.name, location = model.add, phone = model.tel, image = model.img)

                val callB: Call<AccommodationBookmarkPostResponse> = RetrofitConnection.jsonNetServ.postAccommodationBookmark(
                    "Bearer $token",
                    model.contentId,
                    newAccommodationBM
                )
                callB.enqueue(object : Callback<AccommodationBookmarkPostResponse> {
                    override fun onResponse(call: Call<AccommodationBookmarkPostResponse>, response: Response<AccommodationBookmarkPostResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "postAccommodationBookmark: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "postAccommodationBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<AccommodationBookmarkPostResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(postAccommodationBookmark)", t)
                    }
                })
                Toast.makeText(context, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()

            }
            else { // 등록

                binding.BtnAccBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // <즐겨찾기 삭제>
                val callB: Call<AccommodationBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteAccommodationBookmark(
                    "Bearer $token",
                    model.contentId
                )
                callB.enqueue(object : Callback<AccommodationBookmarkDeleteResponse> {
                    override fun onResponse(call: Call<AccommodationBookmarkDeleteResponse>, response: Response<AccommodationBookmarkDeleteResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deleteAccommodationBookmark: $response")
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