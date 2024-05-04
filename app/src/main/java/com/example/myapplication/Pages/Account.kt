package com.example.myapplication.Pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.AndroidAlarmScheduler
import com.example.myapplication.components.PieChartScreen
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.util.Calendar

fun isMissed(date:String, time:String):Boolean{
    val calendar = Calendar.getInstance()
    val todoCalendar = Calendar.getInstance()


    val splitDate = date.split("-")
    val splitTime = time.split(":")
    if (splitDate.size != 3 || splitTime.size != 2) {
        return false
    }
    todoCalendar.set(splitDate[0].toInt(),splitDate[1].toInt(),splitDate[2].toInt(),splitTime[0].toInt(),splitTime[1].toInt())

    return if ( calendar.before(todoCalendar) ) {
        println("missed")
        println(todoCalendar.toString() )
        true
    } else {
        println("done")
        println(todoCalendar.toString())
        false
    }

}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Account(navController: NavHostController, login: MutableState<Boolean>,viewModel: TodoListViewModel = viewModel(), androidAlarmScheduler: AndroidAlarmScheduler) {

    val scheduled by viewModel.scheduledTodoItems.collectAsState()
    val today by viewModel.todayTodoItems.collectAsState()
    val done by viewModel.doneTodoItems.collectAsState()

    var missed = 0
    var undoAmount = 0
    val todayAmount = today.size
    val doneAmount = done.size

    scheduled.forEach {
        if (isMissed(it.date, it.time)) {
            missed ++
        } else {
            undoAmount ++
        }
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
            text = "Today: $todayAmount",

            modifier = Modifier.padding(start = 40.dp),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "Finished: $doneAmount",

            modifier = Modifier.padding(start = 40.dp),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "Missed:   $missed",

            modifier = Modifier.padding(start = 40.dp),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "Future:    $undoAmount",
            modifier = Modifier.padding(start = 40.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize =18.sp)
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),Arrangement.Center
        ) {
            PieChartScreen(future = undoAmount, done = doneAmount, missed = missed, today=todayAmount )
        }
        Button(
            onClick = {


                Firebase.auth.signOut()
                androidAlarmScheduler.clear()
                login.value = false
                //navController.navigate("Login")
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log out")

        }
    }
}