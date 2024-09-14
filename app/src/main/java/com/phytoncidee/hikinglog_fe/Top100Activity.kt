package com.phytoncidee.hikinglog_fe

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.phytoncidee.hikinglog_fe.adapter.RegionTop100Adapter
import com.phytoncidee.hikinglog_fe.adapter.Top100Adapter
import com.phytoncidee.hikinglog_fe.databinding.ActivityTop100Binding
import com.phytoncidee.hikinglog_fe.models.GetRegionTop100Response
import com.phytoncidee.hikinglog_fe.models.Top100Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Top100Activity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String
    private lateinit var binding: ActivityTop100Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTop100Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("userToken", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)!!

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
                if (position in 0..16) {
                    getRegionTop100(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    } //onCreate()

    fun getRegionTop100(index : Int){
        // [Call객체를 통해 Retrofit 통신_요청]
        val call: Call<GetRegionTop100Response> = RetrofitConnection.jsonNetServ.getRegionTop100Mountains(
            "Bearer $token",
            index
        )

        // [Call객체를 통해 Retrofit 통신_응답] (return된 값 처리)
        call.enqueue(object : Callback<GetRegionTop100Response> {
            override fun onResponse(call: Call<GetRegionTop100Response>, response: Response<GetRegionTop100Response>) {

                if (response.isSuccessful) {

                    Log.d("mobileApp", "getRegionTop100Mountains: $response")
                    Log.d("mobileApp", "getRegionTop100Mountains: ${response.body()}")

                    binding.top100RecyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    binding.top100RecyclerView.adapter = RegionTop100Adapter(this@Top100Activity, response.body()!!.data, token!!)
                    binding.top100RecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

                } else {
                    // 오류 처리
                    Log.e("mobileApp", "getRegionTop100Mountains Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetRegionTop100Response>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("mobileApp", "Failed to fetch data(getRegionTop100Mountains)", t)
            }
        })
    }
}