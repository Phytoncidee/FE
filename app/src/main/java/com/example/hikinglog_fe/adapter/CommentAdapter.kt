package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ItemCommentBinding
import com.example.hikinglog_fe.models.Comment

class CommentHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)
class CommentAdapter(val context: Context, val datas:MutableList<Comment>?, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CommentHolder).binding
        val model = datas!![position]

        binding.commentContent.text = model.content
        binding.commentUserName.text = model.userName

        if (model.userImage != "") {
            // <커뮤니티 글 작성자 프로필 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.userImage}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.commentUserProfile)
        }

    }
}