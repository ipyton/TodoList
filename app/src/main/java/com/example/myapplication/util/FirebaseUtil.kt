package com.example.myapplication.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.AndroidAlarmScheduler
import com.example.myapplication.MainApplication
import com.example.myapplication.Pages.userId
import com.example.myapplication.entities.TodoItem
import com.example.myapplication.viewmodel.TodoItemViewModel
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

object FirebaseUtil {

    fun getUserEventsCollection(uid: String): Task<QuerySnapshot> {
        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        return userDocRef.collection("events").get()
    }

    suspend fun deleteTodoItemFromFirebase(uid: String, documentId: String, onFinished: () -> Unit): Task<Void> {
        val roomDao = MainApplication.database.todoItemDao()
        roomDao.deleteById(documentId)

        return getUserEventsCollection(uid).onSuccessTask { querySnapshot ->
            val document = querySnapshot.documents.find { it.id == documentId }
            document?.reference?.delete()?.addOnCompleteListener { onFinished() } ?:
            throw Exception("Document not found")
        }
    }

    fun writeToFirebase(
        uid: String,
        title: String,
        introduction: String,
        latitude: Double,
        longitude: Double,
        date: String,
        time: String,
        isDone: Boolean
    ): Task<DocumentReference> {
        val userEventsCollection = FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("events")

        val eventData = hashMapOf(
            "title" to title,
            "introduction" to introduction,
            "latitude" to latitude,
            "longitude" to longitude,
            "date" to date,
            "time" to time,
            "isDone" to isDone
        )

        val addTask = userEventsCollection.add(eventData)

        return addTask.continueWith { task ->
            if (task.isSuccessful) {
                val documentReference = task.result

                val viewModel = TodoItemViewModel()

                val todoItem = TodoItem(
                    title = title,
                    introduction = introduction,
                    latitude = latitude,
                    longitude = longitude,
                    date = date,
                    time = time,
                    isDone = isDone,
                    documentId = documentReference.id,
                    selected = false
                )
                viewModel.insertTodoItem(todoItem)

                documentReference
            } else {
                throw task.exception ?: Exception("Unknown error occurred")
            }
        }
    }

    fun updateTodoItemInFirebase(uid: String, todoItem: TodoItem, onFinished: () -> Unit): Task<Void> {
        val userEventsCollection = FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("events")

        val eventData = hashMapOf(
            "title" to todoItem.title,
            "introduction" to todoItem.introduction,
            "latitude" to todoItem.latitude,
            "longitude" to todoItem.longitude,
            "date" to todoItem.date,
            "time" to todoItem.time,
            "isDone" to todoItem.isDone
        )

        val documentId = todoItem.documentId

        val todoItemDocRef = userEventsCollection.document(documentId)

        return todoItemDocRef.update(eventData as Map<String, Any>).apply {
            addOnCompleteListener { onFinished() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteSelectedTodoItemsFromFirebase(
        selectedItems: List<TodoItem>,
        todoListViewModel: TodoListViewModel,
        androidAlarmScheduler: AndroidAlarmScheduler
    ) {
        val roomDao = MainApplication.database.todoItemDao()
        selectedItems.forEachIndexed { index, todoItem ->
            deleteTodoItemFromFirebase(userId, todoItem.documentId) {
                if (index == selectedItems.size - 1)
                    todoListViewModel.apply {
                        fetchAndGroupTodoItems()
                        removeSelectedItems(androidAlarmScheduler = androidAlarmScheduler)
                    }
            }
            roomDao.deleteById(todoItem.documentId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun markSelectedTodoItemsAsDone(
        selectedItems: List<TodoItem>,
        todoListViewModel: TodoListViewModel,
        androidAlarmScheduler: AndroidAlarmScheduler
    ) {
        val roomDao = MainApplication.database.todoItemDao()
        selectedItems.forEachIndexed { index, todoItem ->
            todoItem.isDone = true

            val existedItem = roomDao.getById(todoItem.documentId).getOrNull(0)
            if (existedItem == null) roomDao.upsert(todoItem)
            else roomDao.upsert(todoItem.copy(id = existedItem.id))

            updateTodoItemInFirebase(userId, todoItem) {
                if (index == selectedItems.size - 1)
                    todoListViewModel.apply {
                        fetchAndGroupTodoItems()
                        removeSelectedItems(androidAlarmScheduler)
                    }
            }
        }

    }

}