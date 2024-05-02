package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.myapplication.entities.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Upsert
    suspend fun upsert(todoItem: TodoItem)

    @Query("SELECT * FROM TodoItem WHERE documentId = :id")
    suspend fun getById(id: String): List<TodoItem>

    @Query("DELETE FROM TodoItem WHERE documentId = :id")
    suspend fun deleteById(id: String)

}