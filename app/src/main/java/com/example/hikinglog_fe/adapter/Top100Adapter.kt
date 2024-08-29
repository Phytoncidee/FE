package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.models.Top100Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.hikinglog_fe.databinding.ItemTop100MountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.NationalMountainsResponse

private lateinit var apiService: ApiService

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

        // [[산 클릭 시 산 상세로 이동]]
        binding.root.setOnClickListener {
            // [Top100 산 이름으로 검색 => 전국산 mountain 변수에 저장]
            // ApiService 초기화
            apiService = ApiService.create()
            apiService.getMountainInfo("Bearer $token", model.frtrlNm!!)
                .enqueue(object : Callback<NationalMountainsResponse> {
                    override fun onResponse(
                        call: Call<NationalMountainsResponse>,
                        response: Response<NationalMountainsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null) {
                                Log.d("mobileApp", "getMountainInfo Response: $responseBody")
                                val mountain = responseBody.response?.body?.items?.item?.get(0) //첫번째 검색 결과 mountain에 저장

                                Intent(context, MountainInfoActivity::class.java).apply {
                                    putExtra("mountain", mountain) // 산 상세 페이지에서 사용할 "mountain" 전달
                                }.run { context.startActivity(this) }
                            }
                        } else {
                            Log.e("mobileApp", "getMountainInfo Response code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<NationalMountainsResponse>, t: Throwable) {
                        t.printStackTrace()
                        Log.e("mobileApp", "getMountainInfo Failure: ${t.message}")
                    }
                })

        }
    }
}
