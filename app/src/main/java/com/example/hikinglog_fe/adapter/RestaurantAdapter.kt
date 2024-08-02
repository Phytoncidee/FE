package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.AccommodationDetailActivity
import com.example.hikinglog_fe.RestaurantDetailActivity
import com.example.hikinglog_fe.databinding.ItemRestaurantBinding
import com.example.hikinglog_fe.models.Restaurant

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
            Intent(context, RestaurantDetailActivity::class.java).apply {
                putExtra("contentId", model.contentId) // 산 이름 전달
            }.run { context.startActivity(this) }
        }
    }
}