package com.example.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnRegister: Button = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            // RegisterActivity로 이동
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupEditTextFocusListener(editText: EditText) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // 포커스가 있을 때 보더 색상을 초록색으로 변경
                DrawableCompat.setTint(
                    DrawableCompat.wrap(editText.background),
                    ContextCompat.getColor(this, R.color.green)
                )
            } else {
                // 포커스가 없을 때 보더 색상을 기본 색상으로 변경
                DrawableCompat.setTint(
                    DrawableCompat.wrap(editText.background),
                    ContextCompat.getColor(this, R.color.lightgray)
                )
            }
        }
    }
}