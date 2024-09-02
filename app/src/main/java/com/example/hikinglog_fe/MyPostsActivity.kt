package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.CommunityPostAdapter
import com.example.hikinglog_fe.adapter.MyPostsAdapter
import com.example.hikinglog_fe.adapter.MyPostsPostHolder
import com.example.hikinglog_fe.databinding.ActivityMyLikesBinding
import com.example.hikinglog_fe.databinding.ActivityMyPostsBinding
import com.example.hikinglog_fe.models.CommunityPostLResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyPostsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // [[SharedPreferences 초기화]
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

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
                    binding.communityRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.communityRecyclerView.adapter = MyPostsAdapter(this@MyPostsActivity, response.body()!!.data.boardList,  token)
                    binding.communityRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
//                    Log.e("mobileApp", "getPostList Error: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("mobileApp", "getMyPosts Error: ${response.code()}, Error Body: ${errorBody}")
                }
            }

            override fun onFailure(call: Call<CommunityPostLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMyPosts)", t)
            }
        })
    }
}