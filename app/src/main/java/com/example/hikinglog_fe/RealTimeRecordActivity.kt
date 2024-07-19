package com.example.hikinglog_fe

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RealTimeRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_record)

        val startHikingButton: Button = findViewById(R.id.startHikingButton)
        startHikingButton.setOnClickListener {
            val bottomSheetFragment = HikingBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }
}