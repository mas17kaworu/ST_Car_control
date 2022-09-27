package com.longkai.stcarcontrol.st_exp.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.longkai.stcarcontrol.st_exp.databinding.ItemTrackingHistoryRecordBinding


class HistoryRecordsAdapter(
    private val data: List<HistoryRecord>,
    private val itemClickListener: (HistoryRecord) -> Unit
) : RecyclerView.Adapter<HistoryRecordsAdapter.HistoryRecordsVH>() {
    class HistoryRecordsVH(binding: ItemTrackingHistoryRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecordsVH {
        val binding = ItemTrackingHistoryRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryRecordsVH(binding)
    }

    override fun onBindViewHolder(holder: HistoryRecordsVH, position: Int) {
        val item = data[position]
        holder.name.text = item.recordName
        holder.itemView.setOnClickListener { itemClickListener.invoke(item) }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}