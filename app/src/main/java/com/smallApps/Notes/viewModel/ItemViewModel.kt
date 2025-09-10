package com.smallApps.Notes.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smallApps.Notes.database.DbProvider
import com.smallApps.Notes.models.Category
import com.smallApps.Notes.models.ItemEntity
import com.smallApps.Notes.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemViewModel (
    private val repo: ItemRepository
) : ViewModel() {

    val categories: List<Category> = repo.categories()


    private val _selectedCategory = MutableStateFlow(categories.first())
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    val items: StateFlow<List<ItemEntity>> =
        _selectedCategory
            .flatMapLatest { cat -> repo.observeItems(cat) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun selectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun addItem(name: String = "") {
        viewModelScope.launch {
            repo.addItem(_selectedCategory.value, name)
        }
    }

    fun renameItem(id: Long, name: String) {
        viewModelScope.launch {
            repo.renameItem(id, name)
        }
    }

    fun toggleItem(id: Long) {
        viewModelScope.launch {
            repo.toggleItem(id)
        }
    }

    fun setChecked(id: Long, checked: Boolean) {
        viewModelScope.launch {
            repo.setChecked(id, checked)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repo.deleteItem(id)
        }
    }

    fun clearChecked() {
        viewModelScope.launch {
            repo.clearChecked(_selectedCategory.value)
        }
    }

    fun clearCategory() {
        viewModelScope.launch {
            repo.clearCategory(_selectedCategory.value)
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = DbProvider.getRepository(app)
                    return ItemViewModel(repo) as T
                }

            }
    }

}