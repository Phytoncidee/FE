package com.example.hikinglog_fe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ActivityMountainInfoBinding
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.NationalMountainsResponse

class MountainInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMountainInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMountainInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 Mountain 데이터를 받아옴
        val mountain = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("mountain", Mountain::class.java)
        } else {
            intent.getParcelableExtra("mountain") as? Mountain
        }

        // 이미지 URL 수신
        val imageUrl = intent.getStringExtra("image_url")

        // Mountain 객체가 null인지, 예상된 데이터가 있는지
        if (mountain == null) {
            Log.e("MountainInfoActivity", "Mountain 객체 null")
        } else {
            Log.d("MountainInfoActivity", "Mountain 객체를 받음: $mountain")
            displayMountainInfo(mountain)

            // 이미지 URL을 사용하여 이미지 로드
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.etc_default_mountain) // 에러 시 이미지
                    .into(binding.mntImg)
            } else {
                // 이미지 URL이 비어 있을 경우 기본 이미지 설정
                binding.mntImg.setImageResource(R.drawable.etc_default_mountain)
            }
        }


        // [[마이관광 이동 버튼 클릭]]
        binding.buttonMakeCourse.setOnClickListener {
            val context = binding.root.context
            val intent = Intent(context, CreateCourseActivity::class.java).apply {
                putExtra("mountain", mountain)
            }
            context.startActivity(intent)
        }

    } // onCreate()

    private fun displayMountainInfo(mountain: Mountain) {
        // 산의 상세 정보를 표시
        binding.mountainTitle.text = mountain!!.mntiname
        binding.mountainComment.text = mountain!!.mntidetails
        binding.mountainLocation.text = mountain!!.mntiadd
        binding.mountainHeight.text = "${mountain!!.mntihigh} m"
    }
}