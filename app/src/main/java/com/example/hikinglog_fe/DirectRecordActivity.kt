package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hikinglog_fe.databinding.ActivityDirectRecordBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.DirectRecordRequest
import com.example.hikinglog_fe.models.DirectRecordResponse
import com.example.hikinglog_fe.models.NationalMountainsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DirectRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectRecordBinding
    private var selectedDate: String = ""
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달된 산 이름과 고유번호를 받아옴
        val mountainName = intent.getStringExtra("name")!!
        val mountainNumber = intent.getLongExtra("number", 0)

        // 등반한 산 이름 설정
        binding.mountainName.text = "${mountainName} 등산 기록"

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)!!

        binding.spinnerDate.init(2024, 0, 1) { view, year, monthOfYear, dayOfMonth ->
            // 날짜를 yyyy-MM-dd 형식으로 변환
            val calendar = Calendar.getInstance()
            calendar.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
        }

        // 추가하기 버튼 클릭시
        binding.btnAddRecord.setOnClickListener {
            // PT 시간 형식으로 변환
            val hour = binding.editTextHour.text.toString().toIntOrNull() ?: 0
            val minute =
                binding.editTextMinute.text.toString().toIntOrNull() ?: 0

            // PT 형식으로 변환
            val durationString = buildString {
                append("PT")
                if (hour > 0) append("${hour}H")
                if (minute > 0 || hour == 0) append("${minute}M") // 기본값으로 "PT0M"을 추가
            }

            val request = DirectRecordRequest(
                mountainName = mountainName,
                mountainNumber = mountainNumber,
                date = selectedDate,
                time = durationString
            )
            submitMountainRecord(token, request)
        }
    }

    private fun submitMountainRecord(token: String, request: DirectRecordRequest) {
        apiService.recordHiking("Bearer $token", request)
            .enqueue(object : Callback<DirectRecordResponse> {
                override fun onResponse(
                    call: Call<DirectRecordResponse>,
                    response: Response<DirectRecordResponse>
                ) {
                    if(response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Log.d("DirectRecordActivity", "Message: ${result.message}")

                            showToast("기록이 성공적으로 저장되었습니다.")

                            // 기록 목록 액티비티로 이동
                            val intent = Intent(this@DirectRecordActivity, HikingRecordActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티를 종료
                        }
                    } else {
                        // 오류 처리
                        Log.e("DirectRecordActivity", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DirectRecordResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("DirectRecordActivity", "Failed to fetch data", t)
                }
            })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}