package com.example.myapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.MainApplication.Companion.context
import com.example.myapplication.Pages.Account
import com.example.myapplication.Pages.Done
import com.example.myapplication.Pages.ForgetPageOne
import com.example.myapplication.Pages.Location
import com.example.myapplication.Pages.Login
import com.example.myapplication.Pages.RegistrationPageOne
import com.example.myapplication.Pages.RegistrationPageTwo
import com.example.myapplication.Pages.Scheduled
import com.example.myapplication.Pages.Today
import com.example.myapplication.Pages.userEmail
import com.example.myapplication.Pages.userId
import com.example.myapplication.components.AddEvents
import com.example.myapplication.util.FirebaseUtil.deleteSelectedTodoItemsFromFirebase
import com.example.myapplication.util.FirebaseUtil.markSelectedTodoItemsAsDone
import com.example.myapplication.util.GoogleAuthUIClient
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.LocalDate


private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "todolist"
        val descriptionText = "this is a channel for todo list"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("todolist", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val androidAlarmScheduler = AndroidAlarmScheduler(this)

        val googleAuthUiClient by lazy{ GoogleAuthUIClient(context=applicationContext, Identity.getSignInClient(applicationContext)) }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Firebase.database.setPersistenceEnabled(true)

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

                    mainStage(loginState, launcher, googleAuthUiClient,androidAlarmScheduler)
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
    androidAlarmScheduler: AndroidAlarmScheduler
) {


    val visible = remember {
        mutableStateOf(false)
    }
    if(Firebase.auth.currentUser!=null) {
        loginState.value = true
    } else {
        loginState.value = false
    }
    return if (loginState.value ) {
        Firebase.database.getReference().onDisconnect().apply {
            Toast.makeText(
                context,
                "You are offline now.",
                Toast.LENGTH_SHORT,
            ).show()
        }
        ScaffoldExample(login = loginState, isVisible = visible, googleAuthUiClient,androidAlarmScheduler)
    }
    else{
        Firebase.database.getReference().onDisconnect().apply {
            Toast.makeText(
                context,
                "You are offline now.",
                Toast.LENGTH_SHORT,
            ).show()
        }
        AccountPage(login = loginState, googleAuthUiClient)
    }
}





@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MyNavigator(navController: NavHostController,
                login: MutableState<Boolean>,
                isVisible: MutableState<Boolean>,
                googleAuthUiClient: GoogleAuthUIClient,
                todoListViewModel: TodoListViewModel,
                androidAlarmScheduler: AndroidAlarmScheduler
                ) {
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
//        composable("Login") {
//            AccountPage(login = login,  googleAuthUiClient)
//        }
        composable("Today") {
            Today(isVisible = isVisible, viewModel = todoListViewModel)
        }
        composable("Scheduled") {
            Scheduled(isVisible = isVisible, viewModel = todoListViewModel)
        }
        composable("Done") { Done(isVisible = isVisible, viewModel = todoListViewModel) }
        composable("Location") { Location(viewModel = todoListViewModel) }
        composable("Account") { Account(navController, login=login, viewModel=todoListViewModel,androidAlarmScheduler) }
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
        composable("forgetOne") { ForgetPageOne(navController, login) }
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
    androidAlarmScheduler: AndroidAlarmScheduler
) {
    val currentDate = LocalDate.now()
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Today", "Scheduled", "Done", "Location", "Account")
    val account : ComposableFun = { Icon(Icons.Default.Home, contentDescription = "Account") }
    val location : ComposableFun = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
    val scheduled : ComposableFun = { Icon(Icons.Default.DateRange, contentDescription = "scheduled") }
    val done : ComposableFun = {  Icon(Icons.Filled.CheckCircle, contentDescription = "Done")}
    val today : ComposableFun = { Icon(Icons.Rounded.Notifications, contentDescription = "today") }
    val navController = rememberNavController()
    val todoListViewModel = TodoListViewModel()
    val selectedTodoItems by todoListViewModel.selectedTodoItems.collectAsState()


    val icons = listOf(today, scheduled, done, location, account)
    var showEventAdder= remember {
        mutableStateOf(false)
    }




    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(todoListViewModel) {
        userEmail = Firebase.auth.currentUser?.email.toString()
        userId = Firebase.auth.currentUser?.uid ?: ""
        todoListViewModel.fetchAndGroupTodoItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    if (selectedItem == 0)
                    {
                        Text(items[selectedItem] + " " + currentDate)
                    }
                    else
                    {
                        Text(items[selectedItem])
                    }

                },
                actions = {
                    if (selectedTodoItems.isNotEmpty()) {
                        if (items[selectedItem] == "Today" || items[selectedItem] == "Scheduled") {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    markSelectedTodoItemsAsDone(selectedTodoItems, todoListViewModel,androidAlarmScheduler)
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
                                        todoListViewModel,
                                        androidAlarmScheduler = androidAlarmScheduler
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
                AddEvents(showEventAdder, todoListViewModel, scheduler = androidAlarmScheduler)
            }
            MyNavigator(navController = navController, login = login, isVisible = isVisible,googleAuthUiClient,todoListViewModel = todoListViewModel
            ,androidAlarmScheduler)
        }
    }
}


typealias ComposableFun = @Composable () -> Unit


