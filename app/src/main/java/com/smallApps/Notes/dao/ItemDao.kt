package com.smallApps.Notes.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smallApps.Notes.models.Category
import com.smallApps.Notes.models.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("""
        SELECT * FROM items
        WHERE category = :category
        ORDER BY checked ASC, name COLLATE NOCASE ASC
    """)
    fun observeItemsByCategory(category: Category): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun observeItem(id: Long): Flow<ItemEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: ItemEntity): Long

    @Update
    suspend fun update(item: ItemEntity)

    @Query("UPDATE items SET name = :name WHERE id = :id")
    suspend fun updateName(id: Long, name: String)

    @Query("UPDATE items SET checked = NOT checked WHERE id = :id")
    suspend fun toggleChecked(id: Long)

    @Query("UPDATE items SET checked = :checked WHERE id = :id")
    suspend fun setChecked(id: Long, checked: Boolean)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM items WHERE category = :category AND checked = 1")
    suspend fun deleteCheckedInCategory(category: Category)

    @Query("DELETE FROM items WHERE category = :category")
    suspend fun deleteAllInCategory(category: Category)
}