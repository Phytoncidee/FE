package com.phytoncidee.hikinglog_fe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.phytoncidee.hikinglog_fe.databinding.ActivitySafetyGuideBinding
class SafetyGuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safety_guide)

        val binding = ActivitySafetyGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.safetyGuideBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
            startActivity(intent)
            true
        }
    }
}