package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ActivityMountainInfoBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.TrailResponse
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapView
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLngBounds
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.shape.MapPoints
import com.kakao.vectormap.shape.Polyline
import com.kakao.vectormap.shape.PolylineOptions
import com.kakao.vectormap.shape.PolylineStyle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MountainInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMountainInfoBinding
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // Intent에서 Mountain 데이터를 받아옴
        val mountain = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("mountain", Mountain::class.java)
        } else {
            intent.getParcelableExtra("mountain") as? Mountain
        }

        // 이미지 URL 수신
        val imageUrl = intent.getStringExtra("image_url")

        // Mountain 객체가 null인지, 예상된 데이터가 있는지
        if (mountain == null) {
            Log.e("MountainInfoActivity", "Mountain 객체 null")
        } else {
            Log.d("MountainInfoActivity", "Mountain 객체를 받음: $mountain")
            displayMountainInfo(mountain)

            // 이미지 URL을 사용하여 이미지 로드
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.etc_default_mountain) // 에러 시 이미지
                    .into(binding.mntImg)
            } else {
                // 이미지 URL이 비어 있을 경우 기본 이미지 설정
                binding.mntImg.setImageResource(R.drawable.etc_default_mountain)
            }

            // MapView 초기화
            mapView = MapView(this)
            binding.mntiTrailView.addView(mapView)

            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // 지도 API가 정상적으로 종료될 때 호출
                    Log.d("KakaoMap", "지도 정상 작동")
                }

                override fun onMapError(error: Exception) {
                    // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                    Log.e("KakaoMap", "지도 에러: ", error)
                }
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    // 정상적으로 인증이 완료되었을 때 호출
                    // KakaoMap 객체를 얻어 옵니다.
                    kakaoMap = map

                    if (token != null && mountain != null) {
                        fetchTrailData(token, mountain.mntiname, mountain.mntiadd)
                    }
                }
            })
        }
    }
    private fun fetchTrailData(token: String, mntiname: String?, mntiadd: String?) {
        val call = apiService.getTrail("Bearer $token", mntiname!!, mntiadd!!)

        // 호출될 URL을 로그로 출력
        Log.d("MountainInfoActivity", "Calling URL: ${call.request().url}")

        call.enqueue(object : Callback<TrailResponse> {
            override fun onResponse(call: Call<TrailResponse>, response: Response<TrailResponse>) {
                if (response.isSuccessful) {
                    val trailData = response.body()?.data
                    if (trailData != null) {
                        Log.d("MountainInfoActivity", "Trail data received: $trailData")
                        val trailPoints = trailData.map {
                            LatLng.from(it[1], it[0]) // LatLng 객체 생성 (위도, 경도)
                        }
                        drawPolyline(trailPoints)
                    } else {
                        Log.e("MountainInfoActivity", "Trail data is null. Full response: ${response.body()}")
                    }
                } else {
                    Log.e("MountainInfoActivity", "Response Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TrailResponse>, t: Throwable) {
                Log.e("MountainInfoActivity", "경로 데이터를 가져오는데 실패했습니다.", t)
            }
        })
    }

    private fun drawPolyline(trailPoints: List<LatLng>) {
        if (::kakaoMap.isInitialized) {
            // MapPoints 객체를 생성
            val mapPoints = MapPoints.fromLatLng(trailPoints)

            // PolylineOptions 객체 생성하고 스타일 설정
            val polylineOptions = PolylineOptions.from(
                mapPoints,
                PolylineStyle.from(3F, Color.parseColor("#FF0000")) // 두께 3, 빨간색
            )

            // Polyline 지도에 추가
            kakaoMap.getShapeManager()!!.getLayer().addPolyline(polylineOptions)

            // 경로의 중심과 범위를 계산
            val bounds = LatLngBounds.Builder()
            for (point in trailPoints) {
                bounds.include(point)
            }
            val latLngBounds = bounds.build()

            // 카메라를 경로가 화면에 잘 보이도록 이동
            kakaoMap.moveCamera(CameraUpdateFactory.fitMapPoints(latLngBounds, 100))
        } else {
            Log.e("MountainInfoActivity", "KakaoMap 객체가 초기화되지 않았습니다.")
        }
    }

    private fun displayMountainInfo(mountain: Mountain) {
        // 산의 상세 정보를 표시
        binding.mountainTitle.text = mountain!!.mntiname
        binding.mountainComment.text = mountain!!.mntidetails
        binding.mountainLocation.text = mountain!!.mntiadd
        binding.mountainHeight.text = "${mountain!!.mntihigh} m"
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.finish()
    }
}