package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.AccommodationAdapter
import com.example.hikinglog_fe.adapter.CommentAdapter
import com.example.hikinglog_fe.databinding.FragmentCommentBinding
import com.example.hikinglog_fe.models.CommentsGetResponse
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_BOARD_ID = "boardId"

/**
 * A simple [Fragment] subclass.
 * Use the [CommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommentFragment : BottomSheetDialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token : String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var boardId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            boardId = it.getString(ARG_BOARD_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCommentBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)
        // 저장된 데이터 읽기
        token = sharedPreferences.getString("token", "")!!


        // [[ 댓글 목록 조회]]
        val call: Call<CommentsGetResponse> = RetrofitConnection.jsonNetServ.getPostComments(
            "Bearer $token",
            boardId!!.toInt(),
            2147483647,
            0
        )
        call.enqueue(object : Callback<CommentsGetResponse> {
            override fun onResponse(call: Call<CommentsGetResponse>, response: Response<CommentsGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getPostComments: $response")

                    // <리사이클러뷰에 표시>
                    binding.commentRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.commentRecyclerView.adapter = CommentAdapter(context!!, response.body()!!.data.commentList, token)
                    binding.commentRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getPostComments: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<CommentsGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getPostComments)", t)
            }
        })

        // [[ 댓글 등록 ]]

        // [[ 댓글 삭제 ]]


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, boardId: String) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_BOARD_ID, boardId)
                }
            }
    }
}