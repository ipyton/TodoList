package com.example.myapplication


import android.app.Application
import android.util.Log
import android.content.pm.ActivityInfo
import android.graphics.Matrix
import android.opengl.ETC1
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.VideoView

import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.viewinterop.AndroidView

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.myapplication.Pages.Account
import com.example.myapplication.Pages.Done
import com.example.myapplication.Pages.ForgetPageOne

import com.example.myapplication.Pages.Location
import com.example.myapplication.Pages.Login
import com.example.myapplication.Pages.RegistrationPageOne

import com.example.myapplication.Pages.RegistrationPageTwo

import com.example.myapplication.Pages.Scheduled
import com.example.myapplication.Pages.Today
import com.example.myapplication.components.AddEvents
import com.example.myapplication.entities.TodoItem
import com.example.myapplication.util.FirebaseUtil.deleteSelectedTodoItemsFromFirebase
import com.example.myapplication.util.FirebaseUtil.markSelectedTodoItemsAsDone
import com.example.myapplication.util.GoogleAuthUIClient
import com.example.myapplication.viewmodel.TodoItemViewModel
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max


class MainActivity : ComponentActivity() {

    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId("467501865267-im97al3s39cei2j2l17a1karb2r7jmmj.apps.googleusercontent.com")
        .build()

    val request: androidx.credentials.GetCredentialRequest = androidx.credentials.GetCredentialRequest.Builder()
        .setCredentialOptions(listOf(googleIdOption))
        .build()

    //val db = Firebase.firestore
    val todoListViewModel = TodoListViewModel()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*lifecycleScope.launch(Dispatchers.IO) {
            // 创建一个 TodoItem 实例
            val todoItem = TodoItem(
                documentId = "doc1",
                date = "2024-04-28",
                introduction = "This is a test todo item",
                isDone = false,
                latitude = 0.0,
                longitude = 0.0,
                time = "12:00 PM",
                title = "Test Todo Item",
                selected = false
            )

            // 插入数据到数据库
            db.todoItemDao().insert(todoItem)
        }*/
        val googleAuthUiClient by lazy{ GoogleAuthUIClient(context=applicationContext, Identity.getSignInClient(applicationContext)) }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val loginState = remember {
                        mutableStateOf(false)
                    }
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if(result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    loginState.value = signInResult.data == null
                                }
                            }
                        }
                    )

                    mainStage(loginState, launcher, googleAuthUiClient)
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun mainStage(
    loginState: MutableState<Boolean>,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    googleAuthUiClient: GoogleAuthUIClient,

) {


    val userName = remember {
        mutableStateOf("")
    }
    val userAvatar = remember {
        mutableStateOf("")
    }

    val visible = remember {
        mutableStateOf(false)
    }



    return if (loginState.value) {
        ScaffoldExample(login = loginState, isVisible = visible, googleAuthUiClient)
    }
    else{
        AccountPage(login = loginState, googleAuthUiClient)
    }
}





@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MyNavigator(navController: NavHostController,
                login: MutableState<Boolean>,
                isVisible: MutableState<Boolean>,
                googleAuthUiClient: GoogleAuthUIClient,
                todoListViewModel: TodoListViewModel) {
    DisposableEffect(key1 = navController) {
        val callback = NavController.OnDestinationChangedListener { _, _, _ ->
            isVisible.value = false
            //selectedTodoItems.clear()
        }
        navController.addOnDestinationChangedListener(callback)
        onDispose {
            navController.removeOnDestinationChangedListener(callback)
        }
    }

    NavHost(navController = navController, startDestination = "today") {
        composable("Login") {
            AccountPage(login = login,  googleAuthUiClient)
        }
        composable("Today") {
            Today(isVisible = isVisible, viewModel = todoListViewModel)
        }
        composable("Scheduled") {
            Scheduled(isVisible = isVisible, viewModel = todoListViewModel)
        }
        composable("Done") { Done(isVisible = isVisible, viewModel = todoListViewModel) }
        composable("Location") { Location() }
        composable("Account") { Account(navController, login=login) }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AccountPage(
    login: MutableState<Boolean>,
    googleAuthUiClient: GoogleAuthUIClient
) {
    var navigator = rememberNavController()
    AccountNavigator(navigator, login, googleAuthUiClient)
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AccountNavigator(
    navController: NavHostController,
    login: MutableState<Boolean>,
    googleAuthUiClient: GoogleAuthUIClient,
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(navController, login, googleAuthUiClient )
        }
        //composable("forget") { forgetPage(navController) }
        composable("forgetOne") { ForgetPageOne(navController, login) }
//        composable("forgetTwo") { ForgetPageTwo(navController, login) }
//        composable("forgetThree") { ForgetPageThree(navController, login) }
        composable("registrationOne") { RegistrationPageOne(navController) }
        composable("registrationTwo") { RegistrationPageTwo(navController) }



    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample(
    login: MutableState<Boolean>,
    isVisible: MutableState<Boolean>,
    googleAuthUiClient: GoogleAuthUIClient,
) {
    var presses by remember { mutableIntStateOf(0) }
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Today", "Scheduled", "Done", "Location", "Account")
    val account : ComposableFun = { Icon(Icons.Default.Home, contentDescription = "Account") }
    val location : ComposableFun = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
    val scheduled : ComposableFun = { Icon(Icons.Default.DateRange, contentDescription = "scheduled") }
    val done : ComposableFun = {  Icon(Icons.Filled.CheckCircle, contentDescription = "Done")}
    val today : ComposableFun = { Icon(Icons.Rounded.Notifications, contentDescription = "today") }
    val navController = rememberNavController()
    val todoListViewModel = remember { TodoListViewModel() }
    val todoItemViewModel = remember { TodoItemViewModel() }
    val selectedTodoItems by todoListViewModel.selectedTodoItems.collectAsState()


    val icons = listOf(today, scheduled, done, location, account)
    var showEventAdder= remember {
        mutableStateOf(false)
    }


    var barContent by remember {
        mutableStateOf("TodoList")
    }


    val viewModel = remember { todoListViewModel }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(viewModel) {
        viewModel.fetchAndGroupTodoItems()
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
                },
                actions = {
                    if (selectedTodoItems.isNotEmpty()) {
                        if (items[selectedItem] == "Today" || items[selectedItem] == "Scheduled") {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    markSelectedTodoItemsAsDone(selectedTodoItems, viewModel)
                                    isVisible.value = false
                                }
                            }) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "finish")
                            }
                        }
                        if (items[selectedItem] == "Today" || items[selectedItem] == "Scheduled"
                            || items[selectedItem] == "Done"
                        ) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    deleteSelectedTodoItemsFromFirebase(
                                        selectedTodoItems,
                                        todoListViewModel
                                    )
                                    isVisible.value = false

                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "delete")
                            }

                        }
                    }
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
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (showEventAdder.value) {
                AddEvents(showEventAdder, viewModel, todoItemViewModel)
            }
            MyNavigator(navController = navController, login = login, isVisible = isVisible,googleAuthUiClient,todoListViewModel = todoListViewModel
            )
        }
    }
}


typealias ComposableFun = @Composable () -> Unit


