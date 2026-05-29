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
import com.example.gemini.model.GpaSubject
import kotlinx.coroutines.launch
import java.util.Locale

class GpaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
    }

    private val _subjects = MutableLiveData<MutableList<GpaSubject>>(mutableListOf(GpaSubject()))
    val subjects: LiveData<MutableList<GpaSubject>> = _subjects

    private val _gpaResult = MutableLiveData<Double?>()
    val gpaResult: LiveData<Double?> = _gpaResult

    val history: LiveData<List<HistoryEntity>> = repository.getHistoryByType("GPA").asLiveData()

    fun addSubject() {
        val currentList = _subjects.value ?: mutableListOf()
        currentList.add(GpaSubject())
        _subjects.value = currentList
    }

    fun removeSubject(position: Int) {
        val currentList = _subjects.value ?: return
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _subjects.value = currentList
        }
    }

    fun calculateGpa() {
        val currentList = _subjects.value ?: return
        var totalPoints = 0.0
        var totalCredits = 0.0

        for (subject in currentList) {
            val points = when (subject.grade) {
                "A+" -> 4.5
                "A" -> 4.0
                "B+" -> 3.5
                "B" -> 3.0
                "C+" -> 2.5
                "C" -> 2.0
                "D+" -> 1.5
                "D" -> 1.0
                "F" -> 0.0
                else -> null
            }

            if (points != null) {
                totalPoints += points * subject.credits
                totalCredits += subject.credits.toDouble()
            }
        }

        val result = if (totalCredits > 0) totalPoints / totalCredits else 0.0
        _gpaResult.value = result
        
        saveHistory(currentList, result)
    }

    private fun saveHistory(subjectList: List<GpaSubject>, gpa: Double) {
        viewModelScope.launch {
            val summary = "${subjectList.size} 개 과목"
            val details = StringBuilder()
            subjectList.forEach { 
                val name = if (it.name.isBlank()) "미지정" else it.name
                details.append("- $name: ${it.grade} (${it.credits}학점)\n")
            }
            
            val fullExpression = "$summary\n\n${details.toString().trim()}"
            val res = String.format(Locale.getDefault(), "평균 평점: %.2f", gpa)
            repository.insert(HistoryEntity(type = "GPA", expression = fullExpression, result = res))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistoryByType("GPA")
        }
    }
}
