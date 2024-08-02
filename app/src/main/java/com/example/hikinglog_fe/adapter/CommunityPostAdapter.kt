package com.example.hikinglog_fe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ItemPostBinding
import com.example.hikinglog_fe.models.CommunityPost

class CommunityPostHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)
class CommunityPostAdapter(val context: Context, val datas:MutableList<CommunityPost>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommunityPostHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CommunityPostHolder).binding
        val model = datas!![position]

        // [[커뮤니티 글 내용]]
        binding.postUsername.text = model.userid
        binding.postContent.text = model.content
        binding.mountainTag.text = model.tag

        if (model.image != "") {
            // <커뮤니티 글 작성자 프로필 표시(Glide)> ////넘겨받는 프로필 이미지 데이터가 없다....???
//            Glide.with(binding.root)
//                .load("${model.image}")
//                .override(40, 40) // 이미지 크기 조정
//                .into(binding.postImage)
        }

        if (model.image != "") {
            // <커뮤니티 글 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.image}")
                .override(40, 40) // 이미지 크기 조정
                .into(binding.postImage)
        }
    }
}