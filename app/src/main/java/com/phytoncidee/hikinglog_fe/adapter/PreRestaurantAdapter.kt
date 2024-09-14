package com.phytoncidee.hikinglog_fe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.phytoncidee.hikinglog_fe.R
import com.phytoncidee.hikinglog_fe.RetrofitConnection
import com.phytoncidee.hikinglog_fe.databinding.ItemRestaurantBinding
import com.phytoncidee.hikinglog_fe.interfaces.OnDataPassListener
import com.phytoncidee.hikinglog_fe.models.PostRestaurantBMDTO
import com.phytoncidee.hikinglog_fe.models.Restaurant
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkDeleteResponse
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkGetResponse
import com.phytoncidee.hikinglog_fe.models.RestaurantBookmarkPostResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreRestaurantHolder(val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root)
class PreRestaurantAdapter(val context: Context, private val listener: OnDataPassListener, val datas:MutableList<Restaurant>?, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PreRestaurantHolder(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as PreRestaurantHolder).binding
        val model = datas!![position]

        // [[음식점 정보]]
        binding.restaurantName.text = model.name
        binding.restaurantAdd.text = model.add


        if (model.img != "") {
            // <음식점 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.img}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.ImgRestaurant)
        }
        else {
            if (model.img2 != "") {
                // <음식점 이미지 표시(Glide)>
                Glide.with(binding.root)
                    .load("${model.img2}")
                    .override(100, 100) // 이미지 크기 조정
                    .into(binding.ImgRestaurant)
            }
        }


        var isSelected : Boolean = false

        // [[리사이클러 뷰 클릭 -> 색 변화 & 데이터 저장해서 넘기기 ]]
        binding.root.setOnClickListener {
            val preRestaurant = Restaurant(
                name = model.name,
                contentId = model.contentId,
                add = model.add,
                img = model.img,
                img2 = model.img2,
                mapX = model.mapX,
                mapY = model.mapY,
                tel = model.tel
            )
            if (isSelected == false){ //미선택인 상황
                isSelected = true
                // 색 변화
                binding.root.setBackgroundResource(R.color.backgroundblue)
                binding.BtnResBookmark.setBackgroundResource(R.color.backgroundblue)
                // >> 데이터 CreateCourseActivity로 넘기기
                listener.preRestaurantToActivity(preRestaurant)
            }
            else { //선택인 상황에서 취소를 위해 재선택
                isSelected = false
                // 색 변화
                binding.root.setBackgroundResource(R.color.backgroundyellow)
                binding.BtnResBookmark.setBackgroundResource(R.color.backgroundyellow)
                // >> 취소된 경우 CreateCourseActivity에 알리기
                listener.CancelpreRestaurant(preRestaurant)
            }
        }


        // [[음식점 즐겨찾기]]
        var isBookmarked : Boolean = false

        // [즐겨찾기 조회 -> 표시]
        // <즐겨찾기 조회>
        val callB: Call<RestaurantBookmarkGetResponse> = RetrofitConnection.jsonNetServ.getRestaurantBookmark(
            "Bearer $token",
            5,
            0
        )
        callB.enqueue(object : Callback<RestaurantBookmarkGetResponse> {
            override fun onResponse(call: Call<RestaurantBookmarkGetResponse>, response: Response<RestaurantBookmarkGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantBookmark: $response")
                    // 즐겨찾기 여부 저장
                    for (i in 0..response.body()!!.data.bookmarkList.size-1){ //bookmarkList에 해당 숙박시설이 존재하는지 확인
                        if(response.body()!!.data.bookmarkList[i].storeId == model.contentId){
                            isBookmarked = true // false -> true 변경
                            Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                        }
                    }

                    // <즐겨찾기 버튼 표시>
                    if (isBookmarked == true) { // 등록
                        binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                        Log.d("mobileApp", "${model.contentId}: isBookmarked가 true로 유지되는 중!!")
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

//        // [즐겨찾기 등록/삭제 -> 표시]
        binding.BtnResBookmark.setOnClickListener {

            if(isBookmarked == false){ // 미등록

                binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                isBookmarked = true

                // <즐겨찾기 등록>
                val newRestaurantnBM = PostRestaurantBMDTO(name = model.name, location = model.add, phone = model.tel, image = model.img)

                val callB: Call<RestaurantBookmarkPostResponse> = RetrofitConnection.jsonNetServ.postRestaurantBookmark(
                    "Bearer $token",
                    model.contentId,
                    newRestaurantnBM
                )
                callB.enqueue(object : Callback<RestaurantBookmarkPostResponse> {
                    override fun onResponse(call: Call<RestaurantBookmarkPostResponse>, response: Response<RestaurantBookmarkPostResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "postRestaurantBookmark: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "postRestaurantBookmark: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<RestaurantBookmarkPostResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(postRestaurantBookmark)", t)
                    }
                })
                Toast.makeText(context, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()

            }
            else { // 등록

                binding.BtnResBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                isBookmarked = false

                // <즐겨찾기 삭제>
                val callB: Call<RestaurantBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteRestaurantBookmark(
                    "Bearer $token",
                    model.contentId
                )
                callB.enqueue(object : Callback<RestaurantBookmarkDeleteResponse> {
                    override fun onResponse(call: Call<RestaurantBookmarkDeleteResponse>, response: Response<RestaurantBookmarkDeleteResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deleteRestaurantBookmark: $response")
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