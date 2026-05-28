package com.example.gemini.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gemini.databinding.FragmentCalculatorBinding
import com.example.gemini.viewmodel.CalculatorViewModel

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.expression.observe(viewLifecycleOwner) {
            binding.tvExpression.text = it
        }
        viewModel.result.observe(viewLifecycleOwner) {
            binding.tvResult.text = it
        }
    }

    private fun setupClickListeners() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9,
            binding.btnDot
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                viewModel.onNumberClick(button.text.toString())
            }
        }

        binding.btnPlus.setOnClickListener { viewModel.onOperatorClick("+") }
        binding.btnMinus.setOnClickListener { viewModel.onOperatorClick("-") }
        binding.btnMultiply.setOnClickListener { viewModel.onOperatorClick("*") }
        binding.btnDivide.setOnClickListener { viewModel.onOperatorClick("/") }

        binding.btnClear.setOnClickListener { viewModel.onClearClick() }
        binding.btnBackspace.setOnClickListener { viewModel.onBackspaceClick() }
        binding.btnEquals.setOnClickListener { viewModel.onEqualsClick() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
