package com.phytoncidee.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.phytoncidee.hikinglog_fe.adapter.CommentAdapter
import com.phytoncidee.hikinglog_fe.databinding.FragmentCommentBinding
import com.phytoncidee.hikinglog_fe.models.CommentWriteDTO
import com.phytoncidee.hikinglog_fe.models.CommentsGetResponse
import com.phytoncidee.hikinglog_fe.models.PostLikeCommentResponse
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

    private lateinit var binding: FragmentCommentBinding

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
        binding = FragmentCommentBinding.inflate(inflater, container, false)

        // BottomSheet 확장 설정
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isFitToContents = true
            }
        }

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)
        // 저장된 데이터 읽기
        token = sharedPreferences.getString("token", "")!!


        // [[ 댓글 목록 조회]]
        getComments()


        // [[ 댓글 등록 ]]
        binding.BtnSendComment.setOnClickListener {
            val newComment = CommentWriteDTO(
                content = binding.CommentEditText.text.toString()
            )
            val callP: Call<PostLikeCommentResponse> = RetrofitConnection.jsonNetServ.postPostComment(
                "Bearer $token",
                boardId!!.toInt(),
                newComment
            )
            callP.enqueue(object : Callback<PostLikeCommentResponse> {
                override fun onResponse(call: Call<PostLikeCommentResponse>, response: Response<PostLikeCommentResponse>) {
                    if (response.isSuccessful) {
                        Log.d("mobileApp", "postPostComment: $response")

                        // EditText 내용 비우기
                        binding.CommentEditText.text.clear()

                        // 댓글 목록 재조회
                        getComments()
                    } else {
                        // 오류 처리
                        Log.e("mobileApp", "postPostComment: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<PostLikeCommentResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(postPostComment)", t)
                }
            })
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun getComments() { // >> 댓글 조회 함수 <<
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
                    binding.commentRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.commentRecyclerView.adapter = CommentAdapter(context!!, response.body()!!.data.commentList, token, boardId, binding.commentRecyclerView)
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