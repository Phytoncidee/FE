package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.AccommodationDetailActivity
import com.example.hikinglog_fe.databinding.ItemAccommodationBinding
import com.example.hikinglog_fe.models.Accommodation

class AccommodationHolder(val binding: ItemAccommodationBinding): RecyclerView.ViewHolder(binding.root)
class AccommodationAdapter(val context: Context, val datas:MutableList<Accommodation>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccommodationHolder(ItemAccommodationBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AccommodationHolder).binding
        val model = datas!![position]

        // [[숙박시설 정보]]
        binding.accommodationName.text = model.name
        binding.accommodationAdd.text = model.add

        if (model.img != "") {
            // <숙박시설 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.img}")
                .override(40, 40) // 이미지 크기 조정
                .into(binding.imgAccommodation)
        }


        // [[리사이클러 뷰 클릭 -> 숙박시설 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            Intent(context, AccommodationDetailActivity::class.java).apply {
                putExtra("contentId", model.contentId) // 산 이름 전달
            }.run { context.startActivity(this) }
        }
    }
}