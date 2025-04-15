package com.bahaa.chinv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.Item
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val itemDao = AppDatabase.getDatabase(application).itemDao()

    val items: StateFlow<List<Item>> = itemDao.getAllItems()
        .map { it.sortedByDescending { item -> item.id } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insertItem(item)
        }
    }
}