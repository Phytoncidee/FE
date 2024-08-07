package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.hikinglog_fe.databinding.FragmentMyPageBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService : ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)

        // ApiService 초기화
        apiService = ApiService.create()

        // 저장된 데이터 읽기
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            fetchUserProfile(token)
            binding.btnAuth.setImageResource(R.drawable.button_logout) // 로그아웃 이미지로 변경
            binding.btnAuth.setOnClickListener {
                logout()
            }
        } else {
            binding.btnAuth.setImageResource(R.drawable.button_login) // 로그인 이미지로 변경
            binding.btnAuth.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        binding.myfavoritesBtn.setOnClickListener {
            val intent = Intent(context, FavoritesActivity::class.java)
            startActivity(intent)
            true
        }
        return binding.root
    }

    private fun fetchUserProfile(token: String) {
        apiService.getProfile("Bearer $token").enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profileData = response.body()?.data
                    profileData?.let {
                        activity?.runOnUiThread {
                            binding.userName.text = it.name
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            remove("token")
            remove("userName")
            apply()
        }
        Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MyPageFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MyPageFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}