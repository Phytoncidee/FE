package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hikinglog_fe.databinding.ActivityLiveRecordBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.DirectRecordRequest
import com.example.hikinglog_fe.models.DirectRecordResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.concurrent.TimeUnit

class LiveRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLiveRecordBinding
    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달된 산 이름과 고유번호를 받아옴
        val mountainName = intent.getStringExtra("name")!!
        val mountainNumber = intent.getLongExtra("number", 0)

        // 등반한 산 이름 설정
        binding.recordTitle.text = "${mountainName}\n 등산 중입니다!"

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)!!

        // 시작 버튼 클릭
        binding.startBtn.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) start() else pause()
        }

        // 종료 버튼 클릭
        binding.recordBtn.setOnClickListener {
            if (time != 0) {
                stopRecording(mountainName, mountainNumber, time, token)
            }
        }

    }

    private fun start() {
        binding.startBtn.text = "중지"
        timerTask = kotlin.concurrent.timer(period = 10) {	// timer() 호출
            time++	// period=10, 0.01초마다 time를 1씩 증가

            // 시, 분, 초, 밀리초 계산
            val hours = time / 100 / 3600
            val minutes = (time / 100 % 3600) / 60
            val seconds = (time / 100) % 60
            val milliseconds = time % 100

            runOnUiThread {
                // 화면에 시간 업데이트
                binding.timerText.text = String.format(
                    "%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds
                )
            }
        }
    }

    private fun pause() {
        binding.startBtn.text ="재실행"
        timerTask?.cancel();
    }

    private fun stopRecording(mountainName: String, mountainNumber: Long, time: Int, token: String) {
        pause() // 타이머 중지

        // 날짜를 현재 날짜로 설정
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 시간을 PT 형식으로 변환
        val totalTimeInSeconds = time / 100
        val hours = TimeUnit.SECONDS.toHours(totalTimeInSeconds.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(totalTimeInSeconds.toLong()) % 60
        val ptTime = "PT${hours}H${minutes}M"

        val request = DirectRecordRequest(
            mountainName = mountainName,
            mountainNumber = mountainNumber,
            date = currentDate,
            time = ptTime
        )

        apiService.recordHiking("Bearer $token", request)
            .enqueue(object : Callback<DirectRecordResponse> {
                override fun onResponse(
                    call: Call<DirectRecordResponse>,
                    response: Response<DirectRecordResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Log.d("LiveRecordActivity", "Message: ${result.message}")

                            showToast("기록이 성공적으로 저장되었습니다.")

                            // 기록 목록 액티비티로 이동
                            val intent = Intent(this@LiveRecordActivity, HikingRecordActivity::class.java)
                            startActivity(intent)
                            finish() // 현재 액티비티를 종료
                        }
                    } else {
                        // 오류 처리
                        Log.e("LiveRecordActivity", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<DirectRecordResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("LiveRecordActivity", "Failed to fetch data", t)
                }

            })

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}