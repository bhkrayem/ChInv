package com.bahaa.chinv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.Item
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val itemDao = AppDatabase.getDatabase(application).itemDao()

    val items: StateFlow<List<Item>> = itemDao.getAll() // MATCH DAO NAME HERE
        .map { itemList -> itemList.sortedByDescending { it.id } }
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

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.deleteItem(item)
        }
    }
}
