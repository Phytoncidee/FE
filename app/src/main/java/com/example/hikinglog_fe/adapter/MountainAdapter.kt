package com.example.hikinglog_fe.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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

class MountainAdapter(private val apiService: ApiService, private val token: String): RecyclerView.Adapter<MountainAdapter.MountainViewHolder>(), Filterable {

    private var datalist = mutableListOf<Mountain>()    // 원본 리스트
    private var filteredList = mutableListOf<Mountain>() // 필터링된 리스트

    fun setData(mntData: List<Mountain>) {
        datalist = mntData.toMutableList()
        filteredList = datalist // 필터링 리스트 초기화 (공백 또는 검색 기능을 사용하지 않을 때는 전체 리스트를 보여주기에 복제한 리스트의 초기값을 원본 리스트와 동일하게 선언)
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
        holder.bind(filteredList[position])
    }
    override fun getItemCount(): Int = filteredList.size

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
    override fun getFilter(): Filter {
        return object : Filter() {
            // 입력 받은 문자열에 대한 처리
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString().trim()
                // 공백, 아무런 값이 입력 되지 않았을 때는 원본 리스트
                filteredList = if (charString.isEmpty()) {
                    datalist
                } else {
                    val filteredResults = datalist.filter { mountain ->
                        mountain.mntiname!!.contains(charString, ignoreCase = true) ||
                                mountain.mntiadd!!.contains(charString, ignoreCase = true)
                    }
                    filteredResults.toMutableList()
                }

                val filterResults = FilterResults().apply {
                    values = filteredList
                }
                return filterResults
            }

            // 필터링 결과 적용
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Mountain>
                notifyDataSetChanged()
            }
        }
    }


    // 검색 텍스트가 변경 되면 Activity에서 호출됨
    fun performSearch(query: String) {
        filter.filter(query)
    }

}
