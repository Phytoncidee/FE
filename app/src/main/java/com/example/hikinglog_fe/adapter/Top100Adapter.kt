package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.NationalMountainsActivity
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.models.Top100Item
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.hikinglog_fe.databinding.ItemTop100MountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.MountainDetailResponse

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
            // [Top100 산 이름, 코드 =>  산 상세 api로 Mountian Data Class 얻어오기]
            // [Retrofit 통신 요청: 숙소 목록 검색]
            Log.d("mobileApp", "Mountian 얻어올 데이터 있는지 확인: ${model.frtrlNm}/${model.mtnCd}/${model.addrNm}")
            val call: Call<MountainDetailResponse> = RetrofitConnection.jsonNetServ.getMountainDetail(
                "Bearer $token",
                model.frtrlNm,
                model.mtnCd
            )
            // [Retrofit 통신 응답: 숙소 목록 검색]
            call.enqueue(object : Callback<MountainDetailResponse> {
                override fun onResponse(call: Call<MountainDetailResponse>, response: Response<MountainDetailResponse>) {
                    if (response.isSuccessful) {
                        Log.d("mobileApp", "getMountainDetail: $response")

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
                        // >> 산 상세 페이지에 data 넘겨주기 <<
                        Intent(context, MountainInfoActivity::class.java).apply {
                            putExtra("mountain", mountain) // 산 상세 페이지에서 사용할 "mountain" 전달
                        }.run { context.startActivity(this) }

                    } else {
                        // 오류 처리
                        Log.e("mobileApp", "getMountainDetail Error: ${response.code()}")

                        // data 없는 경우 >>전국 산 검색창으로 이동<< -> 검색 결과 중 직접 선택
                        Intent(context, NationalMountainsActivity::class.java).apply {
                            putExtra("mountainName", binding.mntiname.text) // 산 이름 전달
                        }.run { context.startActivity(this) }
                        Toast.makeText(context, "검색 결과를 통해 상세 정보를 열람하세요!", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<MountainDetailResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(getMountainDetail)", t)
                }
            })

        }
    }
}
