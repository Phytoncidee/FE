package com.example.hikinglog_fe.adapter

import android.app.AlertDialog
import android.content.Intent
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
import com.example.hikinglog_fe.CreateCourseActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemCommentBinding
import com.example.hikinglog_fe.models.Comment
import com.example.hikinglog_fe.models.CommentsGetResponse
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import com.example.hikinglog_fe.models.ProfileGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)
class CommentAdapter(val context: Context, val datas:MutableList<Comment>?, private val token: String?, private val boardId: String?, private val recyclerView: RecyclerView): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        // [현재 로그인한 사용자와 작성자가 일치하면 삭제버튼 보이기]
        // >> 현재 로그인한 사용자 프로필 조회 -> userid 얻어오기
        val call: Call<ProfileGetResponse> = RetrofitConnection.jsonNetServ.getMyProfile(
            "Bearer $token"
        )
        call.enqueue(object : Callback<ProfileGetResponse> {
            override fun onResponse(call: Call<ProfileGetResponse>, response: Response<ProfileGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getMyProfile: $response")
                    // >> 로그인 사용자 userid와 comment 목록의 작성자 userid 비교
                    if (response.body()!!.data.userid == model.userid) { //본인이 작성한 댓글
                        binding.BtnDelete.visibility = View.VISIBLE // 삭제 버튼 보이도록
                    }
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getMyProfile: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ProfileGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMyProfile)", t)
            }
        })

        // >> 삭제 버튼 클릭(확인 다이얼로그 -> 확인 -> 삭제)
        binding.BtnDelete.setOnClickListener {
            // 다이얼로그
            val eventHandler = object: DialogInterface.OnClickListener{ // 다이얼로그에 속하는 버튼에 쓸 클릭 리스너.. 변수로 받아 여러 버튼에 대해 사용
                override fun onClick(dialog: DialogInterface?, which: Int) { //which에 어떤 버튼 눌렸는지 저장
                    if (which == DialogInterface.BUTTON_POSITIVE) { //positive 버튼 누른 경우
                        // >> 삭제
                        val callD: Call<PostLikeCommentResponse> = RetrofitConnection.jsonNetServ.deletePostComment(
                            "Bearer $token",
                            boardId!!.toInt(),
                            model.id
                        )
                        callD.enqueue(object : Callback<PostLikeCommentResponse> {
                            override fun onResponse(call: Call<PostLikeCommentResponse>, response: Response<PostLikeCommentResponse>) {
                                if (response.isSuccessful) {
                                    Log.d("mobileApp", "deletePostComment: $response")
                                    // (댓글 재조회)
                                    val callG: Call<CommentsGetResponse> = RetrofitConnection.jsonNetServ.getPostComments(
                                        "Bearer $token",
                                        boardId!!.toInt(),
                                        2147483647,
                                        0
                                    )
                                    callG.enqueue(object : Callback<CommentsGetResponse> {
                                        override fun onResponse(call: Call<CommentsGetResponse>, response: Response<CommentsGetResponse>) {
                                            if (response.isSuccessful) {
                                                Log.d("mobileApp", "getPostComments: $response")

                                                // <리사이클러뷰에 표시>
                                                recyclerView.layoutManager = LinearLayoutManager(context)
                                                recyclerView.adapter = CommentAdapter(context!!, response.body()!!.data.commentList, token, boardId, recyclerView)
                                                recyclerView.addItemDecoration(
                                                    DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                                                )
                                            } else {
                                                // 오류 처리
                                                Log.e("mobileApp", "getPostComments: ${response.code()}")
                                            }
                                        }
                                        override fun onFailure(call: Call<CommentsGetResponse>, t: Throwable) {
                                            // 네트워크 오류 처리
                                            Log.e("mobileApp", "Failed to fetch data(getPostComments)", t)
                                        }
                                    }) // (댓글 재조회) <<
                                } else {
                                    // 오류 처리
                                    Log.e("mobileApp", "deletePostComment: ${response.code()}")
                                }
                            }
                            override fun onFailure(call: Call<PostLikeCommentResponse>, t: Throwable) {
                                // 네트워크 오류 처리
                                Log.e("mobileApp", "Failed to fetch data(deletePostComment)", t)
                            }
                        }) // 삭제 <<
                    }
                    else if (which == DialogInterface.BUTTON_NEGATIVE) { //negative 버튼 누른 경우
                        Log.d("mobileapp", "댓글 삭제 취소") //그냥 다이얼로그만 닫음
                    }
                }
            }
            AlertDialog.Builder(context).run(){
                setTitle("댓글 삭제 확인")
                setIcon(android.R.drawable.ic_dialog_alert)

                setMessage("정말로 삭제하시겠습니까?")

                setPositiveButton("예", eventHandler)
                setNegativeButton("아니오", eventHandler)

                show()
            }
        }


    }
}