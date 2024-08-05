package com.example.hikinglog_fe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.adapter.CommunityPostAdapter
import com.example.hikinglog_fe.databinding.ActivityAddPostBinding
import com.example.hikinglog_fe.databinding.ActivityEquipmentShopBinding
import com.example.hikinglog_fe.models.CommunityPostLResponse
import com.example.hikinglog_fe.models.PostWriteDTO
import com.example.hikinglog_fe.models.PostWriteResponseDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AddPostActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddPostBinding
    lateinit var uri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [이미지 선택 버튼]
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode === android.app.Activity.RESULT_OK) { //성공적으로 이미지 불러왔으면..
                // <불러온 이미지 보여주기>
                binding.PostImg.visibility = View.VISIBLE
                Glide.with(applicationContext)
                    .load(it.data?.data)
                    .override(100, 100)
                    .into(binding.PostImg)
                // <서버에 업로드하기 위한 uri 확보>
                uri = it.data?.data!!
            }
        }
        binding.BtnPostImg.setOnClickListener {//사진 버튼 클릭 시
            // <갤러리 앱 불러와 사진 선택>
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        // [저장 버튼]
        binding.BtnAddPost.setOnClickListener {
            if(binding.postMountain.text.isNotEmpty()){
                if(binding.postContent.text.isNotEmpty()){
                    // <산 이름, 게시물 내용, 이미지 저장>
                    // >> 서버에 저장할 data 만들기
                    val newPost = PostWriteDTO(title = "New Post1", content = "This is a new post1")


                    // >> 서버에 넘겨 저장
                    // [Retrofit 통신 요청: 커뮤니티 글 작성]
                    val call: Call<PostWriteResponseDTO<String>> = RetrofitConnection.jsonNetServ.postCommunityPost(
                        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzQ5NjM5MiwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.TKguWwv_0JcaNgtzinEpn7GRLYusUUnX9s6ZlOiFS00HJOMKbSGdGfbrUNqyrGExqdEQuOGy2Z11ZZUvF28jAg",
                        newPost
                    )

                    // [Retrofit 통신 응답: 커뮤니티 글 작성]
                    call.enqueue(object : Callback<PostWriteResponseDTO<String>> {
                        override fun onResponse(call: Call<PostWriteResponseDTO<String>>, response: Response<PostWriteResponseDTO<String>>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "postCommunityPost: $response")


                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "postCommunityPost Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<PostWriteResponseDTO<String>>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(postCommunityPost)", t)
                        }
                    })


                }
                else{
                    Toast.makeText(this, "산 이름 미입력", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "게시물 내용 미입력", Toast.LENGTH_LONG).show()
            }
        }
    }
}