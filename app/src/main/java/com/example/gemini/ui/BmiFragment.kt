package com.example.gemini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gemini.databinding.FragmentBmiBinding
import com.example.gemini.viewmodel.BmiViewModel
import java.util.Locale

class BmiFragment : Fragment() {
    private var _binding: FragmentBmiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BmiViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.btnCalculateBmi.setOnClickListener {
            val heightStr = binding.etHeight.text.toString()
            val weightStr = binding.etWeight.text.toString()

            if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
                val height = heightStr.toDouble()
                val weight = weightStr.toDouble()
                viewModel.calculateBmi(height, weight)
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

    private fun setupObservers() {
        viewModel.bmiResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                binding.tvBmiValue.text = String.format(Locale.getDefault(), "%.1f", result.value)
                binding.tvBmiStatus.text = result.status
                binding.tvBmiStatus.setTextColor(result.color)
                binding.layoutResult.visibility = View.VISIBLE
            }
        }
        // Keep history LiveData active
        viewModel.history.observe(viewLifecycleOwner) { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
