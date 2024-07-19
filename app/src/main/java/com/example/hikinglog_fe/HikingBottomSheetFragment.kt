package com.example.hikinglog_fe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HikingBottomSheetFragment : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hiking_bottom_sheet, container, false)
        return view
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap
//        val seoul = LatLng(37.5665, 126.9780)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10f))
//    }
}
