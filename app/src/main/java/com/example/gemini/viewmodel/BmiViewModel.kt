package com.example.gemini.viewmodel

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BmiViewModel : ViewModel() {

    private val _bmiResult = MutableLiveData<BmiResult?>()
    val bmiResult: LiveData<BmiResult?> = _bmiResult

    data class BmiResult(
        val value: Double,
        val status: String,
        val color: Int
    )

    fun calculateBmi(heightCm: Double, weightKg: Double) {
        val heightM = heightCm / 100
        val bmi = weightKg / (heightM * heightM)

        val (status, color) = when {
            bmi < 18.5 -> "저체중" to Color.BLUE
            bmi < 23.0 -> "정상" to Color.GREEN
            bmi < 25.0 -> "과체중" to Color.parseColor("#FFA500") // Orange
            else -> "비만" to Color.RED
        }

        _bmiResult.value = BmiResult(bmi, status, color)
    }
}
