package com.example.hikinglog_fe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hikinglog_fe.databinding.ActivityAccommodationBinding
import com.example.hikinglog_fe.databinding.ActivityNationalMountainsBinding

class NationalMountainsActivity : AppCompatActivity() {
    lateinit var binding : ActivityNationalMountainsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNationalMountainsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Top100 페이지에서 클릭 후 이동한 경우 -> 검색창에 해당 Top100 산 이름 자동 입력
        val mountainName: String? = intent.getStringExtra("mountainName")
        binding.searchEditText.setText(mountainName)
    }
}