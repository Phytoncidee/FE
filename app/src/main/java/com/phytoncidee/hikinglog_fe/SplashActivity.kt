package com.phytoncidee.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val pref = getSharedPreferences("userToken", MODE_PRIVATE)
//        val token = pref.getString("token", null)
//        Log.d("자동로그인 여부 확인", token ?: "토큰이 null입니다.")   // null일 경우 빈 문자열을 기본값으로 설정

        val mainExe : Executor = ContextCompat.getMainExecutor(this)
        val backgroundExe : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        backgroundExe.schedule({    // 쓰레드, 시간, 단위
                mainExe.execute {   // 쓰레드 형태로 실행
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            3, TimeUnit.SECONDS
        )
    }
}