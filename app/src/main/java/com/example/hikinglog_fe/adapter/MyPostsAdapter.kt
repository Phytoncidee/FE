package com.example.hikinglog_fe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyPostsPostHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)
class MyPostsAdapter(val context: Context, val datas:MutableList<CommunityPost>?, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyPostsPostHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyPostsPostHolder).binding
        val model = datas!![position]

        // [[커뮤니티 글 내용]]
        binding.postContent.text = model.content
        binding.mountainTag.text = model.tag
        binding.postUsername.text = model.userName

//        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//        binding.postDate.text = dateFormat.format(model.createdAt)

        // 날짜 변환 코드
//        val dateTimeString = model.createdAt  // "2024-08-30T16:08:21.350212"
//        val originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
//        val targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//        val dateTime = LocalDateTime.parse(dateTimeString, originalFormat)
//        val formattedDateTime = dateTime.format(targetFormat)
//        // 변환된 날짜를 TextView에 설정
//        binding.postDate.text = formattedDateTime


        if (model.userImage != "") {
            // <커뮤니티 글 작성자 프로필 표시(Glide)>
            Glide.with(binding.root)
                .load(model.userImage)
                .override(100, 100) // 이미지 크기 조정
                .into(binding.postUserprofile)
        }

        if (model.image != "") {
            // <커뮤니티 글 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load(model.image)
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
        // >> 댓글 버튼 숨기기
        binding.BtnPostComment.visibility = View.INVISIBLE
    }
}