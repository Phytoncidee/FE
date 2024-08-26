package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.databinding.ActivityAccommodationBinding
import com.example.hikinglog_fe.databinding.ActivityAccommodationDetailBinding
import com.example.hikinglog_fe.models.AccommodationBookmarkDeleteResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkGetResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkPostResponse
import com.example.hikinglog_fe.models.AccommodationDResponse
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.PostAccommodationBMDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccommodationDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityAccommodationDetailBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccommodationDetailBinding.inflate(layoutInflater)
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

        // [Retrofit 통신 요청: 등산용품점 상세]
        val call: Call<AccommodationDResponse> = RetrofitConnection.jsonNetServ.getAccommodationDetail(
            "Bearer $token",
            contentId!!.toInt()
        )

        // [Retrofit 통신 응답: 등산용품점 상세]
        call.enqueue(object : Callback<AccommodationDResponse> {
            override fun onResponse(call: Call<AccommodationDResponse>, response: Response<AccommodationDResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationDetail: $response")

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

                    // >> 숙박시설 즐겨찾기
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
                                Log.e("mobileApp", "getAccommodationBookmark: ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<AccommodationBookmarkGetResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getAccommodationBookmark)", t)
                        }
                    })

//        // [즐겨찾기 등록/삭제 -> 표시]
                    binding.btnBookmarkShop.setOnClickListener {

                        if(isBookmarked == false){ // 미등록

                            binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_on)
                            isBookmarked = true

                            // <즐겨찾기 등록>
                            val newAccommodationBM = PostAccommodationBMDTO(name = response.body()!!.data.name, location = response.body()!!.data.add, phone = response.body()!!.data.tel, image = response.body()!!.data.img)

                            val callB: Call<AccommodationBookmarkPostResponse> = RetrofitConnection.jsonNetServ.postAccommodationBookmark(
                                "Bearer $token",
                                contentId!!.toInt(),
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
                            Toast.makeText(applicationContext, "즐겨찾기 등록", Toast.LENGTH_SHORT).show()

                        }
                        else { // 등록

                            binding.btnBookmarkShop.setImageResource(android.R.drawable.btn_star_big_off)
                            isBookmarked = false

                            // <즐겨찾기 삭제>
                            val callB: Call<AccommodationBookmarkDeleteResponse> = RetrofitConnection.jsonNetServ.deleteAccommodationBookmark(
                                "Bearer $token",
                                contentId!!.toInt()
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

                            Toast.makeText(applicationContext, "즐겨찾기 삭제", Toast.LENGTH_SHORT).show()

                        }
                    }

                    // > 길찾기 버튼 (구글 지도 연결.. 카카오 지도로 변경)
                    binding.btnMapShop.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/덕성여자대학교/${response.body()!!.data.name}")) //출발지 어떻게 넣느냐가 관건
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
                    Log.e("mobileApp", "getAccommodationDetail Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AccommodationDResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getAccommodationDetail)", t)
            }
        })

    }
}