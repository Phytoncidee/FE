package com.example.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainExe : Executor = ContextCompat.getMainExecutor(this)
        val backgroundExe : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        backgroundExe.schedule(
            // 쓰레드, 시간, 단위
            {
                mainExe.execute({ // 쓰레드 형태로 실행
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            },
            3, TimeUnit.SECONDS
        )
    }
}