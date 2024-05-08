package com.example.myapplication.components

import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.util.Calendar


@RequiresApi(0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePicker(display: MutableState<Boolean>,
                      onDateTimeSelected: (String, String) -> Unit) {
    val calendar = Calendar.getInstance()
    calendar.set(2024, 0, 1) // month (0) is January
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState()


    var selectedDateString by remember {
        mutableStateOf("")
    }

    var selectedTimeString by remember {
        mutableStateOf("")
    }

    var currentState by remember {
        mutableStateOf("date") // date/ time selection
    }

    fun saveSelectedDateTime() {
        val selectedDateMillis = datePickerState.selectedDateMillis
        selectedDateString = selectedDateMillis?.let { Instant.ofEpochMilli(it).toString().substring(0, 10) } ?: ""
        Log.d(TAG, "saveSelectedDateTime: " + selectedDateMillis?.let { Instant.ofEpochMilli(it).toString() })
        val hour = timePickerState.hour
        val minute = timePickerState.minute
        selectedTimeString = "$hour:$minute"
        selectedTimeString = String.format("%02d:%02d", hour, minute)
        onDateTimeSelected(selectedDateString, selectedTimeString)
        Log.d("SelectedDateTime", "Selected Date: $selectedDateString")
        Log.d("SelectedDateTime", "Selected Time: $selectedTimeString")
    }

    if (display.value) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            DatePickerDialog(onDismissRequest = { display.value = false}, confirmButton = {
                TextButton(
                    onClick = {
                        saveSelectedDateTime()
                        display.value = false },
                ) {
                    Text("Confirm")
                } }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(start = 100.dp)
                ) {
                    if (currentState == "date") {
                        Button(onClick = { currentState = "date" },) {
                            Text("date")
                        }
                        FilledTonalButton(
                            onClick = { currentState = "time" },
                        ) {
                            Text("time")
                        }
                    }
                    else if (currentState == "time") {
                        FilledTonalButton(
                            onClick = { currentState = "date" },
                        ) {
                            Text("date")
                        }
                        Button(
                            onClick = { currentState = "time" },
                        ) {
                            Text("time")
                        }

                    }
                }
                if (currentState == "date") {
                        DatePicker(
                            state = datePickerState,
                            title = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                }
                else {
                    TimePicker(state = timePickerState,modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
