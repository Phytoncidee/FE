package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ActivityAccommodationDetailBinding
import com.example.hikinglog_fe.databinding.ActivityRestaurantDetailBinding
import com.example.hikinglog_fe.models.AccommodationDResponse
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

        // [Retrofit 통신 요청: 등산용품점 상세]
        val call: Call<RestaurantDResponse> = RetrofitConnection.jsonNetServ.getRestaurantDetail(
            "Bearer $token",
            contentId!!.toInt()
        )

        // [Retrofit 통신 응답: 등산용품점 상세]
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

                    // > 즐겨찾기 버튼 (////즐겨찾기 통신 성공 후 다시 도전/////)

                    // > 길찾기 버튼 (구글 지도 연결.. 카카오 지도로 변경)
                    binding.btnMapShop.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/덕성여자대학교/${response.body()!!.data.name}")) //출발지 어떻게 넣느냐가 관건, 근데 왜 길을 못 찾냐.. 좌표로 넣어야 하나
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