package com.bahaa.chinv.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bahaa.chinv.data.Invoice
import com.bahaa.chinv.data.InvoiceDao
import com.bahaa.chinv.data.InvoiceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class InvoiceViewModel(private val dao: InvoiceDao) : ViewModel() {

    private val _invoiceItems = MutableStateFlow<List<InvoiceItem>>(emptyList())
    val invoiceItems: StateFlow<List<InvoiceItem>> = _invoiceItems

    private var currentInvoiceId = 0

    fun getNextInvoiceNumber(): Flow<Int> {
        return dao.getAllInvoices().map { invoices ->
            if (invoices.isEmpty()) 1 else invoices.maxOf { it.id } + 1
        }
    }

    fun addItem(item: InvoiceItem) {
        _invoiceItems.value = _invoiceItems.value + item
    }

    fun removeItem(index: Int) {
        _invoiceItems.value = _invoiceItems.value.toMutableList().also { it.removeAt(index) }
    }

    fun clearItems() {
        _invoiceItems.value = emptyList()
    }

    fun saveInvoice(
        customerName: String,
        customerAddress: String,
        date: String,
        time: String,
        discount: Double,
        invoiceId: Int? = null
    ) {
        fun saveInvoice(
            customerName: String,
            customerAddress: String,
            date: String,
            time: String,
            discount: Double,
            invoiceId: Int? = null
        ) {
            viewModelScope.launch {
                val total = _invoiceItems.value.sumOf { it.value }
                val net = total - discount

                Log.d("InvoiceSave", "Saving invoice... total=$total, discount=$discount, net=$net")

                val invoice = Invoice(
                    id = invoiceId ?: 0,
                    customerName = customerName,
                    customerAddress = customerAddress,
                    date = date,
                    time = time,
                    discount = discount,
                    total = total,
                    netValue = net
                )

                val id = if (invoiceId == null) {
                    Log.d("InvoiceSave", "Inserting new invoice")
                    dao.insertInvoice(invoice).toInt()
                } else {
                    Log.d("InvoiceSave", "Updating existing invoice id=$invoiceId")
                    dao.deleteInvoiceItems(invoiceId)
                    dao.deleteInvoice(invoice)
                    dao.insertInvoice(invoice).toInt()
                }

                val updatedItems = _invoiceItems.value.map {
                    it.copy(invoiceId = id)
                }

                dao.insertInvoiceItems(updatedItems)
                currentInvoiceId = id
                clearItems()

                Log.d("InvoiceSave", "Invoice saved successfully with ID: $id")
            }
        }


    }

    fun getInvoiceItems(invoiceId: Int): Flow<List<InvoiceItem>> {
        return dao.getItemsForInvoice(invoiceId)
    }

    fun getAllInvoices(): Flow<List<Invoice>> {
        return dao.getAllInvoices()
    }

    fun getInvoiceById(id: Int): Flow<Invoice> {
        return dao.getInvoiceById(id)
    }

    fun loadExistingItems(existingItems: List<InvoiceItem>) {
        _invoiceItems.value = existingItems
    }

}
