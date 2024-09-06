package com.example.hikinglog_fe

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
import com.example.hikinglog_fe.models.RecordListResponse
import com.example.hikinglog_fe.models.HikingRecord
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

    private var recordList: List<HikingRecord> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHikingRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            loadRecords(token)
        } else {
            Log.e("TOKEN_ERROR", "No token found in SharedPreferences")
        }

        // 리사이클러뷰 설정
        adapter = HikingRecordAdapter(recordList)
        binding.hikingRecordRecyclerView.adapter = adapter
        binding.hikingRecordRecyclerView.layoutManager = LinearLayoutManager(this)


        binding.addRecord.setOnClickListener {
            val intent = Intent(this, DirectRecordActivity::class.java)
            startActivity(intent)
        }

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
                        recordList = result.data.sortedByDescending { record ->
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(record.date)
                        }
                        // 어댑터에 정렬된 데이터 설정
                        adapter = HikingRecordAdapter(recordList)
                        binding.hikingRecordRecyclerView.adapter = adapter
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


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}