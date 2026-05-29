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
import java.text.DecimalFormat

class DutchPayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
    }

    private val _peopleCount = MutableLiveData(1)
    val peopleCount: LiveData<Int> = _peopleCount

    private val _perPersonAmount = MutableLiveData<Double?>()
    val perPersonAmount: LiveData<Double?> = _perPersonAmount

    val history: LiveData<List<HistoryEntity>> = repository.getHistoryByType("DUTCH_PAY").asLiveData()

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
            val res = totalAmount / count
            _perPersonAmount.value = res
            saveHistory(totalAmount, count, res)
        }
    }

    private fun saveHistory(total: Double, people: Int, perPerson: Double) {
        viewModelScope.launch {
            val formatter = DecimalFormat("#,###")
            val exp = "총 ${formatter.format(total)}원, $people 인"
            val res = "1인당 ${formatter.format(perPerson)}원"
            repository.insert(HistoryEntity(type = "DUTCH_PAY", expression = exp, result = res))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistoryByType("DUTCH_PAY")
        }
    }
}
