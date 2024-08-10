package com.example.hikinglog_fe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.databinding.FragmentHomeBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)

        // ApiService 초기화
        apiService = ApiService.create()

        // 저장된 데이터 읽기
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            fetchUserProfile(token)
        }


        binding.famousMountainBtn.setOnClickListener {
            val intent = Intent(context, Top100Activity::class.java)
            startActivity(intent)
            true
        }

        binding.nationalMountainBtn.setOnClickListener {
            val intent = Intent(context, NationalMountainsActivity::class.java)
            startActivity(intent)
            true
        }

        binding.equipmentShopBtn.setOnClickListener {
            val intent = Intent(context, EquipmentShopActivity::class.java)
            startActivity(intent)
            true
        }
        binding.restaurantBtn.setOnClickListener {
            val intent = Intent(context, RestaurantActivity::class.java)
            startActivity(intent)
            true
        }
        binding.accommodationBtn.setOnClickListener {
            val intent = Intent(context, AccommodationActivity::class.java)
            startActivity(intent)
            true
        }
        binding.safetyGuideBtn.setOnClickListener {
            val intent = Intent(context, SafetyGuideActivity::class.java)
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
                            val userName = it.name
                            binding.bannerName.text = "${userName}님, 환영합니다!🖐🏻"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}