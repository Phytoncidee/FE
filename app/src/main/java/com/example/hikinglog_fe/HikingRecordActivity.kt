package com.example.hikinglog_fe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.HikingRecordAdapter
import com.example.hikinglog_fe.databinding.ActivityHikingRecordBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.DeleteRecordResponse
import com.example.hikinglog_fe.models.RecordListResponse
import com.example.hikinglog_fe.models.HikingRecord
import com.example.hikinglog_fe.models.HikingRecordDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class HikingRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHikingRecordBinding
    private lateinit var adapter: HikingRecordAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    private var recordList: MutableList<HikingRecord> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHikingRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)!!

        loadRecords(token)

        // 리사이클러뷰 설정
        adapter = HikingRecordAdapter(recordList) { record ->
            // 다이얼로그로 삭제 확인받기
            showDeleteDialog(record, token)
        }
        binding.hikingRecordRecyclerView.adapter = adapter
        binding.hikingRecordRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun loadRecords(token: String) {
        apiService.getHikingRecords("Bearer $token").enqueue(object: Callback<RecordListResponse> {
            override fun onResponse(
                call: Call<RecordListResponse>,
                response: Response<RecordListResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("HikingRecordActivity", "Response Body: ${result?.data}")
                    if (result != null && result.status == 200) {
                        // 받아온 데이터를 날짜 기준으로 정렬
                        recordList.clear()
                        recordList.addAll(result.data.sortedByDescending { record ->
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(record.date)
                        })
                        adapter.notifyDataSetChanged()
                    } else {
                        showToast("기록 목록을 가져오는 데 실패했습니다.")
                    }
                } else {
                    // 서버 오류
                    Log.e("HikingRecordActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RecordListResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("HikingRecordActivity", "Failed to fetch data", t)
            }

        })
    }

    private fun showDeleteDialog(record: HikingRecord, token: String) {
        AlertDialog.Builder(this)
            .setMessage("${record.mname} 등산 기록을 삭제하시겠습니까?")
            .setPositiveButton("확인") {_, _ ->
                deleteRecord(record, token)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteRecord(record: HikingRecord, token: String) {
        // 1. 먼저 상세조회를 통해 rid값 얻기
        apiService.getDetailRecord("Bearer $token", record.rid).enqueue(object : Callback<HikingRecordDetailResponse> {
            override fun onResponse(
                call: Call<HikingRecordDetailResponse>,
                response: Response<HikingRecordDetailResponse>
            ) {
                if(response.isSuccessful) {
                    val detailResponse = response.body()
                    if (detailResponse != null && detailResponse.status == 200) {
                        // 상세조회 성공
                        val rid = detailResponse.data.rid
                        performDelete(token, rid)
                    } else {
                        Log.d("HikingRecordActivity", "상세정보를 가져오지 못했습니다.")
                    }
                } else {
                    Log.d("HikingRecordActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HikingRecordDetailResponse>, t: Throwable) {
                Log.e("HikingRecordActivity", "Failed to fetch data - 상세조회", t)
            }
        })
    }

    // 실제 삭제 수행
    private fun performDelete(token: String, rid: Int) {
        apiService.deleteHikingRecords("Bearer $token", rid).enqueue(object : Callback<DeleteRecordResponse> {
            override fun onResponse(
                call: Call<DeleteRecordResponse>,
                response: Response<DeleteRecordResponse>
            ) {
                if (response.isSuccessful){
                    // 삭제 성공 후 리스트 갱신
                    loadRecords(token)
                } else {
                    Log.e("HikingRecordActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DeleteRecordResponse>, t: Throwable) {
                Log.e("HikingRecordActivity", "Failed to delete record", t)
            }
        })

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}