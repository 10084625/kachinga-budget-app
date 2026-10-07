package com.example.prog7313_poe_kachinga.data.dao

import androidx.room.*
import com.example.prog7313_poe_kachinga.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("SELECT * FROM categories WHERE userId = :userId OR userId = 0 ORDER BY name ASC")
    fun getAllCategories(userId: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE (userId = :userId OR userId = 0) AND isExpense = :isExpense ORDER BY name ASC")
    fun getCategoriesByType(userId: Int, isExpense: Boolean): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId")
    suspend fun getUserCategoryCount(userId: Int): Int

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}