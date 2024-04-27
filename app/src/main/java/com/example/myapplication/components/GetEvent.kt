package com.example.myapplication.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Pages.userEmail
import com.example.myapplication.entities.TodoItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.time.format.DateTimeParseException


class TodoListViewModel : ViewModel() {
    val todayTodoItems: MutableState<List<TodoItem>> = mutableStateOf(emptyList())
    val scheduledTodoItems: MutableState<List<TodoItem>> = mutableStateOf(emptyList())
    val doneTodoItems: MutableState<List<TodoItem>> = mutableStateOf(emptyList())
    val selectedTodoItems: MutableState<List<TodoItem>> = mutableStateOf(emptyList())
    val todoItemSelections: MutableList<Boolean> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchAndGroupTodoItems() {
        getAllTodoItemsFromFirebase { todoItems ->
            val groupedTodoItems = groupTodoItemsByDate(todoItems)
            todayTodoItems.value = (groupedTodoItems["Today"] ?: emptyList()).toMutableList()
            scheduledTodoItems.value =
                (groupedTodoItems["Scheduled"] ?: emptyList()).toMutableList()
            doneTodoItems.value = (groupedTodoItems["Done"] ?: emptyList()).toMutableList()
        }
    }
    fun toggleTodoItemSelection(todoItem: TodoItem) {
        val updatedSelectedItems = selectedTodoItems.value.toMutableList()
        if (updatedSelectedItems.contains(todoItem)) {
            updatedSelectedItems.remove(todoItem)
        } else {
            updatedSelectedItems.add(todoItem)
        }
        selectedTodoItems.value = updatedSelectedItems
    }
}

//var scheduledStates: MutableList<Boolean> = mutableListOf()


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
            Log.d("TodoItemGrouping", "TodoItem: ${todoItem.title} Date: ${todoItem.date} Key: $key")
        } catch (e: DateTimeParseException) {
            Log.e("DateTimeParseException", "Error parsing date: ${todoItem.date}", e)
        }
    }

    return groupedTodoItems
}



fun getAllTodoItemsFromFirebase(callback: (List<TodoItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

    val userEmail = userEmail ?: return

    usersCollection.whereEqualTo("email", userEmail).get()
        .addOnSuccessListener { userResult ->
            if (userResult.size() != 1) {
                return@addOnSuccessListener
            }

            val userDocument = userResult.documents.first()

            val eventsCollection = db.collection("users").document(userDocument.id).collection("events")

            eventsCollection.get()
                .addOnSuccessListener { result ->
                    val todoItems = mutableListOf<TodoItem>()
                    for (document in result) {
                        val todoItem = document.toObject(TodoItem::class.java)
                        val documentData = document.data
                        val isDone = documentData["isDone"] as Boolean
                        todoItem.isDone = isDone

                        Log.d("TodoItemIsDne", "TodoItem isDone: ${todoItem.isDone}")

                        todoItem.documentId = document.id
                        todoItems.add(todoItem)
                        Log.d("TodoItemTitle", "TodoItem title: ${todoItem.title}")
                        Log.d("TodoItemIntro", "TodoItem introduction: ${todoItem.introduction}")
                        Log.d("TodoItemTime", "TodoItem time: ${todoItem.time}")
                        Log.d("TodoItemDocumentId", "TodoItem document ID: ${todoItem.documentId}")
                    }
                    callback(todoItems)
                }
                .addOnFailureListener { exception ->
                    println("fail：$exception")
                }
        }
        .addOnFailureListener { exception ->
            println("fail：$exception")
        }
}


fun getUserDocumentByEmail(userEmail: String, onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

    usersCollection.whereEqualTo("email", userEmail).get()
        .addOnSuccessListener { userResult ->
            if (userResult.size() != 1) {
                onFailure(Exception("Multiple or no user documents found for the given email"))
                return@addOnSuccessListener
            }

            onSuccess(userResult)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
fun fetchAndGroupTodoItems() {
    getAllTodoItemsFromFirebase { todoItems ->
        val groupedTodoItems = groupTodoItemsByDate(todoItems)
        scheduledTodoItems = (groupedTodoItems["Scheduled"] ?: emptyList()).toMutableList()
        Log.d("ScheduledTodoItems", "Scheduled Todo Items: $scheduledTodoItems")

        todayTodoItems = (groupedTodoItems["Today"] ?: emptyList()).toMutableList()
        Log.d("TodayTodoItems", "Today Todo Items: $todayTodoItems")

        doneTodoItems = (groupedTodoItems["Done"] ?: emptyList()).toMutableList()
        Log.d("DoneTodoItems", "Done Todo Items: $doneTodoItems")

    }
}
*/


