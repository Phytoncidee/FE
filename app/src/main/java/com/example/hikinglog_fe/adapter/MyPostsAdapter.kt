package com.example.hikinglog_fe.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemPostBinding
import com.example.hikinglog_fe.models.CommunityPost
import com.example.hikinglog_fe.models.CommunityPostLResponse
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostsPostHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)
class MyPostsAdapter(val context: Context, val datas:MutableList<CommunityPost>?, private val token: String?, private val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyPostsPostHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyPostsPostHolder).binding
        val model = datas!![position]

        // [[커뮤니티 글 내용]]
        binding.postContent.text = model.content
        binding.mountainTag.text = model.tag
        binding.postUsername.text = model.userName
        binding.postDate.text = model.createdAt


        if (model.userImage != "") {
            // <커뮤니티 글 작성자 프로필 표시(Glide)>
            Glide.with(binding.root)
                .load(model.userImage)
                .override(100, 100) // 이미지 크기 조정
                .into(binding.postUserprofile)
        } else {
            binding.postUserprofile.setImageResource(R.drawable.etc_default_profile)
        }

        if (model.image != "") {
            // <커뮤니티 글 이미지 표시(Glide)>
            Glide.with(binding.root)
                .load(model.image)
                .override(400, 400) // 이미지 크기 조정
                .into(binding.postImage)
        } else {
            binding.postImage.setImageResource(R.drawable.etc_default_mountain)
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

    // [[삭제]]
    // >> 게시물 클릭(확인 다이얼로그 -> 확인 -> 삭제)
    binding.root.setOnClickListener {
        // 다이얼로그
        val eventHandler = object: DialogInterface.OnClickListener{ // 다이얼로그에 속하는 버튼에 쓸 클릭 리스너.. 변수로 받아 여러 버튼에 대해 사용
            override fun onClick(dialog: DialogInterface?, which: Int) { //which에 어떤 버튼 눌렸는지 저장
                if (which == DialogInterface.BUTTON_POSITIVE) { //positive 버튼 누른 경우
                    // >> 삭제
                    val callD: Call<PostLikeCommentResponse> = RetrofitConnection.jsonNetServ.deleteMyPost(
                        "Bearer $token",
                        model.id
                    )
                    callD.enqueue(object : Callback<PostLikeCommentResponse> {
                        override fun onResponse(call: Call<PostLikeCommentResponse>, response: Response<PostLikeCommentResponse>) {
                            if (response.isSuccessful) {
                                Log.d("mobileApp", "deleteMyPost: $response")
                                // (내 게시물 재조회)
                                // [Retrofit 통신 요청: 커뮤니티 글 목록]
                                val call: Call<CommunityPostLResponse> = RetrofitConnection.jsonNetServ.getMyPosts(
                                    "Bearer $token"
                                )
                                // [Retrofit 통신 응답: 커뮤니티 글 목록]
                                call.enqueue(object : Callback<CommunityPostLResponse> {
                                    override fun onResponse(call: Call<CommunityPostLResponse>, response: Response<CommunityPostLResponse>) {

                                        if (response.isSuccessful) {
                                            Log.d("mobileApp", "getMyPosts: $response")

                                            // <리사이클러뷰에 표시>
                                            recyclerView.layoutManager = LinearLayoutManager(context)
                                            recyclerView.adapter = MyPostsAdapter(context, response.body()!!.data.boardList,  token, recyclerView)
                                            recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

                                        } else {
                                            // 오류 처리
                                            val errorBody = response.errorBody()?.string()
                                            Log.e("mobileApp", "getMyPosts Error: ${response.code()}, Error Body: ${errorBody}")
                                        }
                                    }

                                    override fun onFailure(call: Call<CommunityPostLResponse>, t: Throwable) {
                                        // 네트워크 오류 처리
                                        Log.e("mobileApp", "Failed to fetch data(getMyPosts)", t)
                                    }
                                })
                                // (내 게시물 재조회) <<
                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "deleteMyPost: ${response.code()}")
                            }
                        }
                        override fun onFailure(call: Call<PostLikeCommentResponse>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(deleteMyPost)", t)
                        }
                    }) // 삭제 <<
                }
                else if (which == DialogInterface.BUTTON_NEGATIVE) { //negative 버튼 누른 경우
                    Log.d("mobileapp", "게시물 삭제 취소") //그냥 다이얼로그만 닫음
                }
            }
        }
        AlertDialog.Builder(context).run(){
            setTitle("게시물 삭제 확인")
            setIcon(android.R.drawable.ic_dialog_alert)

            setMessage("정말로 삭제하시겠습니까?")

            setPositiveButton("예", eventHandler)
            setNegativeButton("아니오", eventHandler)

            show()
        }
    }


        // [[댓글]]
        // >> 댓글 버튼 숨기기
        binding.BtnPostComment.visibility = View.INVISIBLE
    }
}