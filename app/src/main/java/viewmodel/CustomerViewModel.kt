package com.bahaa.chinv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.Customer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow


class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).customerDao()

    val customers: StateFlow<List<Customer>> = dao.getAll()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(customer: Customer) {
        viewModelScope.launch { dao.insert(customer) }
    }

    fun delete(customer: Customer) {
        viewModelScope.launch { dao.delete(customer) }


    }

    fun searchCustomers(query: String): Flow<List<Customer>> {
        return customers.map { list ->
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
