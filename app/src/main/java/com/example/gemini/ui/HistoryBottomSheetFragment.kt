package com.example.gemini.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gemini.data.HistoryEntity
import com.example.gemini.databinding.DialogHistoryDetailBinding
import com.example.gemini.databinding.ItemHistoryBinding
import com.example.gemini.databinding.LayoutHistoryBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var historyList: List<HistoryEntity>,
    private val onItemClick: (HistoryEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        
        // 첫 줄을 제목처럼, 나머지는 생략하거나 요약해서 리스트에 표시
        val lines = item.expression.lineSequence().toList()
        val displayText = if (lines.size > 1) {
            lines[0] // 첫 줄 (제품명이나 요약)
        } else {
            item.expression
        }
        
        holder.binding.tvHistoryExpression.text = displayText
        holder.binding.tvHistoryResult.text = item.result
        holder.binding.tvHistoryResult.setTextColor(Color.BLACK)
        
        // 타입에 따른 아이콘 설정
        val iconRes = when (item.type) {
            "CALCULATOR" -> android.R.drawable.ic_menu_edit
            "GPA" -> android.R.drawable.ic_menu_sort_alphabetically
            "BMI" -> android.R.drawable.ic_menu_myplaces
            "DUTCH_PAY" -> android.R.drawable.ic_menu_share
            "DISCOUNT" -> android.R.drawable.ic_menu_view
            else -> android.R.drawable.ic_menu_info_details
        }
        holder.binding.ivHistoryType.setImageResource(iconRes)
        
        val sdf = SimpleDateFormat("a h:mm", Locale.getDefault())
        holder.binding.tvHistoryTime.text = sdf.format(Date(item.timestamp))
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = historyList.size

    fun updateData(newList: List<HistoryEntity>) {
        this.historyList = newList
        notifyDataSetChanged()
    }
}

class HistoryBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: LayoutHistoryBottomSheetBinding? = null
    private val binding get() = _binding!!
    
    private var historyLiveData: LiveData<List<HistoryEntity>>? = null
    private var onClearClick: (() -> Unit)? = null

    fun setHistorySource(liveData: LiveData<List<HistoryEntity>>, onClear: () -> Unit) {
        this.historyLiveData = liveData
        this.onClearClick = onClear
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutHistoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryAdapter(emptyList()) { historyItem ->
            showDetailDialog(historyItem)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        historyLiveData?.observe(viewLifecycleOwner) { historyData ->
            if (historyData.isNullOrEmpty()) {
                binding.rvHistory.visibility = View.GONE
                binding.tvEmptyHistory.visibility = View.VISIBLE
            } else {
                binding.rvHistory.visibility = View.VISIBLE
                binding.tvEmptyHistory.visibility = View.GONE
                adapter.updateData(historyData)
            }
        }

        binding.btnClearHistory.setOnClickListener {
            onClearClick?.invoke()
        }
    }

    private fun showDetailDialog(item: HistoryEntity) {
        val dialogBinding = DialogHistoryDetailBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.tvDetailTitle.text = when(item.type) {
            "GPA" -> "학점 상세 내역"
            "BMI" -> "BMI 측정 기록"
            "DUTCH_PAY" -> "더치페이 상세"
            "DISCOUNT" -> "할인 계산 상세"
            else -> "상세 기록"
        }

        // 아이콘 설정
        val iconRes = when (item.type) {
            "CALCULATOR" -> android.R.drawable.ic_menu_edit
            "GPA" -> android.R.drawable.ic_menu_sort_alphabetically
            "BMI" -> android.R.drawable.ic_menu_myplaces
            "DUTCH_PAY" -> android.R.drawable.ic_menu_share
            "DISCOUNT" -> android.R.drawable.ic_menu_view
            else -> android.R.drawable.ic_menu_info_details
        }
        dialogBinding.ivDetailIcon.setImageResource(iconRes)

        // GPA의 경우 첫 줄(요약)을 제외한 내용을 본문에 표시, 나머지는 그대로 표시
        if (item.type == "GPA") {
            val lines = item.expression.lineSequence().toList()
            if (lines.size > 2) {
                dialogBinding.tvDetailContent.text = lines.drop(2).joinToString("\n")
            } else {
                dialogBinding.tvDetailContent.text = item.expression
            }
        } else {
            // 할인 계산기의 경우 제품명이 있으면 강조해서 보여줄 수도 있지만, 일단 전체 텍스트 표시
            dialogBinding.tvDetailContent.text = item.expression
        }

        dialogBinding.tvDetailResult.text = item.result
        
        dialogBinding.btnCloseDetail.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HistoryBottomSheetFragment"
    }
}
