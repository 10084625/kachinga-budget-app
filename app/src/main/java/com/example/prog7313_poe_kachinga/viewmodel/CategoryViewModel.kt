package com.example.prog7313_poe_kachinga.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.prog7313_poe_kachinga.data.KachingaDatabase
import com.example.prog7313_poe_kachinga.data.SessionManager
import com.example.prog7313_poe_kachinga.data.entity.Category
import com.example.prog7313_poe_kachinga.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CategoryRepository(
        KachingaDatabase.getDatabase(application).categoryDao()
    )
    private val session = SessionManager(application)

    private val userId: StateFlow<Int> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    // Shows default categories (userId=0) + user's own categories
    val allCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getAll(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getExpenseCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<Category>> = userId
        .flatMapLatest { id ->
            if (id >= 0) repo.getIncomeCategories(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun addCategory(name: String, icon: String, isExpense: Boolean) {
        viewModelScope.launch {
            val currentUserId = session.userId.first()
            repo.insert(
                Category(
                    userId = currentUserId,
                    name = name,
                    icon = icon,
                    isExpense = isExpense
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Category added!") }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to add category")
                    }
                }
            )
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.delete(category) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}

data class CategoryUiState(
    val error: String? = null,
    val successMessage: String? = null
)