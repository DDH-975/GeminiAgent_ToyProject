package com.example.gemini.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result

    private var currentNumber = ""
    private var operator = ""
    private var firstOperand: BigDecimal? = null

    fun onNumberClick(number: String) {
        currentNumber += number
        _expression.value = (_expression.value ?: "") + number
    }

    fun onOperatorClick(op: String) {
        if (currentNumber.isNotEmpty()) {
            firstOperand = try {
                BigDecimal(currentNumber)
            } catch (e: Exception) {
                null
            }
            operator = op
            currentNumber = ""
            _expression.value = (_expression.value ?: "") + " " + op + " "
        } else if (firstOperand != null) {
            // Operator change
            operator = op
            val exp = _expression.value ?: ""
            val lastSpaceIndex = exp.trimEnd().lastIndexOf(" ")
            if (lastSpaceIndex != -1) {
                _expression.value = exp.substring(0, lastSpaceIndex).trim() + " " + op + " "
            }
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
            if (exp.endsWith(" ")) {
                // Remove operator and spaces
                _expression.value = exp.substring(0, exp.length - 3)
                operator = ""
                currentNumber = firstOperand?.toPlainString() ?: ""
                firstOperand = null
            } else {
                _expression.value = exp.substring(0, exp.length - 1)
                if (currentNumber.isNotEmpty()) {
                    currentNumber = currentNumber.substring(0, currentNumber.length - 1)
                }
            }
        }
    }

    fun onEqualsClick() {
        if (firstOperand != null && operator.isNotEmpty() && currentNumber.isNotEmpty()) {
            val secondOperand = try {
                BigDecimal(currentNumber)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
            
            val res = try {
                when (operator) {
                    "+" -> firstOperand!!.add(secondOperand)
                    "-" -> firstOperand!!.subtract(secondOperand)
                    "*" -> firstOperand!!.multiply(secondOperand)
                    "/" -> if (secondOperand != BigDecimal.ZERO) {
                        firstOperand!!.divide(secondOperand, 10, RoundingMode.HALF_UP).stripTrailingZeros()
                    } else {
                        null
                    }
                    else -> BigDecimal.ZERO
                }
            } catch (e: Exception) {
                null
            }

            if (res == null) {
                _result.value = "Error"
                currentNumber = ""
            } else {
                val formattedRes = res.stripTrailingZeros().toPlainString()
                _result.value = formattedRes
                currentNumber = formattedRes
            }
            firstOperand = null
            operator = ""
        }
    }
}
