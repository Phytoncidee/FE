package com.example.hikinglog_fe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.CommentFragment
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemPostBinding
import com.example.hikinglog_fe.models.CommunityPost
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommunityPostHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)
class CommunityPostAdapter(val context: Context, val datas:MutableList<CommunityPost>?, val fragmentManager: FragmentManager, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        binding.postContent.text = model.content
        binding.mountainTag.text = model.tag
        binding.postUsername.text = model.userName
        binding.postDate.text = model.createdAt


        if (model.userImage != "") {
            // <커뮤니티 글 작성자 프로필 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.userImage}")
                .override(100, 100) // 이미지 크기 조정
                .into(binding.postUserprofile)
        }

        if (model.image != "") {
            Log.d("mobileApp", "커뮤니티 글 이미지 표시: ${model.image}")
            // <커뮤니티 글 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load("${model.image}")
                .override(400, 400) // 이미지 크기 조정
                .into(binding.postImage)
        }

        // [[좋아요]]
        // >> 좋아요 여부 표시
        var isLiked = model.liked
        if (isLiked == true) {
            binding.BtnPostHeart.setImageResource(R.drawable.icon_community_filledheart)
        }
        // >> 좋아요 수 표시
        var likeNumber = model.likeNum
        binding.CountPostHeart.text = likeNumber.toString()
        // >> 좋아요 버튼 클릭
        binding.BtnPostHeart.setOnClickListener {
            if (isLiked == true) { // 좋아요 => 삭제
                isLiked = false
                binding.BtnPostHeart.setImageResource(R.drawable.icon_community_emptyheart)
                likeNumber -= 1
                binding.CountPostHeart.text = likeNumber.toString()

                // <좋아요 삭제>
                val callD: Call<PostLikeCommentResponse> = RetrofitConnection.jsonNetServ.deletePostLike(
                    "Bearer $token",
                    model.id
                )
                callD.enqueue(object : Callback<PostLikeCommentResponse> {
                    override fun onResponse(call: Call<PostLikeCommentResponse>, response: Response<PostLikeCommentResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "deletePostLike: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "deletePostLike: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<PostLikeCommentResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(deletePostLike)", t)
                    }
                })
            }
            else { // 좋아요 => 등록
                isLiked = true
                binding.BtnPostHeart.setImageResource(R.drawable.icon_community_filledheart)
                likeNumber += 1
                binding.CountPostHeart.text = likeNumber.toString()

                // <좋아요 등록>
                val callP: Call<PostLikeCommentResponse> = RetrofitConnection.jsonNetServ.postPostLike(
                    "Bearer $token",
                    model.id
                )
                callP.enqueue(object : Callback<PostLikeCommentResponse> {
                    override fun onResponse(call: Call<PostLikeCommentResponse>, response: Response<PostLikeCommentResponse>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "postPostLike: $response")
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "postPostLike: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<PostLikeCommentResponse>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(postPostLike)", t)
                    }
                })
            }
        }


        // [[댓글]]
        // >> 댓글 버튼 클릭
        binding.BtnPostComment.setOnClickListener {
            val commentFragment = CommentFragment.newInstance("param1", "param2", model.id.toString())
            commentFragment.show(fragmentManager, "CommentFragment")
        }
    }
}