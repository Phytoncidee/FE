package com.example.hikinglog_fe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hikinglog_fe.databinding.ActivityNationalMountainsBinding
import com.example.hikinglog_fe.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView


class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var kakaoMap: KakaoMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView: MapView = binding.mapView
        getMap(mapView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getMap(mapView: MapView) {
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
            }
        })
    }
}
