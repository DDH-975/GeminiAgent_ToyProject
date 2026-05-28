package com.example.gemini

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gemini.databinding.FragmentBmiBinding
import java.util.Locale

class BmiFragment : Fragment() {
    private var _binding: FragmentBmiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBmiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCalculateBmi.setOnClickListener {
            calculateBmi()
        }
    }

    private fun calculateBmi() {
        val heightStr = binding.etHeight.text.toString()
        val weightStr = binding.etWeight.text.toString()

        if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
            val height = heightStr.toDouble() / 100 // cm to m
            val weight = weightStr.toDouble()
            val bmi = weight / (height * height)

            binding.tvBmiValue.text = String.format(Locale.getDefault(), "%.1f", bmi)
            
            val (status, color) = when {
                bmi < 18.5 -> "저체중" to Color.BLUE
                bmi < 23.0 -> "정상" to Color.GREEN
                bmi < 25.0 -> "과체중" to Color.parseColor("#FFA500") // Orange
                else -> "비만" to Color.RED
            }

            binding.tvBmiStatus.text = status
            binding.tvBmiStatus.setTextColor(color)
            binding.layoutResult.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
