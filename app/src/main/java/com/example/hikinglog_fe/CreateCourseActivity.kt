package com.example.hikinglog_fe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.PostRestaurantAdapter
import com.example.hikinglog_fe.adapter.PostTourspotAdapter
import com.example.hikinglog_fe.adapter.PreRestaurantAdapter
import com.example.hikinglog_fe.adapter.PreTourspotAdapter
import com.example.hikinglog_fe.databinding.ActivityCreateCourseBinding
import com.example.hikinglog_fe.interfaces.OnDataPassListener
import com.example.hikinglog_fe.models.CourseSaveDTO
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.ProfileGetResponse
import com.example.hikinglog_fe.models.Restaurant
import com.example.hikinglog_fe.models.RestaurantDetail
import com.example.hikinglog_fe.models.RestaurantLResponse
import com.example.hikinglog_fe.models.TourSpot
import com.example.hikinglog_fe.models.TourismLResponse
import com.example.hikinglog_fe.models.TourspotDetail
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CreateCourseActivity : AppCompatActivity(), OnDataPassListener {
    private lateinit var binding: ActivityCreateCourseBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token : String
    private lateinit var PreTourspot: TourSpot
    private lateinit var PostTourspot: TourSpot
    private lateinit var PreRestaurant: Restaurant
    private lateinit var PostRestaurant: Restaurant
    private lateinit var jsonData : JSONObject
    private lateinit var courseName: String
    private lateinit var mountain: Mountain
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // [[SharedPreferences 초기화]]
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "")!!

        // [[Intent에서 Mountain 데이터를 받아옴]]
        mountain = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("mountain", Mountain::class.java)!!
        } else {
            (intent.getParcelableExtra("mountain") as? Mountain)!!
        }
        Log.d("mobileApp", "CreateCourseActivity Intent: ${mountain}")

        // >> 현재 로그인한 사용자 프로필 조회 -> userid 얻어오기
        val call: Call<ProfileGetResponse> = RetrofitConnection.jsonNetServ.getMyProfile(
            "Bearer $token"
        )
        call.enqueue(object : Callback<ProfileGetResponse> {
            override fun onResponse(call: Call<ProfileGetResponse>, response: Response<ProfileGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getMyProfile: $response")
                    userId = response.body()!!.data.userid
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getMyProfile: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ProfileGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMyProfile)", t)
            }
        })


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

        jsonData = JSONObject()
        // [[코스 저장]]
        binding.BtnSaveCourse.setOnClickListener {
            // >> 받은 데이터를 처리
            createJsonData(PreTourspot, PostTourspot, PreRestaurant, PostRestaurant)
            // [코스 저장]
            // [Retrofit 통신 요청: 마이관광 저장]
            Log.d("mobileApp", "token 확인: $token")
            Log.d("mobileApp", "jsonData 확인: $jsonData")
            val call: Call<List<String>> = RetrofitConnection.jsonNetServ.saveCourse(
                "Bearer $token",
                jsonData
            )

            // [Retrofit 통신 응답: 마이관광 저장]
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        Log.d("mobileApp", "saveCourse: $response")
                    } else {
                        // 오류 처리
                        Log.e("mobileApp", "saveCourse Error: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(saveCourse)", t)
                }
            })

            // [메인 페이지로 이동]
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            true
        }
    } //onCreate()

    override fun preRestaurantToActivity(preRestaurant: Restaurant) {
        PreRestaurant = preRestaurant
    }
    override fun postRestaurantToActivity(postrestaurant: Restaurant) {
        PostRestaurant = postrestaurant
    }
    override fun preTourspotToActivity(preTourspot: TourSpot) {
        PreTourspot = preTourspot
    }
    override fun postTourspotToActivity(postTourspot: TourSpot) {
        PostTourspot = postTourspot
    }

    private fun createJsonData(preTourspot: TourSpot?, postTourspot: TourSpot?, prerestaurant: Restaurant?, postrestaurant: Restaurant?){
        Log.d("mobileApp", "createJsonData()에서 받은 data 확인: ${preTourspot}, ${postTourspot}, ${prerestaurant}, ${postrestaurant}")
        // 기본 정보 채우기
        if (userId == 0){
            Log.d("mobileApp", "사용자 userId 조회 실패")
        } else {
            jsonData.put("userId", userId)
        }
        jsonData.put("userId", userId)
        jsonData.put("tourTitle", courseName)
        jsonData.put("mountainId", mountain.mntilistno)

        // 선택된 tour와 restaurant의 id 추가
        val preHikeTourIds = JSONArray()
        preTourspot?.let {
            preHikeTourIds.put(preTourspot.contentId.toString())
        }
        jsonData.put("preHikeTourIds", preHikeTourIds)

        val postHikeTourIds = JSONArray()
        postTourspot?.let {
            postHikeTourIds.put(postTourspot.contentId.toString())
        }
        jsonData.put("postHikeTourIds", postHikeTourIds)

        val preHikeRestaurantIds = JSONArray()
        prerestaurant?.let {
            preHikeRestaurantIds.put(prerestaurant.contentId.toString())
        }
        jsonData.put("preHikeRestaurantIds", preHikeRestaurantIds)

        val postHikeRestaurantIds = JSONArray()
        postrestaurant?.let {
            postHikeRestaurantIds.put(postrestaurant.contentId.toString())
        }
        jsonData.put("postHikeRestaurantIds", postHikeRestaurantIds)

        // 선택된 tour와 restaurant 정보 추가
        val tourDetails = JSONArray()
        preTourspot?.let {
            val tourDetail = JSONObject().apply {
                put("name", it.name)
                put("contentId", it.contentId.toString())
                put("add", it.add)
                put("img", it.img)
                put("img2", it.img2)
                put("mapX", it.mapX.toString())
                put("mapY", it.mapY.toString())
                put("tel", it.tel)
            }
            tourDetails.put(tourDetail)
        }
        postTourspot?.let {
            val tourDetail = JSONObject().apply {
                put("name", it.name)
                put("contentId", it.contentId.toString())
                put("add", it.add)
                put("img", it.img)
                put("img2", it.img2)
                put("mapX", it.mapX.toString())
                put("mapY", it.mapY.toString())
                put("tel", it.tel)
            }
            tourDetails.put(tourDetail)
        }

        val restaurantDetails = JSONArray()
        prerestaurant?.let {
            val restaurantDetail = JSONObject().apply {
                put("name", it.name)
                put("contentId", it.contentId.toString())
                put("add", it.add)
                put("img", it.img)
                put("mapX", it.mapX.toString())
                put("mapY", it.mapY.toString())
                put("tel", it.tel)
                put("intro", "")

            }
            restaurantDetails.put(restaurantDetail)
        }
        postrestaurant?.let {
            val restaurantDetail = JSONObject().apply {
                put("name", it.name)
                put("contentId", it.contentId.toString())
                put("add", it.add)
                put("img", it.img)
                put("mapX", it.mapX.toString())
                put("mapY", it.mapY.toString())
                put("tel", it.tel)
                put("intro", "")
            }
            restaurantDetails.put(restaurantDetail)
        }
        jsonData.put("tourDetails", tourDetails)
        jsonData.put("restaurantDetails", restaurantDetails)
    }



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
                                Log.d("mobileApp", "getPreRestaurantList: $response")

                                // <리사이클러뷰에 표시>
                                binding.befHFoodRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.befHFoodRecyclerView.adapter = PreRestaurantAdapter(this@CreateCourseActivity,this@CreateCourseActivity, response.body()!!.data, token)
                                binding.befHFoodRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getPreRestaurantList Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getPreRestaurantList)", t)
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
                                binding.befHTourismRecyclerView.adapter = PreTourspotAdapter(this@CreateCourseActivity, this@CreateCourseActivity, response.body()!!.data)
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
                    )

                    // [Retrofit 통신 응답: 음식점 목록]
                    call3.enqueue(object : Callback<RestaurantLResponse> {
                        override fun onResponse(call: Call<RestaurantLResponse>, response: Response<RestaurantLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getPostRestaurantList: $response")

                                // <리사이클러뷰에 표시>
                                binding.aftHFoodRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.aftHFoodRecyclerView.adapter = PostRestaurantAdapter(this@CreateCourseActivity, this@CreateCourseActivity,response.body()!!.data, token)
                                binding.aftHFoodRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "getPostRestaurantList Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<RestaurantLResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getPostRestaurantList)", t)
                        }
                    })


                    // [[하산 후 관광지]]
                    // [Retrofit 통신 요청: 음식점 목록]
                    // [Retrofit 통신 요청: 관광지 목록]
                    val call4: Call<TourismLResponse> = RetrofitConnection.jsonNetServ.getTourpList(
                        "Bearer $token",
                        longitude,
                        latitude
                    )

                    // [Retrofit 통신 응답: 관광지 목록]
                    call4.enqueue(object : Callback<TourismLResponse> {
                        override fun onResponse(call: Call<TourismLResponse>, response: Response<TourismLResponse>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getTourpList2: $response")

                                // <리사이클러뷰에 표시>
                                binding.aftHTourismRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                                binding.aftHTourismRecyclerView.adapter = PostTourspotAdapter(this@CreateCourseActivity,this@CreateCourseActivity, response.body()!!.data)
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