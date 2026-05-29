package com.example.gemini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gemini.data.AppDatabase
import com.example.gemini.data.HistoryEntity
import com.example.gemini.data.HistoryRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val formatter = DecimalFormat("#,###.##########")

    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
    }

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result

    val history: LiveData<List<HistoryEntity>> = repository.getHistoryByType("CALCULATOR").asLiveData()

    // tokens contains alternating numbers and operators: ["50", "-", "60"]
    private val tokens = mutableListOf<String>()
    private var currentNumber = ""

    private fun formatNumber(numStr: String): String {
        if (numStr.isEmpty()) return ""
        if (numStr == ".") return "0."
        return try {
            val parts = numStr.split(".")
            val bigDecimal = BigDecimal(parts[0])
            val formattedInt = formatter.format(bigDecimal)
            if (numStr.contains(".")) {
                formattedInt + "." + (if (parts.size > 1) parts[1] else "")
            } else {
                formattedInt
            }
        } catch (e: Exception) {
            numStr
        }
    }

    private fun updateDisplay() {
        val sb = StringBuilder()
        for (token in tokens) {
            if (token in listOf("+", "-", "*", "/", "%")) {
                sb.append(" $token ")
            } else {
                sb.append(formatNumber(token))
            }
        }
        sb.append(formatNumber(currentNumber))
        _expression.value = sb.toString()
    }

    fun onNumberClick(number: String) {
        if (number == "." && currentNumber.contains(".")) return
        currentNumber += number
        updateDisplay()
    }

    fun onOperatorClick(op: String) {
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber)
            tokens.add(op)
            currentNumber = ""
        } else if (tokens.isNotEmpty()) {
            // Replace last operator
            tokens[tokens.size - 1] = op
        }
        updateDisplay()
    }

    fun onClearClick() {
        _expression.value = ""
        _result.value = ""
        tokens.clear()
        currentNumber = ""
    }

    fun onBackspaceClick() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.substring(0, currentNumber.length - 1)
        } else if (tokens.isNotEmpty()) {
            // Remove operator
            tokens.removeAt(tokens.size - 1)
            // Move last number to currentNumber
            if (tokens.isNotEmpty()) {
                currentNumber = tokens.removeAt(tokens.size - 1)
            }
        }
        updateDisplay()
    }

    fun onEqualsClick() {
        if (currentNumber.isNotEmpty()) {
            tokens.add(currentNumber)
            currentNumber = ""
        }

        if (tokens.isEmpty()) return

        // If ends with operator, remove it
        if (tokens.last() in listOf("+", "-", "*", "/", "%")) {
            tokens.removeAt(tokens.size - 1)
        }

        val fullExp = _expression.value ?: ""
        val finalRes = evaluate(tokens)

        if (finalRes == null) {
            _result.value = "Error"
        } else {
            val displayRes = formatter.format(finalRes.stripTrailingZeros())
            _result.value = displayRes
            
            // Save to history
            saveHistory(fullExp, displayRes)
            
            // Prepare for next calculation
            tokens.clear()
            currentNumber = finalRes.stripTrailingZeros().toPlainString()
        }
    }

    private fun evaluate(inputTokens: List<String>): BigDecimal? {
        if (inputTokens.isEmpty()) return null
        
        try {
            // We'll do sequential evaluation (left to right) as is common in simple calculators
            var res = BigDecimal(inputTokens[0])
            var i = 1
            while (i < inputTokens.size) {
                val op = inputTokens[i]
                val nextVal = BigDecimal(inputTokens[i + 1])
                
                res = when (op) {
                    "+" -> res.add(nextVal)
                    "-" -> res.subtract(nextVal)
                    "*" -> res.multiply(nextVal)
                    "/" -> if (nextVal != BigDecimal.ZERO) res.divide(nextVal, 10, RoundingMode.HALF_UP) else return null
                    "%" -> res.multiply(nextVal).divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
                    else -> res
                }
                i += 2
            }
            return res
        } catch (e: Exception) {
            return null
        }
    }

    private fun saveHistory(exp: String, res: String) {
        viewModelScope.launch {
            repository.insert(HistoryEntity(type = "CALCULATOR", expression = exp, result = res))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistoryByType("CALCULATOR")
        }
    }
}
