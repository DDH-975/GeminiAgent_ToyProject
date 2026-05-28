package com.example.gemini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gemini.databinding.FragmentDutchPayBinding
import com.example.gemini.viewmodel.DutchPayViewModel
import java.text.DecimalFormat

class DutchPayFragment : Fragment() {
    private var _binding: FragmentDutchPayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DutchPayViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDutchPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        binding.btnMinus.setOnClickListener {
            viewModel.decrementPeople()
        }

        binding.btnPlus.setOnClickListener {
            viewModel.incrementPeople()
        }

        binding.etPeopleCount.addTextChangedListener {
            val count = it.toString().toIntOrNull() ?: 1
            viewModel.setPeopleCount(count)
        }

        binding.btnCalculateDutch.setOnClickListener {
            val amountStr = binding.etTotalAmount.text.toString()
            if (amountStr.isNotEmpty()) {
                viewModel.calculateDutchPay(amountStr.toDouble())
            }
        }
    }

    private fun setupObservers() {
        viewModel.peopleCount.observe(viewLifecycleOwner) { count ->
            if (binding.etPeopleCount.text.toString() != count.toString()) {
                binding.etPeopleCount.setText(count.toString())
            }
        }

        viewModel.perPersonAmount.observe(viewLifecycleOwner) { amount ->
            if (amount != null) {
                val formatter = DecimalFormat("#,###")
                binding.tvPerPersonAmount.text = "${formatter.format(amount)} 원"
                binding.cardResult.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
