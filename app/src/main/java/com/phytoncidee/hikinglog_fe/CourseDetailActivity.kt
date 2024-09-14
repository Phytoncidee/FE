package com.phytoncidee.hikinglog_fe

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.phytoncidee.hikinglog_fe.adapter.TourDRestaurantAdapter
import com.phytoncidee.hikinglog_fe.adapter.TourDTourspotAdapter
import com.phytoncidee.hikinglog_fe.databinding.ActivityCourseDetailBinding
import com.phytoncidee.hikinglog_fe.models.Mountain
import com.phytoncidee.hikinglog_fe.models.MountainDetailResponse
import com.phytoncidee.hikinglog_fe.models.MyTourDResponse
import com.phytoncidee.hikinglog_fe.models.NationalMountainsImageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCourseDetailBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private var imageUrl: String? = null
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
                    // >> 산 상세 정보 얻어오기
                    // [Retrofit 통신 요청: 산 상세]
                    val call: Call<MountainDetailResponse> = RetrofitConnection.jsonNetServ.getMountainDetail(
                        "Bearer $token",
                        response.body()!!.mountainName,
                        response.body()!!.mountainId.toString()
                    )
                    // [Retrofit 통신 응답: 산 상세]
                    call.enqueue(object : Callback<MountainDetailResponse> {
                        override fun onResponse(call: Call<MountainDetailResponse>, response: Response<MountainDetailResponse>) {
                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getMountainDetail: $response")
                                val data = response.body()!!.data
                                val mountain = Mountain(
                                    mntiadd = data.mntiadd,
                                    mntiadmin = data.mntiadmin,
                                    mntiadminnum = data.mntiadminnum,
                                    mntidetails = data.mntidetails,
                                    mntihigh = data.mntihigh,
                                    mntilistno = data.mntilistno,
                                    mntiname = data.mntiname,
                                    mntinfdt = data.mntinfdt,
                                    mntisname = data.mntisname,
                                    mntisummary = data.mntisummary,
                                    mntitop = data.mntitop
                                )
                                // >> 가져온 산 정보 넣어주기
                                // [Retrofit 통신 요청: 산 이미지]
                                val call: Call<NationalMountainsImageResponse> = RetrofitConnection.jsonNetServ.getMountainImage(
                                    "Bearer $token",
                                    mountain.mntilistno.toString()
                                )
                                // [Retrofit 통신 응답: 산 이미지]
                                call.enqueue(object : Callback<NationalMountainsImageResponse> {
                                    override fun onResponse(call: Call<NationalMountainsImageResponse>, response: Response<NationalMountainsImageResponse>) {
                                        if (response.isSuccessful) {
                                            val responseBody = response.body()
                                            if (responseBody != null) {
                                                val images = responseBody.response?.body?.items?.item
                                                if (!images.isNullOrEmpty()) {
                                                    // 이미지 URL을 설정
                                                    imageUrl = "https://www.forest.go.kr/images/data/down/mountain/${images[0].imgfilename}"
                                                    Glide.with(binding.root.context)
                                                        .load(imageUrl)
                                                        .error(R.drawable.etc_default_mountain) // 로드 실패 시 이미지
                                                        .into(binding.mntImg)
                                                }
                                            }
                                        } else {
                                            // 오류 처리
                                            Log.e("mobileApp", "getMountainDetail Error: ${response.code()}")
                                        }
                                    }
                                    override fun onFailure(call: Call<NationalMountainsImageResponse>, t: Throwable) {
                                        // 네트워크 오류 처리
                                        Log.e("mobileApp", "Failed to fetch data(getMountainDetail)", t)
                                    }
                                })
                                binding.mntAdd.text = mountain.mntiadd
                                binding.mntHigh.text = "${mountain.mntihigh}m"

                                // 실시간 등산기록
                                binding.BtnRecordNow.setOnClickListener {
                                    val context = binding.root.context
                                    val intent = Intent(context, LiveRecordActivity::class.java).apply{
                                        putExtra("name", mountain!!.mntiname)
                                        putExtra("number", mountain!!.mntilistno)
                                    }
                                    startActivity(intent)
                                }
                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getMountainDetail Error: ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<MountainDetailResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getMountainDetail)", t)
                        }
                    })

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