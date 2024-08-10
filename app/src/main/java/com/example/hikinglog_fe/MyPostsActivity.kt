package com.example.hikinglog_fe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hikinglog_fe.databinding.ActivityMyLikesBinding
import com.example.hikinglog_fe.databinding.ActivityMyPostsBinding

class MyPostsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyPostsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}