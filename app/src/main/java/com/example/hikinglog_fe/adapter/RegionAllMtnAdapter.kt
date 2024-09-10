package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.MountainDetail
import com.example.hikinglog_fe.models.MountainDetailResponse
import com.example.hikinglog_fe.models.MountainsByRegion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegionAllMtnViewHolder(val binding: ItemMountainBinding) :RecyclerView.ViewHolder(binding.root)

class RegionAllMtnAdapter(
    val context: Context,
    val datas: MutableList<MountainsByRegion>?,
    private val token: String,
    private val apiService: ApiService
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegionAllMtnViewHolder(ItemMountainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as RegionAllMtnViewHolder).binding
        val model = datas!![position]

        binding.mntiname.text = model.mntiname
        binding.mntihigh.text = model.mntihigh.toString()
        binding.mntiadd.text = model.mntiadd

        // 기본 이미지 설정
        binding.mntImg.setImageResource(R.drawable.etc_default_mountain)

        // 산 상세 이동
        binding.root.setOnClickListener {
            Log.d(
                "RegionAllMtnAdapter",
                "Mountain 얻어올 데이터:  ${model.mntiname}/${model.mntihigh}/${model.mntiadd}"
            )

            apiService.getMountainDetail("Bearer $token", model.mntiname, model.mntilistno)
                .enqueue(object : Callback<MountainDetailResponse> {
                    override fun onResponse(
                        call: Call<MountainDetailResponse>,
                        response: Response<MountainDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()?.data
                            if (data != null) {
                                // Mountain 객체 생성
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

                                // 이미지 로드
                                Log.d("RegionAllMtnAdapter", "Image URL: ${data.mimage}")
                                Glide.with(binding.root.context)
                                    .load(data.mimage)
                                    .error(R.drawable.etc_default_mountain) // 로드 실패 시 이미지
                                    .into(binding.mntImg)

                                Log.d("RegionAllMtnAdapter", "Mountain data: $mountain")

                                // MountainInfoActivity로 이동
                                val intent =
                                    Intent(context, MountainInfoActivity::class.java).apply {
                                        putExtra("mountain", mountain)
                                        putExtra("image_url", data.mimage)

                                    }
                                context.startActivity(intent)
                            } else {
                                Log.e("RegionAllMtnAdapter", "Response data is null")
                            }
                        } else {
                            Log.e(
                                "RegionAllMtnAdapter",
                                "getMountainDetail Error: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<MountainDetailResponse>, t: Throwable) {
                        Log.e("RegionAllMtnAdapter", "Failed to fetch mountain detail", t)
                    }
                })
        }

    }
}
