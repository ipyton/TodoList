package com.example.myapplication

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Locale


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
fun Done() {
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
fun Location() {

}



@Composable
fun MyNavigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "today") {
        composable("Today") {
            ActivityList()
        }
        composable("Scheduled") { Scheduled() }
        composable("Done") { Done() }
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


@RequiresApi(0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayDatePicker(display: MutableState<Boolean>) {
    val calendar = Calendar.getInstance()
    calendar.set(2024, 0, 1) // month (0) is January
    val state = rememberTimePickerState()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    var selectedDate by remember {
        mutableStateOf(calendar.timeInMillis)
    }


    var currentState by remember {
        mutableStateOf("date") // date/ time selection
    }

        if (display.value) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            DatePickerDialog(onDismissRequest = { display.value = false}, confirmButton = {
                TextButton(
                onClick = { display.value = false },
            ) {
                Text("Confirm")
            } }) {
                Row(
                ) {
                    TextButton(
                        onClick = { currentState = "date" },
                    ) {
                        Text("date")
                    }
                    TextButton(
                        onClick = { currentState = "time" },
                    ) {
                        Text("time")
                    }
                }
                if (currentState == "date") {
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )}
                else {
                    TimePicker(state = state)
                }
            }
        }


    }
}

fun onDismissRequest() {
    TODO("Not yet implemented")
}

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
    var displayDateTimePicker = remember {
        mutableStateOf(false)
    }

    var displayLocationPicker = remember {
        mutableStateOf(false)
    }

    if (displayDateTimePicker.value) {
        DisplayDatePicker(displayDateTimePicker)
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
                        Text(text = "Use current location?")
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
@Preview
@Composable
fun stepForRegistration() {
    val numberStep = 4
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList= arrayListOf("Step 1","Step 2","Step 3","Step 4")

    Stepper(
        numberOfSteps = numberStep,
        currentStep = currentStep,
        stepDescriptionList = titleList
    )

}

@Preview
@Composable
fun stepForResetPassword() {
    val numberStep = 4
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList= arrayListOf("Step 1","Step 2","Step 3","Step 4")

    Stepper(
        numberOfSteps = numberStep,
        currentStep = currentStep,
        stepDescriptionList = titleList
    )

}

@Composable
fun registrationPage(navController: NavHostController) {
    val numberStep = 4
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList= arrayListOf("Step 1","Step 2")

    Stepper(
        numberOfSteps = numberStep,
        currentStep = currentStep,
        stepDescriptionList = titleList
    )

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
            if (showEventAdder.value) {
                AddEvents(showEventAdder)
            }

            MyNavigator(navController = navController)
        }
    }
}

typealias ComposableFun = @Composable () -> Unit


