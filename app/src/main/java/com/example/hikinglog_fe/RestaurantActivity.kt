package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.RestaurantAdapter
import com.example.hikinglog_fe.databinding.ActivityRestaurantBinding
import com.example.hikinglog_fe.models.RestaurantLResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.Manifest
import android.location.Location
import android.widget.Toast
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.models.AccommodationLResponse

class RestaurantActivity : AppCompatActivity() {
    lateinit var binding : ActivityRestaurantBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [[SharedPreferences 초기화]]
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // [[FusedLocationProviderClient 초기화]]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // [[위치 권한 확인]]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) { // 위치 권한이 허용된 경우 현재 위치를 가져옴
            getCurrentLocation(token)
        } else { // 위치 권한 요청
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }

        binding.btnSearch.setOnClickListener {
            val keyword = binding.searchEditText.text.toString()
            if (keyword.isNotEmpty()) {
                searchRestaurants(keyword, token)
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    } //onCreate()

    private fun searchRestaurants(keyword: String, token: String?) {
        // [Retrofit 통신 요청: 숙소 목록 검색]
        val call: Call<RestaurantLResponse> = RetrofitConnection.jsonNetServ.searchRestaurant(
            "Bearer $token",
            keyword
        )

        // [Retrofit 통신 응답: 숙소 목록 검색]
        call.enqueue(object : Callback<RestaurantLResponse> {
            override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getAccommodationList: $response")

                    // <리사이클러뷰에 표시>
                    binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.restaurantRecyclerView.adapter = RestaurantAdapter(this@RestaurantActivity, response.body()!!.data, token)
                    binding.restaurantRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getAccommodationList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getAccommodationList)", t)
            }
        })
    }

    private fun getCurrentLocation(token: String?) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) { // 권한이 있는 경우에만 위치를 가져옴
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("mobileApp", "현재 사용자 위치 정보: $latitude, $longitude")

                        // Retrofit 통신 요청: 음식점 목록
                        requestRestaurantList(token, latitude, longitude)
                    } else {
                        Log.e("mobileApp", "Location is null")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("mobileApp", "Failed to get location", exception)
                }
        } else { // 권한이 없는 경우
            Log.e("mobileApp", "Location permission not granted")
        }
    }


    private fun requestRestaurantList(token: String?, latitude: Double, longitude: Double) {
        val call: Call<RestaurantLResponse> = RetrofitConnection.jsonNetServ.getRestaurantList(
            "Bearer $token",
            longitude,
            latitude
        )

        call.enqueue(object : Callback<RestaurantLResponse> {
            override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getRestaurantList: $response")

                    // 리사이클러뷰에 표시
                    binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.restaurantRecyclerView.adapter = RestaurantAdapter(this@RestaurantActivity, response.body()!!.data, token)
                    binding.restaurantRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    Log.e("mobileApp", "getRestaurantList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                Log.e("mobileApp", "Failed to fetch data(getRestaurantList)", t)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용된 경우 현재 위치를 가져옴
                val token = sharedPreferences.getString("token", null)
                getCurrentLocation(token)
            } else {
                Log.e("mobileApp", "Location permission denied")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}