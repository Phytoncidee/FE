package com.phytoncidee.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
class MapLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var initialLatitude: Double = 37.5665  // 기본값: 서울
    private var initialLongitude: Double = 126.9780 // 기본값: 서울

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_location)

        // CreateCourseActivity에서 전달된 위도와 경도를 받음
        intent?.let {
            initialLatitude = it.getDoubleExtra("latitude", initialLatitude)
            initialLongitude = it.getDoubleExtra("longitude", initialLongitude)
        }

        // SupportMapFragment를 이용해 지도를 로드합니다.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 전달받은 위도와 경도로 초기 위치 설정
        val specificLocation = LatLng(initialLatitude, initialLongitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(specificLocation, 15f))

        // 지도를 클릭하여 마커 추가
        map.setOnMapClickListener { latLng ->
            map.clear() // 기존 마커 제거
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            // 선택한 위치의 위도와 경도를 반환
            val resultIntent = Intent()
            resultIntent.putExtra("latitude", latLng.latitude)
            resultIntent.putExtra("longitude", latLng.longitude)
            setResult(RESULT_OK, resultIntent)
        }
    }

    // 완료 버튼 클릭시 선택된 위치의 위도와 경도 반환
    fun onDoneClick(view: View) {
        finish() // 완료 시 액티비티 종료
    }
}