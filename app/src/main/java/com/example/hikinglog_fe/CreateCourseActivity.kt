package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.adapter.RestaurantAdapter
import com.example.hikinglog_fe.adapter.RestaurantAdapter2
import com.example.hikinglog_fe.adapter.TourspotAdapter
import com.example.hikinglog_fe.databinding.ActivityCreateCourseBinding
import com.example.hikinglog_fe.databinding.ActivityMountainInfoBinding
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.RestaurantLResponse
import com.example.hikinglog_fe.models.TourismLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CreateCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCourseBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // [[SharedPreferences 초기화]]
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "")!!

        // [[Intent에서 Mountain 데이터를 받아옴]]
        val mountain = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("mountain", Mountain::class.java)
        } else {
            intent.getParcelableExtra("mountain") as? Mountain
        }

        Log.d("mobileApp", "CreateCourseActivity Intent: ${mountain}")


        // [[입산 전 위치 선택 버튼 클릭 -> 지도로 이동하여 위치 선택]]
        binding.befHLocation.setOnClickListener {
            val address = mountain!!.mntiadd

            if (address != null) {
                val location = getLocationFromAddress(address)

                if (location != null) {
                    val (latitude, longitude) = location

                    val intent = Intent(this, MapLocationActivity::class.java)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivityForResult(intent, REQUEST_LOCATION)
                } else {
                    Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "주소 정보가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }


        // [[하산 후 위치 선택 버튼 클릭 -> 지도로 이동하여 위치 선택]]
        binding.aftHLocation.setOnClickListener {
            val address = mountain?.mntiadd

            if (address != null) {
                val location = getLocationFromAddress(address)

                if (location != null) {
                    val (latitude, longitude) = location

                    val intent = Intent(this, MapLocationActivity::class.java)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivityForResult(intent, REQUEST_LOCATION_AFTER)
                } else {
                    Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "주소 정보가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }


        // [[입력받은 코스명 저장]]
        var courseName: String = ""
        binding.courseName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { // 텍스트가 변경된 후 실행
                courseName = s.toString()
                Log.d("mobileApp", "getCourseName: $courseName")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 실행
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경되는 동안 실행
            }
        })


        // [[코스 저장]]
        binding.BtnSaveCourse.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            true
        }
    } //onCreate()



    // [[MapLocationActivity에서 받은 위도 경도 사용]]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)

            when (requestCode) {
                REQUEST_LOCATION -> { // >> 입산 전 위치에 대한 처리
                    Log.d("mobileApp", "입산 전 위치: latitude: $latitude, longitude: $longitude")

                    // [[입산 전 음식점]]
                    // [Retrofit 통신 요청: 음식점 목록]
                    val call: Call<RestaurantLResponse> = RetrofitConnection.jsonNetServ.getRestaurantList(
                        "Bearer $token",
                        longitude,
                        latitude
                    )

                    // [Retrofit 통신 응답: 음식점 목록]
                    call.enqueue(object : Callback<RestaurantLResponse> {
                        override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getRestaurantList1: $response")

                                // <리사이클러뷰에 표시>
                                binding.befHFoodRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.befHFoodRecyclerView.adapter = RestaurantAdapter2(this@CreateCourseActivity, response.body()!!.data, token)
                                binding.befHFoodRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getRestaurantList Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getRestaurantList)", t)
                        }
                    })


                    // [[입산 전 관광지]]
                    // [Retrofit 통신 요청: 관광지 목록]
                    val call2: Call<TourismLResponse> = RetrofitConnection.jsonNetServ.getTourpList(
                        "Bearer $token",
                        longitude,
                        latitude
                    )

                    // [Retrofit 통신 응답: 관광지 목록]
                    call2.enqueue(object : Callback<TourismLResponse> {
                        override fun onResponse(call: Call<TourismLResponse>, response: Response<TourismLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getTourpList: $response")

                                // <리사이클러뷰에 표시>
                                binding.befHTourismRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.befHTourismRecyclerView.adapter = TourspotAdapter(this@CreateCourseActivity, response.body()!!.data)
                                binding.befHTourismRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getTourpList Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<TourismLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getTourpList)", t)
                        }
                    })
                }

                REQUEST_LOCATION_AFTER -> { // >> 하산 후 위치에 대한 처리
                    Log.d("mobileApp", "하산 후 위치: latitude: $latitude, longitude: $longitude")

                    // [[하산 후 음식점]]
                    // [Retrofit 통신 요청: 음식점 목록]
                    val call3: Call<RestaurantLResponse> = RetrofitConnection.jsonNetServ.getRestaurantList(
                        "Bearer $token",
                        longitude,
                        latitude
                        // 산에 맞게 위도, 경도 받아서 넘겨주는 처리 필요
                    )

                    // [Retrofit 통신 응답: 음식점 목록]
                    call3.enqueue(object : Callback<RestaurantLResponse> {
                        override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getRestaurantList2: $response")

                                // <리사이클러뷰에 표시>
                                binding.aftHFoodRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.aftHFoodRecyclerView.adapter = RestaurantAdapter2(this@CreateCourseActivity, response.body()!!.data, token)
                                binding.aftHFoodRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getRestaurantList2 Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getRestaurantList2)", t)
                        }
                    })


                    // [[하산 후 관광지]]
                    // [Retrofit 통신 요청: 음식점 목록]
                    // [Retrofit 통신 요청: 관광지 목록]
                    val call4: Call<TourismLResponse> = RetrofitConnection.jsonNetServ.getTourpList(
                        "Bearer $token",
                        longitude,
                        latitude
                        // 현재 위도, 경도 받아서 넘겨주는 처리 필요
                    )

                    // [Retrofit 통신 응답: 관광지 목록]
                    call4.enqueue(object : Callback<TourismLResponse> {
                        override fun onResponse(call: Call<TourismLResponse>, response: Response<TourismLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getTourpList2: $response")

                                // <리사이클러뷰에 표시>
                                binding.aftHTourismRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.aftHTourismRecyclerView.adapter = TourspotAdapter(this@CreateCourseActivity, response.body()!!.data)
                                binding.aftHTourismRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getTourpList2 Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<TourismLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getTourpList2)", t)
                        }
                    })
                }
            }
        }
    }

    // [[주소를 위도와 경도로 변환하는 함수]]
    private fun getLocationFromAddress(address: String): Pair<Double, Double>? {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList.isNullOrEmpty()) {
                null
            } else {
                val location = addressList[0]
                Pair(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    companion object {
        private const val REQUEST_LOCATION = 1
        private const val REQUEST_LOCATION_AFTER = 2
    }
}