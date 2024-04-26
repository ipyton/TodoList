package com.example.myapplication.Pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.myapplication.components.EditEvents
import com.example.myapplication.components.TodoListViewModel
import com.example.myapplication.components.getUserDocumentByEmail


import com.example.myapplication.entities.TodoItem

@Composable
fun Scheduled(isVisible: MutableState<Boolean>, viewModel: TodoListViewModel) {
    var scheduledItems = viewModel.scheduledTodoItems
    var selectedItems = viewModel.selectedTodoItems
    var showEventEdit= remember {
        mutableStateOf(false)
    }


    //var selectedTodoItems = remember { mutableStateOf(mutableListOf<TodoItem>()) }
    //var scheduledItems = remember { mutableStateOf(scheduledTodoItems.toMutableList()) }

    LazyColumn {
        items(scheduledItems.value.size) { index ->
            val todoItem = scheduledItems.value[index]
            val isSelected = viewModel.selectedTodoItems.value.contains(todoItem)
            var expanded by remember { mutableStateOf(false) }
            Column {
                ListItem(
                    headlineContent = { Text(todoItem.title) },
                    overlineContent = { Text(todoItem.introduction) },
                    supportingContent = { Text(todoItem.time) },
                    leadingContent = {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.toggleTodoItemSelection(todoItem)

                                isVisible.value = selectedItems.value.isNotEmpty()
                                Log.d("RadioButtonClicked", "RadioButton clicked for TodoItem: ${todoItem.title}")
                                //Log.d("SelectedTodoItems", "Selected Todo Items: ${selectedTodoItems}, TodoItem: ${todoItem.title}")
                                //Log.d("IsEmpty", "isEmpty in Scheduled: ${selectedTodoItems.value.isNotEmpty()}")

                                //Log.d("IsVisible", "isVisible value in Scheduled: ${isVisible.value}")

                            },
                            modifier = Modifier.semantics { contentDescription = "Localized Description" }
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = {
                            expanded = true

                        }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "change status") }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    //Log.d("TodoItems", "Todo Items before deletion: $todoItems")
                                    deleteTodoItemFromFirebase(todoItem.documentId)
                                    scheduledItems.value = scheduledItems.value.toMutableList().apply {
                                        removeAll { it.documentId == todoItem.documentId }
                                    }

                                    //Log.d("TodoItems", "Todo Items after deletion: $todoItems")

                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = null
                                    )
                                })
                            DropdownMenuItem(
                                text = { Text("edit") },
                                onClick = {
                                    //expanded = false

                                    showEventEdit.value = true
                                    },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = null
                                    )
                                })
                            if(showEventEdit.value) {
                                EditEvents(
                                    showEditEvent = showEventEdit,
                                    viewModel = viewModel,
                                    todoItem = todoItem
                                )
                            }

                        }
                    }

                )
            }
        }
    }
}

fun deleteTodoItemFromFirebase(documentId: String) {
    getUserDocumentByEmail(
        userEmail,
        onSuccess = { userResult ->
            val userDocumentRef = userResult.documents[0].reference

            userDocumentRef.collection("events").document(documentId).delete()
                .addOnSuccessListener {
                    Log.d("FirebaseDelete", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseDelete", "Error deleting document", e)
                }
        },
        onFailure = { exception ->
            Log.e("FirebaseDelete", "Failed to fetch user document", exception)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun deleteSelectedTodoItemsFromFirebase(selectedItemsState: MutableState<List<TodoItem>>) {
    val selectedItems: List<TodoItem> = selectedItemsState.value
    for (todoItem in selectedItems) {
        deleteTodoItemFromFirebase(todoItem.documentId)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun markSelectedTodoItemsAsDone(selectedItemsState: MutableState<List<TodoItem>>) {
    val selectedItems: List<TodoItem> = selectedItemsState.value
    for (todoItem in selectedItems) {
        todoItem.isDone = true
        updateTodoItemInFirebase(todoItem)
    }

}

fun updateTodoItemInFirebase(todoItem: TodoItem) {
    getUserDocumentByEmail(
        userEmail,
        onSuccess = { userResult ->
            val userDocumentRef = userResult.documents[0].reference
            userDocumentRef.collection("events").document(todoItem.documentId)
                .update("isDone", true)
                .addOnSuccessListener {
                    Log.d("FirebaseUpdate", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseUpdate", "Error updating document", e)
                }
        },
        onFailure = { exception ->
            Log.e("FirebaseUpdate", "Failed to fetch user document", exception)
        }
    )
}
