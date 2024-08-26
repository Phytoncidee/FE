package com.example.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
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
import com.example.hikinglog_fe.adapter.Top100Adapter
import com.example.hikinglog_fe.databinding.ActivityTop100Binding
import com.example.hikinglog_fe.models.Top100Response
import com.example.hikinglog_fe.utils.parseEscapedJson
import com.google.gson.annotations.JsonAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Top100Activity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTop100Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // [Call객체를 통해 Retrofit 통신_요청]
        val call: Call<Top100Response> = RetrofitConnection.xmlNetServ.getTop100Mountains(
            "Bearer $token"
        )

        // [Call객체를 통해 Retrofit 통신_응답] (return된 값 처리)
        call.enqueue(object : Callback<Top100Response> {
            override fun onResponse(call: Call<Top100Response>, response: Response<Top100Response>) {

                if (response.isSuccessful) {

                    Log.d("mobileApp", "$response")
                    Log.d("mobileApp", "${response.body()}")

                    binding.top100RecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.top100RecyclerView.adapter = Top100Adapter(this@Top100Activity, response.body()!!.body!!.items!!.item, token!!)
                    binding.top100RecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Top100Response>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getTop100Mountains)", t)
            }
        })



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