package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import java.time.Instant
import java.util.Calendar


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    mainStage()

                }
            }
        }
    }
}


@Composable
fun mainStage() {
    val isLoggedIn = remember {
        mutableStateOf(false)
    }

    val userName = remember {
        mutableStateOf("")
    }
    val userAvatar = remember {
        mutableStateOf("")
    }
    return if (isLoggedIn.value) {
        ScaffoldExample()
    }
    else{
        AccountPage(login = isLoggedIn)
    }
}


@Composable
fun Scheduled() {

}

@Composable
fun Done() {

}

@Composable
fun Location() {

}



@Composable
fun MyNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "today") {
        composable("Today") {
            ActivityList()
        }
        composable("Scheduled") { Text(text = "Scheduled") }
        composable("Done") { Text(text = "Done") }
        composable("Location") { Text(text = "Location") }
    }
}

@Composable
fun AccountPage(login:MutableState<Boolean>) {
    var navigator = rememberNavController()
    AccountNavigator(navigator, login)
}

@Composable
fun AccountNavigator(navController: NavHostController, login:MutableState<Boolean>) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            FormEntry(navController, login = login)
            }
        composable("forget") { forgetPage(navController) }
        composable("registration") { registrationPage(navController) }

    }
}








@Composable
fun FormEntry(navController: NavHostController,login:MutableState<Boolean>) {
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

        Button(onClick = { navController.navigate("forget") }) {
            Text("Forgot")
        }

        Button(onClick = { login.value = true
                            }) {
            Text("Login")
        }
        Button(onClick = { navController.navigate("registration") }) {
            Text("Registration")
        }
    }
}

@RequiresApi(0)
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDatePicker() {
    val calendar = Calendar.getInstance()
    calendar.set(2024, 0, 1) // month (0) is January
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        //selectedDateMillis!! null safety because type declared as Long? selectedDate = datePickerState.selectedDateMillis!!
                    }) { Text(text = "OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) {
                        Text(text = "Cancel")
                    }
                }
            ) //end of dialog
            { //still column scope
                DatePicker(
                    state = datePickerState
                )
            }
        }// end of if
        Button(
            onClick = {
                showDatePicker = true
            }
        ) {
            Text(text = "select date and time")
        }

    }
}


@Preview
@Composable
fun ActivityItem() {
    // activity: Activity
    var state by remember { mutableStateOf(true) }
    Column {
        ListItem(
            headlineContent = { Text("Title") },
            overlineContent = { Text("Details") },
            supportingContent = { Text("Woodside...... 10pm") },
            leadingContent = {
                RadioButton(
                    selected = state,
                    onClick = { state = true },
                    modifier = Modifier.semantics { contentDescription = "Localized Description" }
                )
            },
            trailingContent = { Icon(Icons.Filled.MoreVert, contentDescription = "change status") }
        )
    }
}



@Composable
fun ActivityList() {
    val productList = listOf(
        1,2,3
        // Add more items as needed
    )
    LazyColumn {
        itemsIndexed(productList) {idx, count ->
            ActivityItem()
        }
    }
}



@Composable
fun AddEvents(
    showAddEvent:MutableState<Boolean>
) {
    var title by remember {
        mutableStateOf("")
    }
    var introduction by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = {  }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
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
                    Text(text = "use current location?")
                    Checkbox(
                        checked = true,
                        onCheckedChange = {  }
                    )
                }

                DisplayDatePicker()
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



@Composable
fun registrationPage(navController: NavHostController) {
    Text(text = "this is registration page")
}

@Composable
fun forgetPage(navController: NavHostController) {
    Text(text = "this is forget page")
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample() {
    var presses by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Today", "Scheduled", "Done", "Location")
    val location : ComposableFun = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
    val scheduled : ComposableFun = { Icon(Icons.Default.DateRange, contentDescription = "scheduled") }
    val done : ComposableFun = {  Icon(Icons.Filled.CheckCircle, contentDescription = "Done")}
    val today : ComposableFun = { Icon(Icons.Rounded.Notifications, contentDescription = "today") }
    val navController = rememberNavController()
    val icons = listOf(today, scheduled, done, location)
    var showEventAdder= remember {
        mutableStateOf(false)
    }


//    val icons = listOf(Icon(Icons.Rounded.Notifications, contentDescription = "today"),
//        Icon(Icons.Default.DateRange, contentDescription = "scheduled"),
//        Icon(Icons.Filled.CheckCircle, contentDescription = "Done"),
//        Icon(Icons.Filled.LocationOn, contentDescription = "Location"))


    var barContent by remember {
        mutableStateOf("TodoList")
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(items[selectedItem])
                }
            )
        },
        bottomBar = {
//            BottomAppBar(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                contentColor = MaterialTheme.colorScheme.primary,
//            ) {
//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//                    text = "Bottom app bar",
//                )
//            }
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { icons[index]() },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            if (selectedItem != index) {
                                selectedItem = index
                                navController.navigate(items[index])
                            }

                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEventAdder.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
//            Text(
//                modifier = Modifier.padding(8.dp),
//                text =
//                """
//                    This is an example of a scaffold. It uses the Scaffold composable's parameters to create a screen with a simple top app bar, bottom app bar, and floating action button.
//
//                    It also contains some basic inner content, such as this text.
//
//                    You have pressed the floating action button $presses times.
//                """.trimIndent(),
//            )
            if (showEventAdder.value) {
                AddEvents(showEventAdder)
            }

            MyNavigator(navController = navController)
        }
    }
}

typealias ComposableFun = @Composable () -> Unit


