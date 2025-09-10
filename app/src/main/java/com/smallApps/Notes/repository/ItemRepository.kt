package com.smallApps.Notes.repository

import com.smallApps.Notes.dao.ItemDao
import com.smallApps.Notes.models.Category
import com.smallApps.Notes.models.ItemEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepository {

    fun categories(): List<Category>

    fun observeItems(category: Category): Flow<List<ItemEntity>>

    suspend fun addItem(category: Category, name: String = ""): Long

    suspend fun renameItem(id: Long, name: String)

    suspend fun toggleItem(id: Long)

    suspend fun setChecked(id: Long, checked: Boolean)

    suspend fun deleteItem(id: Long)

    suspend fun clearChecked(category: Category)

    suspend fun clearCategory(category: Category)
}

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override fun categories(): List<Category> =
        listOf(*Category.values())

    override fun observeItems(category: Category): Flow<List<ItemEntity>> =
        dao.observeItemsByCategory(category)

    override suspend fun addItem(category: Category, name: String): Long =
        dao.insert(
            ItemEntity(
                category = category,
                name = name.toString().trim()
            )
        )

    override suspend fun renameItem(id: Long, name: String) {
        dao.updateName(id = id, name = name.toString().trim())
    }

    override suspend fun toggleItem(id: Long) {
        dao.toggleChecked(id)
    }

    override suspend fun setChecked(id: Long, checked: Boolean) {
        dao.setChecked(id, checked)
    }

    override suspend fun deleteItem(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun clearChecked(category: Category) {
        dao.deleteCheckedInCategory(category)
    }

    override suspend fun clearCategory(category: Category) {
        dao.deleteAllInCategory(category)
    }

}