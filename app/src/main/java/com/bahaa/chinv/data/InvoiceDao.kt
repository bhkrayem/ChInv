package com.bahaa.chinv.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insertInvoice(invoice: Invoice): Long

    @Insert
    suspend fun insertInvoiceItems(items: List<InvoiceItem>)

    @Query("SELECT * FROM invoices ORDER BY id DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    fun getItemsForInvoice(invoiceId: Int): Flow<List<InvoiceItem>>

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteInvoiceItems(invoiceId: Int)

    @Query("SELECT * FROM invoices WHERE date BETWEEN :start AND :end ORDER BY id DESC")
    suspend fun getInvoicesBetween(start: String, end: String): List<Invoice>

    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun getItemsForInvoiceOnce(invoiceId: Int): List<InvoiceItem>

    @Query("SELECT * FROM invoices WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, time DESC")
    fun getInvoicesBetweenDates(startDate: String, endDate: String): Flow<List<Invoice>>

}
