package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.NationalMountainsImageResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MountainAdapter(private val apiService: ApiService, private val token: String): RecyclerView.Adapter<MountainAdapter.MountainViewHolder>()  {

    private var datalist = mutableListOf<Mountain>()

    fun setData(mntData: List<Mountain>) {
        datalist = mntData.toMutableList()
        notifyDataSetChanged()
    }

    // 만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MountainViewHolder {
        val view = ItemMountainBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MountainViewHolder(view)
    }

    // recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    // 적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MountainViewHolder, position: Int) {
        holder.bind(datalist[position])
    }
    override fun getItemCount(): Int = datalist.size

    inner class MountainViewHolder(private val binding: ItemMountainBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(mntData: Mountain) {
            binding.mntiname.text = mntData.mntiname
            binding.mntiadd.text = mntData.mntiadd
            binding.mntihigh.text = mntData.mntihigh.toString()

            // 기본 이미지 설정
            binding.mntImg.setImageResource(R.drawable.etc_default_mountain)

            binding.root.setOnClickListener{
                val context = binding.root.context
                val intent = Intent(context, MountainInfoActivity::class.java).apply {
                    putExtra("mountain", mntData)
                }
                context.startActivity(intent)
                Log.d("MountainAdapter", "OnClick - Mountain: $mntData")
            }

            // 이미지 API 호출
            apiService.getMountainImage("Bearer $token", mntData.mntilistno).enqueue(object :
                Callback<NationalMountainsImageResponse> {
                override fun onResponse(call: Call<NationalMountainsImageResponse>, response: Response<NationalMountainsImageResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val images = responseBody.response?.body?.items?.item
                            if (!images.isNullOrEmpty()) {
                                // 이미지 URL을 설정
                                val imageUrl = "https://www.forest.go.kr/images/data/down/mountain/${images[0].imgfilename}"

                                Log.d("MountainAdapter", "Image URL: $imageUrl")

                                Glide.with(binding.root.context)
                                    .load(imageUrl)
                                    .error(R.drawable.etc_default_mountain) // 로드 실패 시 이미지
                                    .into(binding.mntImg)

                                // 액티비티를 클릭하면 이미지 URL도 전달
                                binding.root.setOnClickListener {
                                    val context = binding.root.context
                                    val intent = Intent(context, MountainInfoActivity::class.java).apply {
                                        putExtra("mountain", mntData)
                                        putExtra("image_url", imageUrl) // 이미지 URL을 추가로 전달
                                    }
                                    context.startActivity(intent)
                                    Log.d("MountainAdapter", "OnClick - Mountain: $mntData, Image URL: $imageUrl")
                                }
                            }

                        } else {
                            Log.e("API_ERROR", "Image response body is null")
                        }
                    } else {
                        Log.e("API_ERROR", "Image response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NationalMountainsImageResponse>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("API_ERROR", "Image API failure: ${t.message}")
                }
            })
        }
    }
}