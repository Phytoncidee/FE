package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.adapter.MountainAdapter
import com.example.hikinglog_fe.adapter.RegionAllMtnAdapter
import com.example.hikinglog_fe.adapter.RegionTop100Adapter
import com.example.hikinglog_fe.databinding.ActivityNationalMountainsBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.NationalMountainsResponse
import com.example.hikinglog_fe.models.RegionMountainResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NationalMountainsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNationalMountainsBinding
    private lateinit var apiService: ApiService
    private lateinit var adapter: MountainAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNationalMountainsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ApiService 초기화
        apiService = ApiService.create()

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)!!


        // 어댑터 초기화, ApiService 전달
        adapter = MountainAdapter(apiService, token)
        binding.allMountainsRecyclerView.adapter = adapter
        binding.allMountainsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Spinner 구현
        val items = resources.getStringArray(R.array.region_array)

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

        binding.spinner.adapter = myAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                if (position == 0) {
//                    // "전국" 선택 시 전국 목록 재호출
//                    fetchMountains(token)
//                } else if (position in 1..16) {
//                    // 지역 선택 시 지역별 산 목록 호출
//                    getMByRegion(position)
//                }
                if (position in 0..16) {
                    getMByRegion(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        // fetchMountains 호출
        fetchMountains(token)

        // SearchView 리스너 설정
        setOnQueryTextListener(token)

    }

    private fun getMByRegion(index: Int) {
//        if (index == 0) {
//            fetchMountains(token) // "전국" 선택 시 전국 목록 재호출
//            return
//        }

        apiService.getMtnByRegion("Bearer $token", index)
            .enqueue(object : Callback<RegionMountainResponse>{
                override fun onResponse(
                    call: Call<RegionMountainResponse>,
                    response: Response<RegionMountainResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("NationalMountainsActivity", "RegionMountains: $response")
                        Log.d("NationalMountainsActivity", "RegionMountains: ${response.body()}")

                        binding.allMountainsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                        binding.allMountainsRecyclerView.adapter = RegionAllMtnAdapter(this@NationalMountainsActivity, response.body()!!.data, token, apiService)
                        binding.allMountainsRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))
                    } else {
                        // 오류 처리
                        Log.e("NationalMountainsActivity", "RegionMountains Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RegionMountainResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("NationalMountainsActivity", "Failed to fetch data(RegionMountains)", t)
                }

            })

    }

    private fun setOnQueryTextListener(token: String) {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            // 검색 버튼 입력시 호출, 검색 버튼이 없으므로 사용안함
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            // 텍스트 입력, 수정시 호출
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    // 먼저 로컬 필터링을 시도
                    adapter.performSearch(newText)

                    // 만약 로컬 필터링에서 데이터가 없는 경우, API 호출을 통해 검색
                    if (adapter.itemCount == 0) {
                        searchMountain(token, newText)
                    }
                } else {
                    // 검색어가 비어있다면 전체 데이터를 다시 로드
                    fetchMountains(token)
                }
                return false
            }
        })
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
                                Log.e("NationalMountainsActivity", "No mountains found")
                            }
                        } else {
                            Log.e("NationalMountainsActivity", "Response body is null")
                        }
                    } else {
                        Log.e("NationalMountainsActivity", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NationalMountainsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("NationalMountainsActivity", "Failure: ${t.message}")
                }
            })
    }

    private fun searchMountain(token: String, searchText: String) {
        apiService.getMountainInfo("Bearer $token", searchText)
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
                                Log.e("NationalMountainsActivity", "No mountains found")
                                adapter.setData(emptyList()) // 검색 결과가 없을 때 빈 리스트로 업데이트
                            }
                        } else {
                            Log.e("NationalMountainsActivity", "Response body is null")
                        }
                    } else {
                        Log.e("NationalMountainsActivity", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NationalMountainsResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("NationalMountainsActivity", "Failure: ${t.message}")
                }
            })
    }

}