package com.example.hikinglog_fe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hikinglog_fe.databinding.ActivityTop100Binding
import com.example.hikinglog_fe.models.Top100Response
import com.example.hikinglog_fe.utils.parseEscapedJson
import com.google.gson.annotations.JsonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Top100Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTop100Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // [Call객체를 통해 Retrofit 통신_요청]
        val call : Call<List<String>> = RetrofitConnection.jsonNetServ.getTop100Mountains(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMjY1NTA0NCwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.nAMA6Nen6yQTe2C8vp9h0dStxK22f0g9aN2L7XH1_GI8xw0nOpfpo_yenQ4mb4MQyN2ypQZAdDNihc7txZdFqA"
        )
        // [Call객체를 통해 Retrofit 통신_응답] (return된 값 처리)
        call?.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val escapedJsonList = response.body() ?: emptyList()
                    val top100ResponseList = parseEscapedJson(escapedJsonList)

                    // 이제 top100ResponseList를 사용합니다.
                    // 예를 들어, mntiname, mntihigh, mntiadd 필드에 접근
                    top100ResponseList.forEach { top100Response ->
                        top100Response.response.body.items.item.forEach { mountainItem ->
                            val name = mountainItem.mntiname
                            val height = mountainItem.mntihigh
                            val address = mountainItem.mntiadd
                            // 필요한 작업 수행
                            Log.d("mobileApp", "Name: $name, Height: $height, Address: $address")
                        }
                    }
                } else {
                    // 오류 처리
                    Log.e("mobileApp", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data", t)
            }
        })

//        // Retrofit API 호출
//        RetrofitConnection.jsonNetServ.getTop100Mountains(
//            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMjY1NTA0NCwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.nAMA6Nen6yQTe2C8vp9h0dStxK22f0g9aN2L7XH1_GI8xw0nOpfpo_yenQ4mb4MQyN2ypQZAdDNihc7txZdFqA"
//        ).enqueue(object : Callback<List<String>> {
//            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
//                if (response.isSuccessful) {
//                    val escapedJsonList = response.body() ?: emptyList()
//                    val top100ResponseList = parseEscapedJson(escapedJsonList)
//
//                    // 이제 top100ResponseList를 사용합니다.
//                    // 예를 들어, mntiname, mntihigh, mntiadd 필드에 접근
//                    top100ResponseList.forEach { top100Response ->
//                        top100Response.response.body.items.item.forEach { mountainItem ->
//                            val name = mountainItem.mntiname
//                            val height = mountainItem.mntihigh
//                            val address = mountainItem.mntiadd
//                            // 필요한 작업 수행
//                            Log.d("MountainInfo", "Name: $name, Height: $height, Address: $address")
//                        }
//                    }
//                } else {
//                    // 오류 처리
//                    Log.e("APIError", "Error: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<String>>, t: Throwable) {
//                // 네트워크 오류 처리
//                Log.e("NetworkError", "Failed to fetch data", t)
//            }
//        })

//        // [Call객체를 통해 Retrofit 통신_요청]
//        val call : Call<List<Top100Response>> = RetrofitConnection.jsonNetServ.getTop100Mountains(
//            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMUBuYXZlci5jb20iLCJ1aWQiOjEsImV4cCI6MTcyMjY1NTA0NCwiZW1haWwiOiJ1c2VyMUBuYXZlci5jb20ifQ.nAMA6Nen6yQTe2C8vp9h0dStxK22f0g9aN2L7XH1_GI8xw0nOpfpo_yenQ4mb4MQyN2ypQZAdDNihc7txZdFqA"
//        )
//        // [Call객체를 통해 Retrofit 통신_응답] (return된 값 처리)
//        call?.enqueue(object: Callback<List<Top100Response>> {
//            override fun onResponse(
//                call: Call<List<Top100Response>>,
//                response: Response<List<Top100Response>>
//            ) { // 정상적으로 return 받음. response라는 객체에 요청한 값 담겨옴.
//                if(response.isSuccessful){ //먼저 성공적으로 왔는지 확인
//                    Log.d("mobileApp", "$response")
//                    Log.d("mobileApp", "${response.body()}")
//
//                    // recyclerView에 보여주기: adapter 연결(별도의 kt 파일에 adpater 생성) / layoutManager 설정
////                    binding.jsonRecyclerView.adapter = JsonAdapter(response.body()?.response!!.body!!.items) //전달받는 mutableList의 타입 넘겨줘야 함.
////                    binding.jsonRecyclerView.layoutManager = LinearLayoutManager(activity)
////                    binding.jsonRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
//                }
//            }
//
//            override fun onFailure(call: Call<List<Top100Response>>, t: Throwable) { // 통신 과정에서 오류
//                Log.d("mobileApp", "onFailure: ${t.message}")
//            }
//        })


        // [Spinner 구현]
        val items = resources.getStringArray(R.array.region_array)

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

        binding.spinner.adapter = myAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

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
    }
}