package com.example.myapplication.Pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun Login(navController: NavHostController, login: MutableState<Boolean>) {
    var name by remember { mutableStateOf ("") }
    var surname by remember { mutableStateOf ("") }

    Column( modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center) {
        Text(text = "Registration Form",
            style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },


            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Button(onClick = { login.value = true
        }) {
            Text("Login")
        }

        Button(onClick = { navController.navigate("forget") }) {
            Text("Forgot")
        }


        Button(onClick = { navController.navigate("registration") }) {
            Text("Registration")
        }
    }
}