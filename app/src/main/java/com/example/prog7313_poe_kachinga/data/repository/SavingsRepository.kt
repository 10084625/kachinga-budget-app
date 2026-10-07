package com.example.prog7313_poe_kachinga.data.repository

import com.example.prog7313_poe_kachinga.data.dao.SavingsGoalDao
import com.example.prog7313_poe_kachinga.data.entity.SavingsGoal
import kotlinx.coroutines.flow.Flow

class SavingsRepository(private val dao: SavingsGoalDao) {

    fun getAll(userId: Int): Flow<List<SavingsGoal>> = dao.getAllGoals(userId)

    fun getById(id: Int, userId: Int): Flow<SavingsGoal?> = dao.getGoalById(id, userId)

    suspend fun insert(goal: SavingsGoal): Result<Unit> {
        return try {
            if (goal.name.isBlank())
                return Result.failure(Exception("Goal name cannot be empty"))
            if (goal.targetAmount <= 0)
                return Result.failure(Exception("Target must be greater than 0"))
            dao.insertGoal(goal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deposit(goalId: Int, userId: Int, amount: Double): Result<Unit> {
        return try {
            if (amount <= 0)
                return Result.failure(Exception("Deposit must be greater than 0"))
            dao.addDeposit(goalId, userId, amount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(goal: SavingsGoal) = dao.deleteGoal(goal)
}