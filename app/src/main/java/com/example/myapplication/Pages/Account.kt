package com.example.myapplication.Pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun Account(navController: NavHostController, login: MutableState<Boolean>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "welcome xxxx!",
            modifier = Modifier.padding(36.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )

        Button(
            onClick = { navController.navigate("Login")
                login.value = false},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log out")
        }
    }
}