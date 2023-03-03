package com.moriatsushi.koject.example.kmm.db

interface TodoDao {
    suspend fun fetchAll(): List<TodoTask>

    suspend fun insertTodoTask(
        id: String,
        title: String,
        isCompleted: Long,
    )

    suspend fun updateTodoTask(
        id: String,
        title: String,
        isCompleted: Long,
    )
}
