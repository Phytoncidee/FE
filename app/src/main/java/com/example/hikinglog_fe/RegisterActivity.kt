package com.example.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hikinglog_fe.R


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // '가입 취소' 버튼 참조
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // '가입 취소' 버튼 클릭 리스너 설정
        btnCancel.setOnClickListener {
            // LoginActivity로 돌아가는 Intent 생성
//            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // 현재 Activity 종료
            finish()
        }
    }
}