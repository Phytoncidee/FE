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
import com.example.hikinglog_fe.databinding.ItemAccommodationBinding
import com.example.hikinglog_fe.databinding.ItemEquipmentshopBinding
import com.example.hikinglog_fe.databinding.ItemRestaurantBinding
import com.example.hikinglog_fe.models.Accommodation
import com.example.hikinglog_fe.models.EShopBookmarkGetResponse
import com.example.hikinglog_fe.models.EquipmentShop
import com.example.hikinglog_fe.models.MBookmarkGetResponse
import com.example.hikinglog_fe.models.Restaurant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantHolder(val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root)
class RestaurantAdapter(val context: Context, val datas:MutableList<Restaurant>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RestaurantHolder(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as RestaurantHolder).binding
        val model = datas!![position]

        // [[음식점 정보]]
        binding.restaurantName.text = model.name
        binding.restaurantAdd.text = model.add


        if (model.img != "") {
            // <음식점 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.img}")
                .override(40, 40) // 이미지 크기 조정
                .into(binding.ImgRestaurant)
        }


        // [[리사이클러 뷰 클릭 -> 음식점 상세 페이지로 이동]]
        binding.root.setOnClickListener {

        }
    }
}