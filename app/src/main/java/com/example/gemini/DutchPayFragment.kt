package com.example.gemini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gemini.databinding.FragmentDutchPayBinding
import java.text.DecimalFormat

class DutchPayFragment : Fragment() {
    private var _binding: FragmentDutchPayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDutchPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMinus.setOnClickListener {
            val current = binding.etPeopleCount.text.toString().toIntOrNull() ?: 1
            if (current > 1) {
                binding.etPeopleCount.setText((current - 1).toString())
            }
        }

        binding.btnPlus.setOnClickListener {
            val current = binding.etPeopleCount.text.toString().toIntOrNull() ?: 1
            binding.etPeopleCount.setText((current + 1).toString())
        }

        binding.btnCalculateDutch.setOnClickListener {
            calculateDutchPay()
        }
    }

    private fun calculateDutchPay() {
        val amountStr = binding.etTotalAmount.text.toString()
        val peopleStr = binding.etPeopleCount.text.toString()

        if (amountStr.isNotEmpty() && peopleStr.isNotEmpty()) {
            val totalAmount = amountStr.toDouble()
            val peopleCount = peopleStr.toInt()

            if (peopleCount > 0) {
                val perPerson = totalAmount / peopleCount
                val formatter = DecimalFormat("#,###")
                binding.tvPerPersonAmount.text = "${formatter.format(perPerson)} 원"
                binding.cardResult.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
