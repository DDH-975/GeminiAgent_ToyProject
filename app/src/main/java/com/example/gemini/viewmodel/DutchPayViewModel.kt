package com.example.gemini.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DutchPayViewModel : ViewModel() {

    private val _peopleCount = MutableLiveData(1)
    val peopleCount: LiveData<Int> = _peopleCount

    private val _perPersonAmount = MutableLiveData<Double?>()
    val perPersonAmount: LiveData<Double?> = _perPersonAmount

    fun incrementPeople() {
        _peopleCount.value = (_peopleCount.value ?: 1) + 1
    }

    fun decrementPeople() {
        val current = _peopleCount.value ?: 1
        if (current > 1) {
            _peopleCount.value = current - 1
        }
    }

    fun setPeopleCount(count: Int) {
        if (count >= 1) {
            _peopleCount.value = count
        }
    }

    fun calculateDutchPay(totalAmount: Double) {
        val count = _peopleCount.value ?: 1
        if (count > 0) {
            _perPersonAmount.value = totalAmount / count
        }
    }
}
