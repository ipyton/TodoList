package com.example.myapplication.Pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.myapplication.Activities.ActivityItem
import com.example.myapplication.components.doneTodoItems
import com.example.myapplication.components.scheduledTodoItems
import com.example.myapplication.entities.TodoItem

@Composable
fun Done(isVisible: MutableState<Boolean>) {
    var selectedTodoItems = remember { mutableStateOf(mutableListOf<TodoItem>()) }

    LazyColumn {
        items(doneTodoItems.size) { index ->
            val todoItem = doneTodoItems[index]
            var state by remember { mutableStateOf(false) }
            var expanded by remember { mutableStateOf(false) }
            Column {
                ListItem(
                    headlineContent = { Text(todoItem.title) },
                    overlineContent = { Text(todoItem.introduction) },
                    supportingContent = { Text(todoItem.time) },
                    leadingContent = {
                        RadioButton(
                            selected = state,
                            onClick = { state = !state
                                if (state) {
                                    selectedTodoItems.value.add(todoItem)
                                } else {
                                    selectedTodoItems.value.remove(todoItem)
                                }
                                isVisible.value = selectedTodoItems.value.isNotEmpty()
                                //Log.d("RadioButtonClicked", "RadioButton clicked for TodoItem: ${todoItem.title}, isSelected: $state")
                                //Log.d("SelectedTodoItems", "Selected Todo Items: ${selectedTodoItems.value}, TodoItem: ${todoItem.title}")
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
                                    doneTodoItems.removeAll { it.documentId == todoItem.documentId }

                                    //Log.d("TodoItems", "Todo Items after deletion: $todoItems")

                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = null
                                    )
                                })
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = { /* Handle settings! */ },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Settings,
                                        contentDescription = null
                                    )
                                })

                        }
                    }

                )
            }
        }
    }
}