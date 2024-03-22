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
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Activities.ActivityItem
import com.example.myapplication.Pages.Account
import com.example.myapplication.Pages.Done
import com.example.myapplication.Pages.Location
import com.example.myapplication.Pages.Login
import com.example.myapplication.Pages.Scheduled
import com.example.myapplication.Pages.Today
import com.example.myapplication.Pages.forgetPage
import com.example.myapplication.Pages.registrationPage
import com.example.myapplication.components.AddEvents
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
        ScaffoldExample(login = isLoggedIn)
    }
    else{
        AccountPage(login = isLoggedIn)
    }
}




@Composable
fun MyNavigator(navController: NavHostController, login: MutableState<Boolean>) {
    NavHost(navController = navController, startDestination = "today") {
        composable("Login") {
            AccountPage(login = login)
        }
        composable("Today") {
            Today()
        }
        composable("Scheduled") { Scheduled() }
        composable("Done") { Done() }
        composable("Location") { Location() }
        composable("Account") { Account(navController, login=login) }
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
            Login(navController, login = login)
        }
        composable("forget") { forgetPage(navController) }
        composable("registration") { registrationPage(navController, login) }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample(login:MutableState<Boolean>) {
    var presses by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Today", "Scheduled", "Done", "Location", "Account")
    val account : ComposableFun = { Icon(Icons.Default.Home, contentDescription = "Account") }
    val location : ComposableFun = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
    val scheduled : ComposableFun = { Icon(Icons.Default.DateRange, contentDescription = "scheduled") }
    val done : ComposableFun = {  Icon(Icons.Filled.CheckCircle, contentDescription = "Done")}
    val today : ComposableFun = { Icon(Icons.Rounded.Notifications, contentDescription = "today") }
    val navController = rememberNavController()
    val icons = listOf(today, scheduled, done, location, account)
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
            MyNavigator(navController = navController, login = login)
        }
    }
}

typealias ComposableFun = @Composable () -> Unit


