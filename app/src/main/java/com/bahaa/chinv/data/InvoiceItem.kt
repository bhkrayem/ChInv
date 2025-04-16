package com.bahaa.chinv.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoice_items")
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,  // foreign key to Invoice.id
    val itemName: String,
    val quantity: Double,
    val unit: String, // "box" or "piece"
    val unitPrice: Double,
    val value: Double
)
