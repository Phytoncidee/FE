package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.CreateCourseActivity
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.databinding.ItemTourspotBinding
import com.example.hikinglog_fe.interfaces.OnDataPassListener
import com.example.hikinglog_fe.models.MyTourDAccommo
import com.example.hikinglog_fe.models.TourSpot

class TourDTourspotHolder(val binding: ItemTourspotBinding): RecyclerView.ViewHolder(binding.root)
class TourDTourspotAdapter(val context: Context, val datas:MutableList<MyTourDAccommo>?, val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TourDTourspotHolder(ItemTourspotBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TourDTourspotHolder).binding
        val model = datas!![position]

        // [[관광지 정보]]
        binding.tourspotName.text = model.sname
        binding.tourspotAdd.text = model.location


        if (model.simage != "") {
            // <관광지 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.simage}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.ImgTourspot)
        }

    }
}