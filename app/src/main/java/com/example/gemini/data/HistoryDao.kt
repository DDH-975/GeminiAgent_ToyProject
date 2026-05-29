package com.example.gemini.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: HistoryEntity)

    @Query("SELECT * FROM calculation_history WHERE type = :type ORDER BY timestamp DESC")
    fun getHistoryByType(type: String): Flow<List<HistoryEntity>>

    @Query("DELETE FROM calculation_history WHERE type = :type")
    suspend fun clearHistoryByType(type: String)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()
}
