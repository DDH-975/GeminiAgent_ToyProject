package com.example.gemini.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gemini.databinding.FragmentDiscountBinding
import com.example.gemini.viewmodel.DiscountViewModel
import java.text.DecimalFormat

class DiscountFragment : Fragment() {
    private var _binding: FragmentDiscountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiscountViewModel by viewModels()
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDiscountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupInputFilters()

        binding.btnCalculateDiscount.setOnClickListener {
            val productName = binding.etProductName.text.toString()
            val priceStr = binding.etOriginalPrice.text.toString().replace(",", "")
            val rateStr = binding.etDiscountRate.text.toString()

            if (priceStr.isNotEmpty() && rateStr.isNotEmpty()) {
                viewModel.calculateDiscount(productName, priceStr.toDouble(), rateStr.toDouble())
            }
        }

        binding.btnHistory.setOnClickListener {
            val historyFragment = HistoryBottomSheetFragment()
            historyFragment.setHistorySource(viewModel.history) {
                viewModel.clearHistory()
            }
            historyFragment.show(parentFragmentManager, HistoryBottomSheetFragment.TAG)
        }
    }

    private fun setupInputFilters() {
        binding.etOriginalPrice.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.etOriginalPrice.removeTextChangedListener(this)
                    
                    val cleanString = s.toString().replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = decimalFormat.format(parsed)
                        current = formatted
                        binding.etOriginalPrice.setText(formatted)
                        binding.etOriginalPrice.setSelection(formatted.length)
                    } else {
                        current = ""
                    }
                    
                    binding.etOriginalPrice.addTextChangedListener(this)
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                binding.tvFinalPrice.text = "${decimalFormat.format(result.finalPrice)} 원"
                binding.tvSavedAmount.text = "${decimalFormat.format(result.savedAmount)}원 할인받음"
                binding.cardResult.visibility = View.VISIBLE
            }
        }
        viewModel.history.observe(viewLifecycleOwner) { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
