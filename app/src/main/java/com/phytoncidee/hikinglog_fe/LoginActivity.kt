package com.phytoncidee.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phytoncidee.hikinglog_fe.databinding.ActivityLoginBinding
import com.phytoncidee.hikinglog_fe.interfaces.ApiService
import com.phytoncidee.hikinglog_fe.models.LoginRequest
import com.phytoncidee.hikinglog_fe.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.editTextId.text.toString().trim()
            val password = binding.editTextPassword.text.toString()

            // 빈칸 제출시 Toast
            if (email.isEmpty() || password.isEmpty()) {
                showToast("아이디와 비밀번호를 모두 입력해주세요.")
                Log.d("LoginActivity", "빈칸 제출: 이메일 또는 비밀번호가 비어있습니다.")
                return@setOnClickListener
            }

            // 백엔드 통신 부분
            val authApi = ApiService.create()
            val data = LoginRequest(email, password)

            authApi.loginUser(data).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val message = response.body()?.message

                    when (response.code()) {
                        200 -> {
                            val loginResponse = response.body()
                            // 로그인 성공
                            saveToken(loginResponse?.data) // 토큰 저장
                            Log.d("로그인 통신 성공", "메시지: $message")
                            showToast("로그인 성공")
                            navigateToMainActivity() // 메인 화면으로 이동
                        }
                        401 -> {
                            // 비밀번호가 틀린 경우
                            showToast("비밀번호가 틀렸습니다.")
                            Log.d("로그인 실패", "비밀번호 오류")
                        }
                        else -> {
                            // 그 외의 응답 코드 처리
                            showToast("로그인에 실패했습니다.")
                            Log.d("로그인 실패", "코드: ${response.code()}")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // 실패
                    Log.d("로그인 통신 실패", t.message.toString())
                    Log.d("로그인 통신 실패", "fail")
                }
            })
        }

        // '회원가입 하기' 버튼 클릭 리스너 설정
        binding.btnGoRegister.setOnClickListener {
            Log.d("LoginActivity", "회원가입 버튼 클릭됨")
            val loginIntent = Intent(this, RegisterActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun saveToken(token: String?) {
        if (token.isNullOrEmpty()) {
            Log.d("토큰 저장 오류", "토큰이 null 또는 빈 문자열입니다.")
            return
        }
        val pref = getSharedPreferences("userToken", MODE_PRIVATE)
        with(pref.edit()) {
            putString("token", token)
            apply()
        }
        Log.d("토큰 저장", "saved")
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 로그인 화면 종료
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}