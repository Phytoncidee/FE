package com.example.hikinglog_fe

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.CommentAdapter
import com.example.hikinglog_fe.adapter.MyToursAdapter
import com.example.hikinglog_fe.adapter.RestaurantAdapter
import com.example.hikinglog_fe.adapter.TourDRestaurantAdapter
import com.example.hikinglog_fe.adapter.TourDTourspotAdapter
import com.example.hikinglog_fe.databinding.ActivityAccommodationDetailBinding
import com.example.hikinglog_fe.databinding.ActivityCourseDetailBinding
import com.example.hikinglog_fe.models.CommentsGetResponse
import com.example.hikinglog_fe.models.MyTourDResponse
import com.example.hikinglog_fe.models.MyTourLResponse
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCourseDetailBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)!!

        // 리사이클러 뷰에서 가게 tourId 받아오기
        val tourId: Int? = intent.getIntExtra("tourId", -1)

        // [Retrofit 통신 요청: 마이 관광 상세]
        val call: Call<MyTourDResponse> = RetrofitConnection.jsonNetServ.getMyTourDetail(
            "Bearer $token",
            tourId
        )
        // [Retrofit 통신 응답: 마이 관광 상세]
        call.enqueue(object : Callback<MyTourDResponse> {
            override fun onResponse(call: Call<MyTourDResponse>, response: Response<MyTourDResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getMyTours: ${response}")

                    // >> 관광 코스 이름
                    binding.titleMyTour.text = response.body()!!.tourTitle

                    // <<입산 전 음식점, 관광지 리사이클러뷰에 표시>>
                    binding.befRestaurantRecyclerView.layoutManager = LinearLayoutManager(this@CourseDetailActivity)
                    binding.befRestaurantRecyclerView.adapter = TourDRestaurantAdapter(this@CourseDetailActivity, response.body()!!.preHikeRestaurant, token)
                    binding.befRestaurantRecyclerView.addItemDecoration(DividerItemDecoration(this@CourseDetailActivity, LinearLayoutManager.VERTICAL))

                    binding.befTourRecyclerView.layoutManager = LinearLayoutManager(this@CourseDetailActivity)
                    binding.befTourRecyclerView.adapter = TourDTourspotAdapter(this@CourseDetailActivity, response.body()!!.preHikeTour, token)
                    binding.befTourRecyclerView.addItemDecoration(DividerItemDecoration(this@CourseDetailActivity, LinearLayoutManager.VERTICAL))

                    // <<산 정보 표시>> (사진, 소재지, 높이, 실시간 등산 기록)

                    
                    // <<하산 후 음식점, 관광지 리사이클러뷰에 표시>>
                    binding.aftRestaurantRecyclerView.layoutManager = LinearLayoutManager(this@CourseDetailActivity)
                    binding.aftRestaurantRecyclerView.adapter = TourDRestaurantAdapter(this@CourseDetailActivity, response.body()!!.postHikeRestaurant, token)
                    binding.aftRestaurantRecyclerView.addItemDecoration(DividerItemDecoration(this@CourseDetailActivity, LinearLayoutManager.VERTICAL))

                    binding.aftTourRecyclerView.layoutManager = LinearLayoutManager(this@CourseDetailActivity)
                    binding.aftTourRecyclerView.adapter = TourDTourspotAdapter(this@CourseDetailActivity, response.body()!!.postHikeTour, token)
                    binding.aftTourRecyclerView.addItemDecoration(DividerItemDecoration(this@CourseDetailActivity, LinearLayoutManager.VERTICAL))

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("mobileApp", "getMyTours Error: ${response.code()}, Error Body: ${errorBody}")
                }
            }

            override fun onFailure(call: Call<MyTourDResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMyTours)", t)
            }
        })


        // [[마이 관광 삭제]]
        binding.BtnDelete.setOnClickListener {
            val eventHandler = object: DialogInterface.OnClickListener{ // 다이얼로그에 속하는 버튼에 쓸 클릭 리스너.. 변수로 받아 여러 버튼에 대해 사용
                override fun onClick(dialog: DialogInterface?, which: Int) { //which에 어떤 버튼 눌렸는지 저장
                    if (which == DialogInterface.BUTTON_POSITIVE) { //positive 버튼 누른 경우
                        // >> 삭제
                        // [Retrofit 통신 요청: 마이 관광 삭제]
                        val call: Call<String> = RetrofitConnection.jsonNetServ.deleteMyTour(
                            "Bearer $token",
                            tourId
                        )
                        // [Retrofit 통신 응답: 마이 관광 삭제]
                        call.enqueue(object : Callback<String> {
                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                if (response.isSuccessful) {
                                    Log.d("mobileApp", "deleteMyTour: $response")
                                    // 삭제 후 메인으로 돌아가기
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                    true
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    Log.e("mobileApp", "deleteMyTour Error: ${response.code()}, Error Body: ${errorBody}")
                                }
                            }
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                // 네트워크 오류 처리
                                Log.e("mobileApp", "Failed to fetch data(deleteMyTour)", t)
                            }
                        }) // 삭제 <<
                    }
                    else if (which == DialogInterface.BUTTON_NEGATIVE) { //negative 버튼 누른 경우
                        Log.d("mobileapp", "댓글 삭제 취소") //그냥 다이얼로그만 닫음
                    }
                }
            }
            AlertDialog.Builder(this).run(){
                setTitle("마이 관광 삭제 확인")
                setIcon(android.R.drawable.ic_dialog_alert)

                setMessage("정말로 삭제하시겠습니까?")

                setPositiveButton("예", eventHandler)
                setNegativeButton("아니오", eventHandler)

                show()
            }
        }
    }
}