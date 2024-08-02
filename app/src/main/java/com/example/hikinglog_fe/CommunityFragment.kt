package com.example.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.adapter.CommunityPostAdapter
import com.example.hikinglog_fe.databinding.FragmentCommunityBinding
import com.example.hikinglog_fe.databinding.FragmentHomeBinding
import com.example.hikinglog_fe.models.AccommodationLResponse
import com.example.hikinglog_fe.models.CommunityPostLResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCommunityBinding.inflate(inflater, container, false)

        // [Retrofit 통신 요청: 커뮤니티 글 목록]
        val call: Call<CommunityPostLResponse> = RetrofitConnection.jsonNetServ.getPostList(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzQ5NjM5MiwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.TKguWwv_0JcaNgtzinEpn7GRLYusUUnX9s6ZlOiFS00HJOMKbSGdGfbrUNqyrGExqdEQuOGy2Z11ZZUvF28jAg",
            JsonObject().apply {
                addProperty("size", 5)
                addProperty("page", 0)
            }
        )

        // [Retrofit 통신 응답: 커뮤니티 글 목록]
        call.enqueue(object : Callback<CommunityPostLResponse> {
            override fun onResponse(call: Call<CommunityPostLResponse>, response: Response<CommunityPostLResponse>) {

                if (response.isSuccessful) {
                    Log.d("mobileApp", "getPostList: $response")

                    // <리사이클러뷰에 표시>
                    binding.communityRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.communityRecyclerView.adapter = CommunityPostAdapter(context!!, response.body()!!.data)
                    binding.communityRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getPostList Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CommunityPostLResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getPostList)", t)
            }
        })


        // [플로팅 버튼 클릭 -> AddPostActivity로 이동]
        binding.mainFab.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java)) //플로팅 버튼 눌러 데이터 추가 화면(AddPostActivity)으로 넘어갔다 finish() 후 돌아옴. -> onStart()에서 처리 -> 작성한 게시물 불러옴
        }

        // [comment fragment 화면 확인용 임시 버튼 클릭 -> CommentFragment]
        binding.commentBtn.setOnClickListener {
            val commentFragment = CommentFragment.newInstance("param1", "param2")
            commentFragment.show(childFragmentManager, "CommentFragment")
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommunityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}