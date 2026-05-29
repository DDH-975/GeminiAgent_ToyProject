package com.example.gemini.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    fun getHistoryByType(type: String): Flow<List<HistoryEntity>> = historyDao.getHistoryByType(type)

    suspend fun insert(history: HistoryEntity) {
        historyDao.insert(history)
    }

    suspend fun clearHistoryByType(type: String) {
        historyDao.clearHistoryByType(type)
    }
}
