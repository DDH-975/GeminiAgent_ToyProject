package com.example.gemini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "CALCULATOR", "GPA", "BMI", "DUTCH_PAY", "DISCOUNT"
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
