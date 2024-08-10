package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.AccommodationDetailActivity
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.RestaurantDetailActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemRestaurantBinding
import com.example.hikinglog_fe.databinding.ItemTourspotBinding
import com.example.hikinglog_fe.models.AccommodationBookmarkDeleteResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkGetResponse
import com.example.hikinglog_fe.models.AccommodationBookmarkPostResponse
import com.example.hikinglog_fe.models.PostAccommodationBMDTO
import com.example.hikinglog_fe.models.PostRestaurantBMDTO
import com.example.hikinglog_fe.models.Restaurant
import com.example.hikinglog_fe.models.RestaurantBookmarkDeleteResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkGetResponse
import com.example.hikinglog_fe.models.RestaurantBookmarkPostResponse
import com.example.hikinglog_fe.models.TourSpot
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TourspotHolder(val binding: ItemTourspotBinding): RecyclerView.ViewHolder(binding.root)
class TourspotAdapter(val context: Context, val datas:MutableList<TourSpot>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TourspotHolder(ItemTourspotBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TourspotHolder).binding
        val model = datas!![position]

        // [[관광지 정보]]
        binding.tourspotName.text = model.name
        binding.tourspotAdd.text = model.add


        if (model.img != "") {
            // <관광지 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.img}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.ImgTourspot)
        }

        var isSelected : Boolean = false

        // [[리사이클러 뷰 클릭 -> 색 변화 & 데이터 저장해서 넘기기 ]]
        binding.root.setOnClickListener {
            if (isSelected == false){ //미선택인 상황
                isSelected = true
                binding.root.setBackgroundResource(R.color.backgroundblue) // 색 변화
            }
            else { //선택인 상황에서 취소를 위해 재선택
                isSelected = false
                binding.root.setBackgroundResource(R.color.backgroundyellow) // 색 변화
            }
        }

    }
}