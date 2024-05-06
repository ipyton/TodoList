package com.example.myapplication.Pages

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.myapplication.Activities.ActivityItem
import com.example.myapplication.components.EditEvents

import com.example.myapplication.entities.TodoItem
import com.example.myapplication.util.FirebaseUtil.deleteTodoItemFromFirebase
import com.example.myapplication.viewmodel.TodoListViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Done(isVisible: MutableState<Boolean>, viewModel: TodoListViewModel) {
    val doneItems by viewModel.doneTodoItems.collectAsState()
    val selectedItems by viewModel.selectedTodoItems.collectAsState()
    var expandedItemId by remember { mutableStateOf("") }
    var showEventEdit = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    //var selectedTodoItems = remember { mutableStateOf(mutableListOf<TodoItem>()) }
    //var scheduledItems = remember { mutableStateOf(scheduledTodoItems.toMutableList()) }

    LazyColumn {
        items(doneItems) {
            val isSelected = selectedItems.contains(it)
            var expanded by remember { mutableStateOf(false) }
            Column {
                if (showEventEdit.value && expandedItemId == it.documentId) {
                    EditEvents(
                        showEditEvent = showEventEdit,
                        viewModel = viewModel,
                        todoItem = it
                    )
                }
                ListItem(
                    headlineContent = { Text(it.title) },
                    overlineContent = { Text(it.introduction) },
                    supportingContent = { Text("Date: " + it.date + " " + "Time: " + it.time) },
                    leadingContent = {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.toggleTodoItemSelection(it)

                                isVisible.value = selectedItems.isNotEmpty()
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
                            expandedItemId = it.documentId
                        }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "change status") }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    coroutineScope.launch {
                                        //Log.d("TodoItems", "Todo Items before deletion: $todoItems")
                                        expandedItemId = ""
                                        deleteTodoItemFromFirebase(userId, it.documentId) {
                                            viewModel.fetchAndGroupTodoItems()
                                        }
                                        expanded = false
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
                                    showEventEdit.value = true
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
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

