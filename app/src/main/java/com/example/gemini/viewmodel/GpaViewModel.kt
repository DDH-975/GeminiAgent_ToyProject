package com.example.gemini.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gemini.model.GpaSubject

class GpaViewModel : ViewModel() {

    private val _subjects = MutableLiveData<MutableList<GpaSubject>>(mutableListOf(GpaSubject()))
    val subjects: LiveData<MutableList<GpaSubject>> = _subjects

    private val _gpaResult = MutableLiveData<Double?>()
    val gpaResult: LiveData<Double?> = _gpaResult

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
                "A0" -> 4.0
                "B+" -> 3.5
                "B0" -> 3.0
                "C+" -> 2.5
                "C0" -> 2.0
                "D+" -> 1.5
                "D0" -> 1.0
                "F" -> 0.0
                else -> null // P/NP
            }

            if (points != null) {
                totalPoints += points * subject.credits
                totalCredits += subject.credits.toDouble()
            }
        }

        if (totalCredits > 0) {
            _gpaResult.value = totalPoints / totalCredits
        } else {
            _gpaResult.value = 0.0
        }
    }
}
