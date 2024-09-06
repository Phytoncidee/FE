package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ActivityAccommodationDetailBinding
import com.example.hikinglog_fe.databinding.ActivityRestaurantDetailBinding
import com.example.hikinglog_fe.models.AccommodationDResponse
import com.example.hikinglog_fe.models.PostRestaurantBMDTO
import com.example.hikinglog_fe.models.RestaurantBookmarkDeleteResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkGetResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkPostResponse
import com.example.hikinglog_fe.models.RestaurantDResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityRestaurantDetailBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // 리사이클러 뷰에서 가게 contentId 받아오기
        val contentId: Int? = intent.getIntExtra("contentId", -1)

        if (contentId == -1) {
            // contentId가 올바르게 전달되지 않은 경우 예외 처리
            throw NullPointerException("contentId가 전달되지 않았습니다.")
        }

        // [Retrofit 통신 요청: 음식점 상세]
        val call: Call<RestaurantDResponse> = RetrofitConnection.jsonNetServ.getRestaurantDetail(
            "Bearer $token",
            contentId!!.toInt()
        )

        // [Retrofit 통신 응답: 음식점 상세]
        call.enqueue(object : Callback<RestaurantDResponse> {
            override fun onResponse(call: Call<RestaurantDResponse>, response: Response<RestaurantDResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantDetail: $response")

                    // <가게 상세 정보 표시>
                    // > 가게 이미지(Glide)
                    Glide.with(binding.root)
                        .load("${response.body()!!.data.img}")
                        .override(250, 200) // 이미지 크기 조정
                        .into(binding.imgShop)
                    // > 가게 이름
                    binding.nameShop.text = response.body()!!.data.name
                    // > 전화 버튼
                    binding.btnCallShop.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${response.body()!!.data.tel}"))
                        startActivity(intent)
                        true
                    }


                    // >> 음식점 즐겨찾기
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
                                    if(response.body()!!.data.bookmarkList[i].storeId == contentId!!.toInt()){
                                        isBookmarked = true // false -> true 변경
                                        Log.d("mobileApp", "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!")
                                    }
                                }

                                // <즐겨찾기 버튼 표시>
                                if (isBookmarked == true) { // 등록
                                    binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_on)
                                    Log.d("mobileApp", "${contentId!!.toInt()}: isBookmarked가 true로 유지되는 중!!")
                                } else { // 미등록
                                    binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_off)
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
                    binding.btnBookmarkShop.setOnClickListener {

                        if(isBookmarked == false){ // 미등록

                            binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_on)
                            isBookmarked = true

                            // <즐겨찾기 등록>
                            val newRestaurantnBM = PostRestaurantBMDTO(name = response.body()!!.data.name, location = response.body()!!.data.add, phone = response.body()!!.data.tel, image = response.body()!!.data.img)

                            val callB: Call<RestaurantBookmarkPostResponse> = RetrofitConnection.jsonNetServ.postRestaurantBookmark(
                                "Bearer $token",
                                contentId!!.toInt(),
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
                            Toast.makeText(applicationContext, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()

                        }
                        else { // 등록

                            binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_off)
                            isBookmarked = false

                            // <즐겨찾기 삭제>
                            val callB: Call<RestaurantBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteRestaurantBookmark(
                                "Bearer $token",
                                contentId!!.toInt()
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

                            Toast.makeText(applicationContext, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()

                        }
                    }

                    // > 길찾기 버튼 (구글 지도 연결.. 카카오 지도로 변경)
                    binding.btnMapShop.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir//${response.body()!!.data.name}")) //출발지 사용자가 선택하도록, 목적지만 해당 가게 이름으로 자동 입력
                        //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${response.body()!!.data.mapX},${response.body()!!.data.mapY}")) //지도가 이상한데 찍힘..
                        startActivity(intent)
                    }
                    // > 가게 주소 정보
                    binding.addShop.text = response.body()!!.data.add
                    // > 가게 전화번호
                    binding.numberShop.text = response.body()!!.data.tel
                    // > 가게 소개
                    binding.infoShop.text = response.body()!!.data.intro


                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getRestaurantDetail Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RestaurantDResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getRestaurantDetail)", t)
            }
        })

    }
}