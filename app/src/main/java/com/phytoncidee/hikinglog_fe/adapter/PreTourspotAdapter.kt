package com.phytoncidee.hikinglog_fe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.phytoncidee.hikinglog_fe.R
import com.phytoncidee.hikinglog_fe.databinding.ItemTourspotBinding
import com.phytoncidee.hikinglog_fe.interfaces.OnDataPassListener
import com.phytoncidee.hikinglog_fe.models.TourSpot

class PreTourspotHolder(val binding: ItemTourspotBinding): RecyclerView.ViewHolder(binding.root)
class PreTourspotAdapter(val context: Context, private val listener: OnDataPassListener, val datas:MutableList<TourSpot>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PreTourspotHolder(ItemTourspotBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as PreTourspotHolder).binding
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
        else {
            if (model.img2 != "") {
                // <관광지 이미지 표시(Glide)>
                Glide.with(binding.root)
                    .load("${model.img2}")
                    .override(100, 100) // 이미지 크기 조정
                    .into(binding.ImgTourspot)
            }
        }

        var isSelected : Boolean = false

        // [[리사이클러 뷰 클릭 -> 색 변화 & 데이터 저장해서 넘기기 ]]
        binding.root.setOnClickListener {
            val preTourspot = TourSpot(
                name = model.name,
                contentId = model.contentId,
                add = model.add,
                img = model.img,
                img2 = model.img2,
                mapX = model.mapX,
                mapY = model.mapY,
                tel = model.tel,
            )
            if (isSelected == false){ //미선택인 상황
                isSelected = true
                // >> 색 변화
                binding.root.setBackgroundResource(R.color.backgroundblue)
                // >> 데이터 CreateCourseActivity로 넘기기
                listener.preTourspotToActivity(preTourspot)
            }
            else { //선택인 상황에서 취소를 위해 재선택
                isSelected = false
                // 색 변화
                binding.root.setBackgroundResource(R.color.backgroundyellow)
                // >> 취소된 경우 CreateCourseActivity에 알리기
                listener.CancelpreTourspotToActivity(preTourspot)
            }
        }

    }
}