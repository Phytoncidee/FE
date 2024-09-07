package com.example.hikinglog_fe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hikinglog_fe.databinding.ItemHikingrecordBinding
import com.example.hikinglog_fe.models.HikingRecord


class HikingRecordAdapter(
    private val recordList: List<HikingRecord>,
    private val onItemClick: (HikingRecord) -> Unit
) : RecyclerView.Adapter<HikingRecordAdapter.HikingRecordViewHolder>() {

    inner class HikingRecordViewHolder(private val binding: ItemHikingrecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: HikingRecord) {
            binding.recordMountainName.text = "${record.mname}에서 ${record.number}번째 등산"
            binding.recordTime.text = formatDuration(record.time)
            binding.recordDate.text = record.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikingRecordViewHolder {
        val binding = ItemHikingrecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HikingRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HikingRecordViewHolder, position: Int) {
        val record = recordList[position]
        holder.bind(record)
        holder.itemView.setOnClickListener{ onItemClick(record) }
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    private fun formatDuration(time: Int): String {
        val hours = time / 60
        val remainingMinutes =  time % 60
        return "${hours}시간 ${remainingMinutes}분"
    }


}
