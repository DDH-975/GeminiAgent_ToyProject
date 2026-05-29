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

class DiscountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    init {
        val historyDao = AppDatabase.getDatabase(application).historyDao()
        repository = HistoryRepository(historyDao)
    }

    private val _result = MutableLiveData<DiscountResult?>()
    val result: LiveData<DiscountResult?> = _result

    val history: LiveData<List<HistoryEntity>> = repository.getHistoryByType("DISCOUNT").asLiveData()

    data class DiscountResult(
        val finalPrice: Double,
        val savedAmount: Double
    )

    fun calculateDiscount(productName: String, originalPrice: Double, discountRate: Double) {
        val savedAmount = originalPrice * (discountRate / 100)
        val finalPrice = originalPrice - savedAmount
        _result.value = DiscountResult(finalPrice, savedAmount)
        
        saveHistory(productName, originalPrice, discountRate, finalPrice)
    }

    private fun saveHistory(name: String, original: Double, rate: Double, final: Double) {
        viewModelScope.launch {
            val formatter = DecimalFormat("#,###")
            val productInfo = if (name.isNotBlank()) " $name" else ""
            val exp = "${formatter.format(original)}원 (${rate}%)${productInfo}"
            val res = "최종 ${formatter.format(final)}원"
            repository.insert(HistoryEntity(type = "DISCOUNT", expression = exp, result = res))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistoryByType("DISCOUNT")
        }
    }
}
