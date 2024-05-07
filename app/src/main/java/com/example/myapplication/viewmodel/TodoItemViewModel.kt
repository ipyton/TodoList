package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainApplication
import com.example.myapplication.entities.TodoItem
import kotlinx.coroutines.launch

class TodoItemViewModel : ViewModel() {

    private val todoItemDao = MainApplication.database.todoItemDao()

    fun insertTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {
            val existedItem = todoItemDao.getById(todoItem.documentId).getOrNull(0)
            if (existedItem == null) todoItemDao.upsert(todoItem)
            else todoItemDao.upsert(todoItem.copy(id = existedItem.id))
        }
    }

}