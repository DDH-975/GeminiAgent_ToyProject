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
        // 소수점이 이미 있는데 또 입력하려는 경우 방지
        if (number == "." && currentNumber.contains(".")) return
        
        currentNumber += number
        _expression.value = (_expression.value ?: "") + number
    }

    fun onOperatorClick(op: String) {
        if (currentNumber.isNotEmpty()) {
            if (firstOperand == null) {
                // 첫 번째 숫자 입력 후 연산자 클릭
                firstOperand = try {
                    BigDecimal(currentNumber)
                } catch (e: Exception) {
                    null
                }
            } else if (operator.isNotEmpty()) {
                // 이미 첫 번째 숫자와 연산자가 있는 상태에서 새로운 연산자 클릭 (중간 계산 수행)
                val secondOperand = try {
                    BigDecimal(currentNumber)
                } catch (e: Exception) {
                    BigDecimal.ZERO
                }
                
                val intermediateResult = calculate(firstOperand!!, secondOperand, operator)
                if (intermediateResult == null) {
                    _result.value = "Error"
                    onClearClick()
                    return
                } else {
                    // 중간 결과를 firstOperand에 저장하여 다음 연산 준비
                    firstOperand = intermediateResult
                }
            }
            operator = op
            currentNumber = ""
            _expression.value = (_expression.value ?: "") + " " + op + " "
        } else if (firstOperand != null) {
            // 이미 숫자가 있는 상태에서 연산자만 변경
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
                // 연산자 삭제 (공백 포함 3글자)
                _expression.value = exp.substring(0, exp.length - 3)
                operator = ""
                // 삭제 후 이전 숫자를 이어서 입력할 수 있도록 복구
                currentNumber = firstOperand?.stripTrailingZeros()?.toPlainString() ?: ""
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
            
            val res = calculate(firstOperand!!, secondOperand, operator)

            if (res == null) {
                _result.value = "Error"
                currentNumber = ""
            } else {
                val formattedRes = res.stripTrailingZeros().toPlainString()
                _result.value = formattedRes
                // 다음 연산을 위해 결과값을 현재 숫자로 설정
                currentNumber = formattedRes
            }
            firstOperand = null
            operator = ""
        }
    }

    private fun calculate(first: BigDecimal, second: BigDecimal, op: String): BigDecimal? {
        return try {
            when (op) {
                "+" -> first.add(second)
                "-" -> first.subtract(second)
                "*" -> first.multiply(second)
                "/" -> if (second != BigDecimal.ZERO) {
                    // 무한 소수 발생 시 소수점 10자리까지 계산
                    first.divide(second, 10, RoundingMode.HALF_UP)
                } else {
                    null
                }
                else -> first
            }
        } catch (e: Exception) {
            null
        }
    }
}
