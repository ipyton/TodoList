package com.example.myapplication.components

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


@RequiresApi(64)
@Composable
fun AddEvents(
    showAddEvent: MutableState<Boolean>
) {
    var title by remember {
        mutableStateOf("")
    }
    var introduction by remember {
        mutableStateOf("")
    }
    var displayDateTimePicker = remember {
        mutableStateOf(false)
    }

    var displayLocationPicker = remember {
        mutableStateOf(false)
    }

    if (displayDateTimePicker.value) {
        DateAndTimePicker(displayDateTimePicker)
    }

    else {
        Dialog(onDismissRequest = {  }) {
            // Draw a rectangle shape with rounded corners inside the dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                ,
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("title") },
                        singleLine = true
                    )
                    TextField(
                        value = introduction,
                        onValueChange = { introduction = it },
                        label = { Text("introduction") },
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        ){

                            Text(modifier = Modifier.padding(12.dp),text = "Use current location?")
                            Checkbox(
                                checked = displayLocationPicker.value,
                                onCheckedChange = { displayLocationPicker.value = !displayLocationPicker.value }
                            )

                    }
                    if (!displayLocationPicker.value) {
                        TextField(value = "", onValueChange = {})
                    }
                    Button(
                        onClick = {
                            displayDateTimePicker.value = true
                        }
                    ) {
                        Text(text = "Select date and time")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TextButton(
                            onClick = { showAddEvent.value = false },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Cancel")

                        }
                        TextButton(
                            onClick = {  },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}