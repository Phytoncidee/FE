package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.MountainDetailResponse
import com.example.hikinglog_fe.models.MountainsByRegion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var apiService: ApiService

class RegionAllMtnViewHolder(val binding: ItemMountainBinding): RecyclerView.ViewHolder(binding.root)
class RegionAllMtnAdapter(
    val context: Context,
    val datas:MutableList<MountainsByRegion>?,
    private val token: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegionAllMtnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as RegionAllMtnViewHolder).binding
        val model = datas!![position]

        // 산 정보
        binding.mntiname.text = model.mntiname
        binding.mntihigh.text = model.mntihigh.toString()
        binding.mntiadd.text = model.mntiadd

        // 산 상세 이동
        binding.root.setOnClickListener {
            apiService.getMountainDetail("Bearer $token", model.mntiname, model.mntiadd)
                .enqueue(object : Callback<MountainDetailResponse> {
                    override fun onResponse(
                        call: Call<MountainDetailResponse>,
                        response: Response<MountainDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()!!.data
                            val mountain = Mountain(
                                mntiadd = data.mntiadd,
                                mntiadmin = data.mntiadmin,
                                mntiadminnum = data.mntiadminnum,
                                mntidetails = data.mntidetails,
                                mntihigh = data.mntihigh,
                                mntilistno = data.mntilistno,
                                mntiname = data.mntiname,
                                mntinfdt = data.mntinfdt,
                                mntisname = data.mntisname,
                                mntisummary = data.mntisummary,
                                mntitop = data.mntitop
                            )

                            Log.d("mobileApp", "산 상세로 넘어가는 Mountian: $mountain")
                            Intent(context, MountainInfoActivity::class.java).apply {
                                putExtra("mountain", mountain) // 산 상세 페이지에서 사용할 "mountain" 전달
                            }.run { context.startActivity(this) }
                        } else {
                            // 오류 처리
                            Log.e("mobileApp", "getMountainDetail Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<MountainDetailResponse>, t: Throwable) {
                        Log.e("mobileApp", "Failed to fetch data(getMountainDetail)", t)
                    }

                })
        }
    }

}