package com.example.hikinglog_fe

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        // 업버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    } //onCreate()


    override fun onSupportNavigateUp(): Boolean {
        Log.d("mobileApp", "onSupportNavigateUp")
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}