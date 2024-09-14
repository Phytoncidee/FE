package com.phytoncidee.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.phytoncidee.hikinglog_fe.adapter.MyToursAdapter
import com.phytoncidee.hikinglog_fe.databinding.FragmentMyTourismBinding
import com.phytoncidee.hikinglog_fe.models.MyTourLResponse
import com.phytoncidee.hikinglog_fe.models.ProfileGetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyTourismFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyTourismFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding : FragmentMyTourismBinding
    private lateinit var token : String

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyTourismBinding.inflate(inflater, container, false)

        // SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences("userToken", Context.MODE_PRIVATE)
        // 저장된 데이터 읽기
        token = sharedPreferences.getString("token", "")!!

        // >> 현재 로그인한 사용자 프로필 조회 -> userid 얻어오기
        val callU: Call<ProfileGetResponse> = RetrofitConnection.jsonNetServ.getMyProfile(
            "Bearer $token"
        )
        callU.enqueue(object : Callback<ProfileGetResponse> {
            override fun onResponse(call: Call<ProfileGetResponse>, response: Response<ProfileGetResponse>) {
                if (response.isSuccessful) {
                    Log.d("mobileApp", "getMyProfile: ${response.body()!!.data.userid}")
                    // [Retrofit 통신 요청: 마이 관광 목록]
                    val call: Call<List<MyTourLResponse>> = RetrofitConnection.jsonNetServ.getMyTours(
                        "Bearer $token",
                        response.body()!!.data.userid
                    )

                    // [Retrofit 통신 응답: 마이 관광 목록]
                    call.enqueue(object : Callback<List<MyTourLResponse>> {
                        override fun onResponse(call: Call<List<MyTourLResponse>>, response: Response<List<MyTourLResponse>>) {

                            if (response.isSuccessful) {
                                Log.d("mobileApp", "getMyTours: $response")

                                // <리사이클러뷰에 표시>
                                binding.myTourismRecyclerView.layoutManager = LinearLayoutManager(context)
                                binding.myTourismRecyclerView.adapter = MyToursAdapter(context!!, response.body()!!, childFragmentManager, token)
                                binding.myTourismRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("mobileApp", "getMyTours Error: ${response.code()}, Error Body: ${errorBody}")
                            }
                        }

                        override fun onFailure(call: Call<List<MyTourLResponse>>, t: Throwable) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(getMyTours)", t)
                        }
                    })
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getMyProfile: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ProfileGetResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getMyProfile)", t)
            }
        })



        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyTourismFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyTourismFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}