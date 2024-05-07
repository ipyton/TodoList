package com.example.myapplication

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.entities.TodoItem


@Dao
interface TodoItemDao {

    @Upsert
    suspend fun upsert(todoItem: TodoItem)

    @Query("SELECT * FROM TodoItem WHERE documentId = :id")
    suspend fun getById(id: String): List<TodoItem>

    @Query("DELETE FROM TodoItem WHERE documentId = :id")
    suspend fun deleteById(id: String)

}