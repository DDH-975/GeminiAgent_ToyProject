package com.example.gemini.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.gemini.data.AppDatabase
import com.example.gemini.data.HistoryEntity
import com.example.gemini.data.HistoryRepository
import kotlinx.coroutines.launch

class BmiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
    }

    private val _bmiResult = MutableLiveData<BmiResult?>()
    val bmiResult: LiveData<BmiResult?> = _bmiResult

    val history: LiveData<List<HistoryEntity>> = repository.getHistoryByType("BMI").asLiveData()

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
        
        saveHistory(heightCm, weightKg, bmi, status)
    }

    private fun saveHistory(h: Double, w: Double, bmi: Double, status: String) {
        viewModelScope.launch {
            val exp = "키: ${h}cm, 몸무게: ${w}kg"
            val res = "BMI: ${String.format("%.1f", bmi)} ($status)"
            repository.insert(HistoryEntity(type = "BMI", expression = exp, result = res))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistoryByType("BMI")
        }
    }
}
