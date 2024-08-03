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
import com.example.hikinglog_fe.databinding.FragmentHomeBinding
import com.example.hikinglog_fe.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding : FragmentMyPageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)

        val userEmail = sharedPreferences.getString("userEmail", null)
        val token = sharedPreferences.getString("token", null)

        if (userEmail != null && token != null) {
            binding.userEmail.text = userEmail
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