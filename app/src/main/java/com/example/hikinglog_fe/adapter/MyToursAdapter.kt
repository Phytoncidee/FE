package com.example.hikinglog_fe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hikinglog_fe.CommentFragment
import com.example.hikinglog_fe.R
import com.example.hikinglog_fe.RetrofitConnection
import com.example.hikinglog_fe.databinding.ItemMytourismBinding
import com.example.hikinglog_fe.databinding.ItemPostBinding
import com.example.hikinglog_fe.models.CommunityPost
import com.example.hikinglog_fe.models.MyTourLResponse
import com.example.hikinglog_fe.models.PostLikeCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyToursHolder(val binding: ItemMytourismBinding): RecyclerView.ViewHolder(binding.root)
class MyToursAdapter(val context: Context, val datas:List<MyTourLResponse>?, val fragmentManager: FragmentManager, private val token: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyToursHolder(ItemMytourismBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyToursHolder).binding
        val model = datas!![position]

        // 마이관광 이름
        binding.mytourismName.text = model.tourTitle
        // 마이관광 상태
        var status = model.status
        if(status == "COMPLETED"){
            binding.mytourismState.setImageResource(R.drawable.button_complete)
        }


        binding.mytourismState.setOnClickListener {
            if(status == "COMPLETED"){ //완료 -> 예정
                status = "PREPARING"
                binding.mytourismState.setImageResource(R.drawable.button_planned)
                // [Retrofit 통신 요청: 마이 관광 상태변화]
                val call: Call<Response<Void>> = RetrofitConnection.jsonNetServ.putStatus(
                    "Bearer $token",
                    model.tourId,
                    "PREPARING"
                    )

                // [Retrofit 통신 응답: 마이 관광 상태변화]
                call.enqueue(object : Callback<Response<Void>> {
                    override fun onResponse(call: Call<Response<Void>>, response: Response<Response<Void>>) {

                        if (response.isSuccessful) {
                            Log.d("mobileApp", "putStatus: $response")
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("mobileApp", "putStatus Error: ${response.code()}, Error Body: ${errorBody}")
                        }
                    }
                    override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(putStatus)", t)
                    }
                })
            }
            else { // 예정 -> 완료
                status = "COMPLETED"
                binding.mytourismState.setImageResource(R.drawable.button_complete)
                // [Retrofit 통신 요청: 마이 관광 상태변화]
                val callC: Call<Response<Void>> = RetrofitConnection.jsonNetServ.putStatus(
                    "Bearer $token",
                    model.tourId,
                    "COMPLETED"
                )
                // [Retrofit 통신 응답: 마이 관광 상태변화]
                callC.enqueue(object : Callback<Response<Void>> {
                    override fun onResponse(call: Call<Response<Void>>, response: Response<Response<Void>>) {
                        if (response.isSuccessful) {
                            Log.d("mobileApp", "putStatus: $response")
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("mobileApp", "putStatus Error: ${response.code()}, Error Body: ${errorBody}")
                        }
                    }
                    override fun onFailure(call: Call<Response<Void>>, t: Throwable) {
                        // 네트워크 오류 처리
                        Log.e("mobileApp", "Failed to fetch data(putStatus)", t)
                    }
                })
            }
        }
    }
}