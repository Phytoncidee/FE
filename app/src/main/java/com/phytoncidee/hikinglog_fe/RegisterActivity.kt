package com.phytoncidee.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phytoncidee.hikinglog_fe.databinding.ActivityRegisterBinding
import com.phytoncidee.hikinglog_fe.interfaces.ApiService
import com.phytoncidee.hikinglog_fe.models.RegisterErrorResponse
import com.phytoncidee.hikinglog_fe.models.RegisterRequest
import com.phytoncidee.hikinglog_fe.models.RegisterResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 날짜 설정 (초기화)
        val calendar = Calendar.getInstance()
        calendar.set(2000, 0, 1) // 2000년 1월 1일
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time) // 기본값으로 설정

        binding.spinnerBirth.init(2000, 0, 1) { view, year, monthOfYear, dayOfMonth ->
            // 날짜를 yyyy-MM-dd 형식으로 변환
            calendar.set(year, monthOfYear, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
        }

        binding.btnRegister.setOnClickListener {
            Log.d("RegisterActivity", "가입하기 버튼 클릭됨")

            val name = binding.editName.text.toString()
            val birth = selectedDate
            val sex = if (binding.radioMale.isChecked) "male" else "female"
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val password2 = binding.editRePassword.text.toString()
            val phone = binding.editPhone.text.toString()

            // 빈칸 제출시 Toast
            if (name.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank() || phone.isBlank()) {
                showToast("모든 필드를 입력해주세요.")
                return@setOnClickListener
            }

            if (!binding.checkboxPrivacy.isChecked || !binding.checkboxLocation.isChecked) {
                showToast("개인정보 수집 및 이용, 위치 정보 수집 및 이용에 동의해주세요.")
                return@setOnClickListener
            }

            if (password != password2) {
                showToast("비밀번호를 다시 확인해주세요.")
                return@setOnClickListener
            }

            val newData = RegisterRequest (
                email = email,
                password = password,
                name = name,
                birth = birth,
                sex = sex,
                phone = phone
            )

            // 백엔드 통신 부분
            val authApi = ApiService.create()


            authApi.registerUser(newData).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    Log.d("RegisterResponse", response.toString())

                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        Log.d("RegisterResponse", registerResponse.toString())
                        if (registerResponse != null && registerResponse.status == 200) {
                            showToast("회원가입에 성공했습니다.")
                            Log.d("회원가입 정보", "${newData}")

                            // LoginActivity로 이동
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // RegisterActivity 종료
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Log.d("RegisterResponseError", errorResponse.toString())
                        if (errorResponse != null) {
                            val gson = Gson()
                            val registerErrorResponse = gson.fromJson(errorResponse, RegisterErrorResponse::class.java)
                            when (registerErrorResponse.status) {
                                400 -> showToast("회원가입 실패: ${registerErrorResponse.message}")
                                else -> showToast("회원가입 실패: 서버 오류")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    // 실패
                    Log.d("회원가입 통신 실패", t.message.toString())
                    Log.d("회원가입 통신 실패", "fail")
                }
            })
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}