package com.example.myapplication.viewmodel


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AndroidAlarmScheduler
import com.example.myapplication.Pages.userEmail
import com.example.myapplication.Pages.userId
import com.example.myapplication.entities.TodoItem
import com.example.myapplication.util.FirebaseUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.time.format.DateTimeParseException
import java.util.Calendar


class TodoListViewModel : ViewModel() {

    private val todayTodoItemsFlow = MutableStateFlow(listOf<TodoItem>(TodoItem(), TodoItem()))
    val todayTodoItems = todayTodoItemsFlow.asStateFlow()
    private val a :MutableLiveData<MutableList<TodoItem>> = MutableLiveData()


    private val scheduledTodoItemsFlow = MutableStateFlow(listOf<TodoItem>())
    val scheduledTodoItems = scheduledTodoItemsFlow.asStateFlow()

    private val doneTodoItemsFlow = MutableStateFlow(listOf<TodoItem>())
    val doneTodoItems = doneTodoItemsFlow.asStateFlow()

    private val selectedTodoItemsFlow = MutableStateFlow(listOf<TodoItem>())
    val selectedTodoItems = selectedTodoItemsFlow.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAndGroupTodoItems() {
        //Log.d("TodoListViewModel", "fetchAndGroupTodoItems() called")

        getAllTodoItemsFromFirebase(userId) { todoItems ->
            val groupedTodoItems = groupTodoItemsByDate(todoItems)
            todayTodoItemsFlow.update { groupedTodoItems["Today"]?.toList() ?: emptyList() }
            scheduledTodoItemsFlow.update { groupedTodoItems["Scheduled"]?.toList() ?: emptyList() }
            doneTodoItemsFlow.update { groupedTodoItems["Done"]?.toList()?: emptyList() }
        }
        /*Log.d("TodoListViewModel", "Today Todo Items: ${todayTodoItems.value}")
        Log.d("TodoListViewModel", "Scheduled Todo Items: ${scheduledTodoItems.value}")
        Log.d("TodoListViewModel", "Done Todo Items: ${doneTodoItems.value}")
        Log.d("TodoListViewModel", "Selected Todo Items: ${selectedTodoItems.value}")*/
    }
    fun toggleTodoItemSelection(todoItem: TodoItem) {
        val updatedSelectedItems = selectedTodoItems.value.toMutableList()
        if (updatedSelectedItems.contains(todoItem)) {
            updatedSelectedItems.remove(todoItem)
        } else {
            updatedSelectedItems.add(todoItem)
        }
        selectedTodoItemsFlow.update { updatedSelectedItems }
    }

    fun removeSelectedItems(androidAlarmScheduler: AndroidAlarmScheduler) {
        selectedTodoItemsFlow.value.forEach { item ->
            val splitDate = item.date.split("-")
            val splitTime = item.time.split(":")
            val instance = Calendar.getInstance()
            instance.set(splitDate[0].toInt(), splitDate[1].toInt() - 1, splitDate[2].toInt(), splitTime[0].toInt(), splitTime[1].toInt() )
            androidAlarmScheduler.cancel(instance, item.title, item.introduction)
            todayTodoItemsFlow.update { it.toMutableList().apply { remove(item) }.toList() }
            scheduledTodoItemsFlow.update { it.toMutableList().apply { remove(item) }.toList() }
            doneTodoItemsFlow.update { it.toMutableList().apply { remove(item) }.toList() }
        }
        selectedTodoItemsFlow.update { emptyList() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun groupTodoItemsByDate(todoItems: List<TodoItem>): Map<String, List<TodoItem>> {
        val groupedTodoItems = mutableMapOf<String, MutableList<TodoItem>>()

        val currentDate = LocalDate.now()

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (todoItem in todoItems) {
            try {
                val itemDate = LocalDate.parse(todoItem.date, dateFormatter)

                // group
                val key = if (todoItem.isDone) {
                    "Done"
                } else if (itemDate == currentDate) {
                    "Today"
                } else {
                    "Scheduled"
                }

                if (!groupedTodoItems.containsKey(key)) {
                    groupedTodoItems[key] = mutableListOf()
                }
                groupedTodoItems[key]?.add(todoItem)
                //Log.d("TodoItemGrouping", "TodoItem: ${todoItem.title} Date: ${todoItem.date} Key: $key")
            } catch (e: DateTimeParseException) {
                Log.e("DateTimeParseException", "Error parsing date: ${todoItem.date}", e)
            }
        }

        return groupedTodoItems
    }

    fun getAllTodoItemsFromFirebase(uid: String, callback: (List<TodoItem>) -> Unit) {
        FirebaseUtil.getUserEventsCollection(uid)
            .addOnSuccessListener { result ->
                val todoItems = mutableListOf<TodoItem>()
                for (document in result) {
                    val todoItem = document.toObject(TodoItem::class.java)
                    val documentData = document.data
                    val isDone = documentData["isDone"] as Boolean
                    todoItem.isDone = isDone

                    todoItem.documentId = document.id
                    todoItems.add(todoItem)
                }
                callback(todoItems)
            }
            .addOnFailureListener { exception ->
                println("fail：$exception")
            }
    }
}




