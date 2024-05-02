package com.example.myapplication.Pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.components.PieChartScreen


@Composable
fun Account(navController: NavHostController, login: MutableState<Boolean>) {
    val done = remember {
        mutableIntStateOf(2)
    }
    val missed = remember {
        mutableIntStateOf(3)
    }
    val future = remember {
        mutableIntStateOf(4)
    }


    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Welcome",
            modifier = Modifier.padding(25.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )
        Text(
            text = "Finished: " + done.intValue,

            modifier = Modifier.padding(start = 40.dp),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "Missed:   " + missed.intValue,

            modifier = Modifier.padding(start = 40.dp),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "Future:    " + future.intValue,
            modifier = Modifier.padding(start = 40.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize =18.sp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp),Arrangement.Center
        ) {
            PieChartScreen(future = future, done = done, missed = missed)
        }
        Button(
            onClick = { navController.navigate("Login")
                login.value = false},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log out")
        }
    }
}