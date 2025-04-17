package com.bahaa.chinv.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>


    @Delete
    suspend fun deleteItem(item: Item)
}
