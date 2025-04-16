package com.bahaa.chinv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerAddress: String,
    val date: String,
    val time: String,
    val discount: Double = 0.0,
    val total: Double,
    val netValue: Double
)
