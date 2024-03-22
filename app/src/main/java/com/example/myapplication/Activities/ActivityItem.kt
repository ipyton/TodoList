package com.example.myapplication.Activities

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview



@Preview
@Composable
fun ActivityItem() {
    // activity: Activity
    var state by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    Column {
        ListItem(
            headlineContent = { Text("Title") },
            overlineContent = { Text("Details") },
            supportingContent = { Text("Woodside...... 10pm") },
            leadingContent = {
                RadioButton(
                    selected = state,
                    onClick = { state = !state },
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
                        onClick = { /* Handle edit! */ },
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
