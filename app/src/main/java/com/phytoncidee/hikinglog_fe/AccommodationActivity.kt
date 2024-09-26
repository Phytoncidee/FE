package com.phytoncidee.hikinglog_fe

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.phytoncidee.hikinglog_fe.adapter.AccommodationAdapter
import com.phytoncidee.hikinglog_fe.databinding.ActivityAccommodationBinding
import com.phytoncidee.hikinglog_fe.models.AccommodationLResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.phytoncidee.hikinglog_fe.adapter.PostRestaurantAdapter
import com.phytoncidee.hikinglog_fe.adapter.PostTourspotAdapter
import com.phytoncidee.hikinglog_fe.adapter.PreRestaurantAdapter
import com.phytoncidee.hikinglog_fe.adapter.PreTourspotAdapter
import com.phytoncidee.hikinglog_fe.models.RestaurantLResponse
import com.phytoncidee.hikinglog_fe.models.TourismLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccommodationActivity : AppCompatActivity() {
    lateinit var binding : ActivityAccommodationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccommodationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [[SharedPreferences 초기화]
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)!!

        // [[FusedLocationProviderClient 초기화]]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // [[음식점 목록 반환 기준 위치 선택 버튼]] (공모전 마감 임박 이슈로 사용자 위치 정보 사용 안 하는 쪽으로 수정)
        binding.BtnMap.setOnClickListener {
            // 위치 선택 지도 액티비티
            val intent = Intent(this, MapLocationActivity::class.java)
            intent.putExtra("latitude", 37.6) // 지도 기준 위치_서울
            intent.putExtra("longitude", 127)
            startActivityForResult(intent, AccommodationActivity.REQUEST_LOCATION)
        }

        // [[ 숙박시설 검색 버튼]]
        binding.btnSearch.setOnClickListener {
            val keyword = binding.searchEditText.text.toString()
            if (keyword.isNotEmpty()) {
                // <로딩 시작>
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()

                searchAccommodations(keyword, token)
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    } //onCreate()


    // [[숙박 시설 검색 함수]]
    private fun searchAccommodations(keyword: String, token: String?) {
        // [Retrofit 통신 요청: 숙소 목록 검색]
        val call: Call<AccommodationLResponse> = RetrofitConnection.jsonNetServ.searchStay(
            "Bearer $token",
            keyword
        )

        // [Retrofit 통신 응답: 숙소 목록 검색]
        call.enqueue(object : Callback<AccommodationLResponse> {
            override fun onResponse(call: Call<AccommodationLResponse>, response: Response<AccommodationLResponse>) {
                if (response.isSuccessful) {
                    // <로딩 종료>
                    binding.lottieAnimationView.cancelAnimation()
                    binding.lottieAnimationView.visibility = View.GONE

                    Log.d("mobileApp", "searchStay: $response")

                    // <리사이클러뷰에 표시>
                    binding.accommodationRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.accommodationRecyclerView.adapter = AccommodationAdapter(this@AccommodationActivity, response.body()!!.data, token)
                    binding.accommodationRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "searchStay Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AccommodationLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(searchStay)", t)
            }
        })
    }


    // [[MapLocationActivity에서 받은 위도 경도 사용]]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)

            when (requestCode) {
                REQUEST_LOCATION -> {
                    Log.d("mobileApp", "숙박 목록 기준 위치: latitude: $latitude, longitude: $longitude")

                    // <로딩 시작>
                    binding.lottieAnimationView.visibility = View.VISIBLE
                    binding.lottieAnimationView.playAnimation()

                    if (latitude != null && longitude != null) {
                        requestAccommodationList(token, latitude, longitude) //지도에서 받아온 위치로 음식점 목록 요청
                    }

                }
            }
        }
    }

    // [[ 숙박시설 목록 요청 함수]]
    private fun requestAccommodationList(token: String?, latitude: Double, longitude: Double) {

        // [Retrofit 통신 요청: 숙박시설 목록]
        val call: Call<AccommodationLResponse> = RetrofitConnection.jsonNetServ.getAccommodationList(
            "Bearer $token",
            longitude,
            latitude
        )

        // [Retrofit 통신 응답: 숙박시설 목록]
        call.enqueue(object : Callback<AccommodationLResponse> {
            override fun onResponse(call: Call<AccommodationLResponse>, response: Response<AccommodationLResponse>) {

                if (response.isSuccessful) {
                    // <로딩 종료>
                    binding.lottieAnimationView.cancelAnimation()
                    binding.lottieAnimationView.visibility = View.GONE

                    Log.d("mobileApp", "getAccommodationList: $response")

                    // <리사이클러뷰에 표시>
                    binding.accommodationRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.accommodationRecyclerView.adapter = AccommodationAdapter(this@AccommodationActivity, response.body()!!.data, token)
                    binding.accommodationRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getAccommodationList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AccommodationLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getAccommodationList)", t)
            }
        })
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_LOCATION = 1 // 지도 요청 관련
    }
}


// [[위치 권한 확인]]
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) { // 위치 권한이 허용된 경우 현재 위치를 가져옴
//
//            getCurrentLocation(token)
//        } else { // 위치 권한 요청
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }

// [[ 사용자 위치 함수 ]]
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // 권한이 허용된 경우 현재 위치를 가져옴
//                val token = sharedPreferences.getString("token", null)
//                getCurrentLocation(token)
//            } else {
//                Log.e("mobileApp", "Location permission denied")
//            }
//        }
//    }

// [[사용자 현재 위치 get 함수 ]]
//    private fun getCurrentLocation(token: String?) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) { // 권한이 있는 경우에만 위치를 가져옴
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    if (location != null) {
//                        val latitude = location.latitude
//                        val longitude = location.longitude
//                        Log.d("mobileApp", "현재 사용자 위치 정보: $latitude, $longitude")
//
//                        // Retrofit 통신 요청: 음식점 목록
//
//                        requestAccommodationList(token, latitude, longitude)
//                    } else {
//                        Log.e("mobileApp", "Location is null")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("mobileApp", "Failed to get location", exception)
//                }
//        } else { // 권한이 없는 경우
//            Log.e("mobileApp", "Location permission not granted")
//        }
//    }