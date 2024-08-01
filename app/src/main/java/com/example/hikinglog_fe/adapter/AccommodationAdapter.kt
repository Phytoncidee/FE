package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.ShopDetailActivity
import com.example.hikinglog_fe.databinding.ItemAccommodationBinding
import com.example.hikinglog_fe.databinding.ItemEquipmentshopBinding
import com.example.hikinglog_fe.models.Accommodation
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EquipmentShop
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            Intent(context, ShopDetailActivity::class.java).apply {
                putExtra("ContentId", model.contentId) // 산 이름 전달
            }.run { context.startActivity(this) }
        }
    }
}