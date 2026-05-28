package com.example.gemini

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    private val _expression = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result

    private var currentNumber = ""
    private var operator = ""
    private var firstOperand: Double? = null

    fun onNumberClick(number: String) {
        currentNumber += number
        _expression.value = (_expression.value ?: "") + number
    }

    fun onOperatorClick(op: String) {
        if (currentNumber.isNotEmpty()) {
            firstOperand = currentNumber.toDoubleOrNull()
            operator = op
            currentNumber = ""
            _expression.value = (_expression.value ?: "") + " " + op + " "
        }
    }

    fun onClearClick() {
        _expression.value = ""
        _result.value = ""
        currentNumber = ""
        operator = ""
        firstOperand = null
    }

    fun onBackspaceClick() {
        val exp = _expression.value ?: ""
        if (exp.isNotEmpty()) {
            _expression.value = exp.substring(0, exp.length - 1)
            if (currentNumber.isNotEmpty()) {
                currentNumber = currentNumber.substring(0, currentNumber.length - 1)
            }
        }
    }

    fun onEqualsClick() {
        if (firstOperand != null && operator.isNotEmpty() && currentNumber.isNotEmpty()) {
            val secondOperand = currentNumber.toDoubleOrNull() ?: 0.0
            val res = when (operator) {
                "+" -> firstOperand!! + secondOperand
                "-" -> firstOperand!! - secondOperand
                "*" -> firstOperand!! * secondOperand
                "/" -> if (secondOperand != 0.0) firstOperand!! / secondOperand else Double.NaN
                else -> 0.0
            }
            _result.value = res.toString()
            currentNumber = res.toString()
            firstOperand = null
            operator = ""
        }
    }
}
