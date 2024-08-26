package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.models.MSearchResponse
import com.example.hikinglog_fe.models.Top100Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.databinding.ItemTop100MountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.NationalMountainsImageResponse
import com.example.hikinglog_fe.models.NationalMountainsResponse


class Top100ViewHolder(val binding: ItemTop100MountainBinding): RecyclerView.ViewHolder(binding.root)
class Top100Adapter(val context: Context, val datas:MutableList<Top100Item>?, private val token: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Top100ViewHolder(ItemTop100MountainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as Top100ViewHolder).binding
        val model = datas!![position]

        // [[산 정보]]
        binding.mntiname.text = model.frtrlNm
        binding.mntihigh.text = model.aslAltide
        binding.mntiadd.text = model.addrNm

        // [Top100 산 이름으로 검색한 전국산 items 저장할 변수 선언]
        var mountain: Mountain? = null

        // [[리사이클러 뷰 클릭 -> 산 상세 페이지로 이동]]
        binding.root.setOnClickListener {
            // [[Top100 산 이름 -> 전국산 검색]]
            // [Call객체를 통해 Retrofit 통신_요청]

            val call: Call<NationalMountainsResponse> = RetrofitConnection.xmlNetServ.getMountain(
                "Bearer $token",
                model.frtrlNm
            )

            // [Call객체를 통해 Retrofit 통신_응답] (return된 값 처리)
            call.enqueue(object : Callback<NationalMountainsResponse> {
                override fun onResponse(call: Call<NationalMountainsResponse>, response: Response<NationalMountainsResponse>) {
                    Log.d("mobileApp", "전국산 검색 response: $response")

                    if (response.isSuccessful) {
                        Log.d("mobileApp", "Top100 산 이름 -> 전국산 검색 response: $response")
                        if (response.body()?.response?.body?.items?.item?.get(0) != null) {
                            mountain = response.body()?.response?.body?.items?.item?.get(0)
                            // 성공적으로 데이터를 받아온 후에만 Intent를 생성
                            startMountainInfoActivity(mountain)
                        }
                    } else {
                        // 오류 처리
                        Log.e("mobileApp", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NationalMountainsResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(getTop100Mountains)", t)
                }
            })
        }
    }

    private fun startMountainInfoActivity(mountain: Mountain?) {
        mountain?.let {
            Intent(context, MountainInfoActivity::class.java).apply {
                putExtra("mountain", it) // 산 상세 페이지에서 사용할 "mountain" 전달
            }.run { context.startActivity(this) }
        } ?: run {
            Log.e("mobileApp", "Mountain data is null, cannot start MountainInfoActivity")
        }
    }
}