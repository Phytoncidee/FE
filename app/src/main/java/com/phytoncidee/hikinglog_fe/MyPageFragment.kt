package com.phytoncidee.hikinglog_fe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.phytoncidee.hikinglog_fe.databinding.FragmentMyPageBinding
import com.phytoncidee.hikinglog_fe.interfaces.ApiService
import com.phytoncidee.hikinglog_fe.models.HikingRecordDetailResponse
import com.phytoncidee.hikinglog_fe.models.ProfileResponse
import com.phytoncidee.hikinglog_fe.models.RecordListResponse
import com.phytoncidee.hikinglog_fe.models.WithdrawResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService : ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)

        // ApiService 초기화
        apiService = ApiService.create()

        // 저장된 데이터 읽기
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            fetchUserProfile(token)
            fetchHikingRecords(token)
            binding.btnAuth.setImageResource(R.drawable.button_logout) // 로그아웃 이미지로 변경
            binding.btnAuth.setOnClickListener {
                logout()
            }
        } else {
            binding.btnAuth.setImageResource(R.drawable.button_login) // 로그인 이미지로 변경
            binding.btnAuth.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // 내 활동_내가 작성한 게시물
        binding.myPostsBtn.setOnClickListener {
            val intent = Intent(context, MyPostsActivity::class.java)
            startActivity(intent)
            true
        }

        // 내 활동_좋아요한 게시물
//        binding.myLikesBtn.setOnClickListener {
//            val intent = Intent(context, MyLikesActivity::class.java)
//            startActivity(intent)
//            true
//        }

        // 내 활동_즐겨찾기
        binding.myfavoritesBtn.setOnClickListener {
            val intent = Intent(context, MyBookmarksActivity::class.java)
            startActivity(intent)
            true
        }

        // 회원탈퇴
        binding.withdrawBtn.setOnClickListener {
            // 모달 창 띄워 탈퇴 여부 확인
            val builder = AlertDialog.Builder(context)
            builder.setMessage("정말 탈퇴하시겠어요?")
                .setPositiveButton("탈퇴") { dialog, _ ->
                    apiService.deleteAccount("Bearer $token").enqueue(object: Callback<WithdrawResponse> {
                        override fun onResponse(
                            call: Call<WithdrawResponse>,
                            response: Response<WithdrawResponse>
                        ) {
                            if (response.isSuccessful) {
                                // 탈퇴 성공
                                Toast.makeText(context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                                // 로그인 화면으로 이동
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                // 탈퇴 실패
                                Toast.makeText(context, "탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                Log.e("MyPageFragment", "회원 탈퇴 실패")
                            }
                        }

                        override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                            // 네트워크 실패 시 처리
                            t.printStackTrace()
                            Log.e("MyPageFragment", "Failed 회원 탈퇴")
                        }

                    })
                }

                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }

            // 모달 창을 실제로 화면에 보여줍니다.
            builder.create().show()
        }

        return binding.root
    }

    private fun fetchUserProfile(token: String) {
        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profileData = response.body()?.data
                    profileData?.let {
                        activity?.runOnUiThread {
                            binding.userName.text = it.name
                            binding.bannerName.text = it.name
                            // 이미지 로드
                            Glide.with(this@MyPageFragment)
                                .load(it.image)
                                .into(binding.userProfile)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun fetchHikingRecords(token: String) {
        apiService.getHikingRecords("Bearer $token").enqueue(object : Callback<RecordListResponse> {
            override fun onResponse(
                call: Call<RecordListResponse>,
                response: Response<RecordListResponse>
            ) {
                if(response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.status == 200) {
                        val records = result.data
                        var totalCount = 0
                        var totalTime = 0
                        var recordsToFetch = records.size
                        var fetchedRecords = 0

                        // 각 등산기록의 상세정보 조회
                        for (record in records){
                            apiService.getDetailRecord("Bearer $token", record.rid).enqueue(object : Callback<HikingRecordDetailResponse>{
                                override fun onResponse(
                                    call: Call<HikingRecordDetailResponse>,
                                    response: Response<HikingRecordDetailResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val detailResponse = response.body()
                                        if (detailResponse != null && detailResponse.status == 200) {
                                            val detail = detailResponse.data // 단일 객체
                                            totalCount += 1
                                            totalTime += detail.time

                                            fetchedRecords += 1

                                            // 모든 기록의 상세정보 조회한 후 UI 업데이트
                                            if (fetchedRecords == recordsToFetch) {
                                                updateUI(totalCount, totalTime)
                                            }
                                        } else {
                                            Log.d("MyPageFragment","상세정보를 가져오지 못했습니다.")
                                        }
                                    } else {
                                        // 서버 오류
                                        Log.d("MyPageFragment","Error: ${response.code()}")
                                    }
                                }

                                override fun onFailure(
                                    call: Call<HikingRecordDetailResponse>,
                                    t: Throwable
                                ) {
                                    // 네트워크 오류
                                    Log.e("MyPageFragment", "Failed to fetch data - 상세조회", t)
                                }
                            })
                        }
                    }

                }
            }
            override fun onFailure(call: Call<RecordListResponse>, t: Throwable) {
                Log.e("MyPageFragment", "Failed to fetch data - 목록 조회", t)
            }
        })
    }

    private fun updateUI(totalCount: Int, totalTime: Int) {
        binding.hikingCount.text = totalCount.toString()
        binding.hikingHeight.text = totalTime.toString()
    }


    private fun logout() {
        with(sharedPreferences.edit()) {
            remove("token")
            remove("userName")
            apply()
        }
        // UI 초기화
        binding.userName.text = "로그인하세요"
        binding.bannerName.text = ""
        binding.btnAuth.setImageResource(R.drawable.button_login)

        // 로그인 화면으로 이동
        val intent = Intent(context, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

        Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
    }
}