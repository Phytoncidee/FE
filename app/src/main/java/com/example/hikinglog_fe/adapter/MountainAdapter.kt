package com.example.hikinglog_fe.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.MountainInfoActivity
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.databinding.ItemMountainBinding
import com.example.hikinglog_fe.interfaces.ApiService
import com.example.hikinglog_fe.models.Mountain
import com.example.hikinglog_fe.models.MountainAddBookmarkErrorResponse
import com.example.hikinglog_fe.models.MountainAddBookmarkRequest
import com.example.hikinglog_fe.models.MountainAddBookmarkResponse
import com.example.hikinglog_fe.models.MountainCheckBookmarkResponse
import com.example.hikinglog_fe.models.MountainDeleteBookmarkResponse
import com.example.hikinglog_fe.models.MountainDetailResponse
import com.example.hikinglog_fe.models.NationalMountainsImageResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MountainAdapter(private val apiService: ApiService, private val token: String): RecyclerView.Adapter<MountainAdapter.MountainViewHolder>(), Filterable {

    private var datalist = mutableListOf<Mountain>()    // 원본 리스트
    private var filteredList = mutableListOf<Mountain>() // 필터링된 리스트
    private var imageUrl: String? = null

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

            // 이미지 API 호출
            apiService.getMountainImage("Bearer $token", mntData.mntilistno).enqueue(object :
                Callback<NationalMountainsImageResponse> {
                override fun onResponse(
                    call: Call<NationalMountainsImageResponse>,
                    response: Response<NationalMountainsImageResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val images = responseBody.response?.body?.items?.item
                            if (!images.isNullOrEmpty()) {
                                // 이미지 URL을 설정
                                imageUrl =
                                    "https://www.forest.go.kr/images/data/down/mountain/${images[0].imgfilename}"

                                Log.d("MountainAdapter", "Image URL: $imageUrl")

                                Glide.with(binding.root.context)
                                    .load(imageUrl)
                                    .error(R.drawable.etc_default_mountain) // 로드 실패 시 이미지
                                    .into(binding.mntImg)
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

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, MountainInfoActivity::class.java).apply {
                    putExtra("mountain", mntData)
                    putExtra("image_url", imageUrl)
                }
                context.startActivity(intent)
                Log.d("MountainAdapter", "OnClick - Mountain: $mntData")
            }


            // 산 즐겨찾기
            var isBookmarked : Boolean = false

            // 산 즐겨찾기 조회
            val call = apiService.getMtnBookmark("Bearer $token", 2147483647, 0)
            call.enqueue(object : Callback<MountainCheckBookmarkResponse> {
                override fun onResponse(
                    call: Call<MountainCheckBookmarkResponse>,
                    response: Response<MountainCheckBookmarkResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("mobileApp", "getMountainBookmark: $response")
                        Log.d("mobileApp", "getMountainBookmark: ${response.body()!!.data.bookmarkList}")
                        // 즐겨찾기 여부 저장
                        for (i in 0..response.body()!!.data.bookmarkList.size - 1) { // bookmarkList에 해당 산이 존재하는지 확인
                            if (response.body()!!.data.bookmarkList[i].mntilistno == mntData.mntilistno) {
                                isBookmarked = true // false -> true 변경
                                Log.d(
                                    "mobileApp",
                                    "${response.body()!!.data.bookmarkList[i].id}: isBookmarked가 true로 변경!!"
                                )
                            }
                        }

                        // 즐겨찾기 버튼 표시
                        if (isBookmarked == true) { // 등록
                            binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                            Log.d(
                                "mobileApp",
                                "${mntData.mntilistno}: isBookmarked가 true로 유지되는 중!!"
                            )
                        } else { // 미등록
                            binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                        }
                    } else {
                        // 오류처리
                        Log.e("mobileApp", "getMountainBookmark: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MountainCheckBookmarkResponse>, t: Throwable) {
                    // 네트워크 오류 처리
                    Log.e("mobileApp", "Failed to fetch data(getMountainBookmark)", t)
                }
            })

            // 즐겨찾기 등록/삭제 -> 표시
            binding.btnMBookmark.setOnClickListener {
                if (isBookmarked == false) {    // 미등록
                    binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_on)
                    isBookmarked = true

                    // 즐겨찾기 등록
                    val bookmarkRequest = MountainAddBookmarkRequest(
                        name = mntData.mntiname ?: "",
                        location = mntData.mntiadd ?: "",
                        info = mntData.mntidetails ?: "",
                        high = mntData.mntihigh ?: 0.0,
                        image = imageUrl ?: ""
                    )
                    apiService.addMtnBookmark("Bearer $token", mntData.mntilistno, bookmarkRequest)
                        .enqueue(object : Callback<MountainAddBookmarkResponse> {
                            override fun onResponse(
                                call: Call<MountainAddBookmarkResponse>,
                                response: Response<MountainAddBookmarkResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("mobileApp", "postMountainBookmark: $response")
                                    Toast.makeText(binding.root.context, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    // 오류 응답 처리
                                    val errorResponse = response.errorBody()?.string()
                                    val errorDetails = Gson().fromJson(errorResponse, MountainAddBookmarkErrorResponse::class.java)
                                    Log.e("mobileApp", "postMountainBookmark Error: ${errorDetails.message}")
                                }
                            }

                            override fun onFailure(
                                call: Call<MountainAddBookmarkResponse>,
                                t: Throwable
                            ) {
                                // 네트워크 오류 처리
                                Log.e("mobileApp", "Failed to fetch data(postMountainBookmark)", t)
                            }

                        })
                } else {    // 등록
                    binding.btnMBookmark.setImageResource(android.R.drawable.btn_star_big_off)
                    isBookmarked = false

                    // 즐겨찾기 삭제
                    apiService.deleteMtnBookmark("Bearer $token", mntData.mntilistno).enqueue(object : Callback<MountainDeleteBookmarkResponse> {
                        override fun onResponse(
                            call: Call<MountainDeleteBookmarkResponse>,
                            response: Response<MountainDeleteBookmarkResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("mobileApp", "deleteMountainBookmark: $response")
                                Toast.makeText(binding.root.context, "즐겨찾기에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                // 오류 처리
                                Log.e("mobileApp", "deleteMountainBookmark: ${response.code()}")
                            }
                        }

                        override fun onFailure(
                            call: Call<MountainDeleteBookmarkResponse>,
                            t: Throwable
                        ) {
                            // 네트워크 오류 처리
                            Log.e("mobileApp", "Failed to fetch data(deleteMountainBookmark)", t)
                        }
                    })
                }
            }
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
