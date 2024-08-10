package com.example.hikinglog_fe

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hikinglog_fe.databinding.ActivityMyLikesBinding

class MyLikesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyLikesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLikesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}