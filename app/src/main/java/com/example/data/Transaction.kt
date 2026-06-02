package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "Income" or "Expense"
    val amount: Double,
    val date: Long, // timestamp
    val accountId: Int, // relation to Account
    val category: String,
    val source: String,
    val note: String
)
