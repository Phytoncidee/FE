package com.example.hikinglog_fe

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.ActivityAddPostBinding
import com.example.hikinglog_fe.models.PostWriteDTO
import com.example.hikinglog_fe.models.PostWriteResponseDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPostBinding
    var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 요청
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
//            }
//        } else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//            }
//        }

        // [이미지 선택 버튼]
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == android.app.Activity.RESULT_OK) {
                // <불러온 이미지 보여주기>
                selectedImageUri = it.data?.data
                binding.PostImg.visibility = View.VISIBLE
                Glide.with(applicationContext)
                    .load(selectedImageUri)
                    .override(100, 100)
                    .into(binding.PostImg)
            }
        }

        binding.BtnPostImg.setOnClickListener {
            // <갤러리 앱 불러와 사진 선택>
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }

        // [저장 버튼]
        binding.BtnAddPost.setOnClickListener {
            if (binding.postMountain.text.isNotEmpty()) {
                if (binding.postContent.text.isNotEmpty()) {
                    // <산 이름, 게시물 내용, 이미지 저장>
                    // >> 서버에 저장할 data 만들기
                    val newPost = PostWriteDTO(
                        content = binding.postContent.text.toString(),
                        tag = binding.postMountain.text.toString()
                    )

                    // 이미지 파일을 RequestBody와 MultipartBody.Part로 변환
                    val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
                        val file = File(getRealPathFromURI(uri))
                        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                        MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }

                    // >> 서버에 넘겨 저장
                    val call: Call<PostWriteResponseDTO<String>> = RetrofitConnection.jsonNetServ.postCommunityPost(
                        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMzQ5NjM5MiwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.TKguWwv_0JcaNgtzinEpn7GRLYusUUnX9s6ZlOiFS00HJOMKbSGdGfbrUNqyrGExqdEQuOGy2Z11ZZUvF28jAg",
                        imagePart,
                        newPost
                    )

                    // [Retrofit 통신 응답: 커뮤니티 글 작성]
                    call.enqueue(object : Callback<PostWriteResponseDTO<String>> {
                        override fun onResponse(call: Call<PostWriteResponseDTO<String>>, response: Response<PostWriteResponseDTO<String>>) {
                            if (response.isSuccessful) {
                                Log.d("mobileApp", "postCommunityPost: $response")
                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "postCommunityPost Error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<PostWriteResponseDTO<String>>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(postCommunityPost)", t)
                        }
                    })
                } else {
                    Toast.makeText(this, "게시물 내용 미입력", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "산 이름 미입력", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}