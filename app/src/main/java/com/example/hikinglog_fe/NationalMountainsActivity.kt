package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.MountainAdapter
import com.example.hikinglog_fe.databinding.ActivityNationalMountainsBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.NationalMountainsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NationalMountainsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNationalMountainsBinding
    private lateinit var apiService: ApiService
    private lateinit var adapter: MountainAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNationalMountainsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            // 어댑터 초기화, ApiService 전달
            adapter = MountainAdapter(apiService, token)
            binding.allMountainsRecyclerView.adapter = adapter
            binding.allMountainsRecyclerView.layoutManager = LinearLayoutManager(this)

            // Spinner 구현
            val items = resources.getStringArray(R.array.region_array)


            val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

            binding.spinner.adapter = myAdapter

            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작
                    when (position) {
                        0 -> {

                        }

                        1 -> {

                        }
                        //...
                        else -> {

                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            // fetchMountains 호출
            fetchMountains(token)
        } else {
            Log.e("TOKEN_ERROR", "No token found in SharedPreferences")
        }
        
        // Top100 페이지에서 클릭 후 이동한 경우 -> 검색창에 해당 Top100 산 이름 자동 입력
        val mountainName: String? = intent.getStringExtra("mountainName")
        binding.searchEditText.setText(mountainName)
        
    }

    private fun fetchMountains(token: String) {
        apiService.getMountain("Bearer $token")
            .enqueue(object : Callback<NationalMountainsResponse> {
                override fun onResponse(
                    call: Call<NationalMountainsResponse>,
                    response: Response<NationalMountainsResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("API_SUCCESS", "Response: $responseBody")
                            val mountains = responseBody.response?.body?.items?.item
                            if (!mountains.isNullOrEmpty()) {
                                adapter.setData(mountains)
                            } else {
                                Log.e("API_ERROR", "No mountains found")
                            }
                        } else {
                            Log.e("API_ERROR", "Response body is null")
                        }
                    } else {
                        Log.e("API_ERROR", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NationalMountainsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("API_ERROR", "Failure: ${t.message}")
                }
            })
    }
}