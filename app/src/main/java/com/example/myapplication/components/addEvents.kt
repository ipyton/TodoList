package com.example.myapplication.components

import android.Manifest
import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.myapplication.AndroidAlarmScheduler
import com.example.myapplication.ApiClient
import com.example.myapplication.MainActivity
import com.example.myapplication.MainApplication.Companion.context
import com.example.myapplication.Pages.userId
import com.example.myapplication.R
import com.example.myapplication.entities.BusinessEntity
import com.example.myapplication.util.FirebaseUtil.writeToFirebase
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar




@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(64)
@Composable
fun AddEvents(
    showAddEvent: MutableState<Boolean>,
    viewModel: TodoListViewModel,
    scheduler: AndroidAlarmScheduler
) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )
    //val intent = Intent(context, AlarmReceiver.class)

    val permissionRequest = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        permission -> run {
        }

    }
    val request = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissions->
        run {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }
                else -> {

                }
            }

        }
    }


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

    var searchLocation by remember {
        mutableStateOf("")

    }

    var latitude by remember {
     mutableStateOf(-1.0)
    }

    var longitude by remember {
      mutableStateOf(-1.0)
    }

    var isDone by remember {
        mutableStateOf(false)
    }

    val currentDate = LocalDate.now()


    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ISO_TIME
    val formattedTime = currentTime.format(formatter)
    Log.d(ContentValues.TAG, "AddEvents: " + formattedTime)

    var selectedDateString by remember {
        mutableStateOf("")
    }

    var selectedTimeString by remember {
        mutableStateOf("")
    }


    var activeSuggestion by remember {
        mutableStateOf(false)
    }

    var suggestions = remember {
        mutableListOf<BusinessEntity>()

    }
    var coroutineScope = rememberCoroutineScope()

    var eventPlace = remember {
        mutableStateOf(BusinessEntity("",1.0,1.0,""))
    }

    val context = LocalContext.current




    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location : Location? ->
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
        } else {
            Log.d("location", "did not get the user location")
        }
    }

    if (displayDateTimePicker.value) {
        DateAndTimePicker(displayDateTimePicker,
            onDateTimeSelected = { date, time ->
                selectedDateString = date
                selectedTimeString = time
            })
    }
    else {
        Dialog(onDismissRequest = { }) {
            // Draw a rectangle shape with rounded corners inside the dialog
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp),
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

                            Text(modifier = Modifier.padding(12.dp),text = "Use current location?")
                            Checkbox(
                                checked = displayLocationPicker.value,
                                onCheckedChange = {
                                    displayLocationPicker.value = !displayLocationPicker.value
                                    activeSuggestion = false
                                }
                            )

                    }
                    if (!displayLocationPicker.value) {
                        if (ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            request.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION))


                        fusedLocationProviderClient.getLastLocation()
                        DockedSearchBar(
                            query = searchLocation,
                            leadingIcon={
                                if (activeSuggestion) IconButton(onClick = {
                                searchLocation = ""
                                activeSuggestion = false
                                suggestions.clear()
                            }) {
                                 Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Delete and disable the text input box")
                            }},
                            trailingIcon = {
                                if (activeSuggestion)
                                IconButton(onClick = {
                                searchLocation = ""
                                suggestions.clear()
                            }) {
                                Icon(Icons.Outlined.Clear, contentDescription = "Delete all contents")
                            }},
                            onQueryChange = {
                                searchLocation = it
                                //suggestions.clear()
                                if (it.isEmpty()) {
                                    activeSuggestion = false
                                }
                                if (it.length >= 3) {
                                    if (latitude == -1.0 || longitude == -1.0) {
                                        return@DockedSearchBar
                                    }
                                    coroutineScope.launch {
                                            ApiClient.apiService.autoCompelete(URLEncoder.encode("$latitude,$longitude", "UTF-8"), URLEncoder.encode(searchLocation, "UTF-8"), "AIzaSyBlBlflFyhquV_cbyY1HVrdz5-K8MDRTok", type="establishment").enqueue(object :
                                                Callback<ResponseBody> {
                                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                    println(call.request().url())
                                                    if (response.isSuccessful) {
                                                        var resultset:MutableList<BusinessEntity> = ArrayList()
                                                        val post = response.body()?.string()
                                                        if (post != null) {
                                                            val jsonObj = JSONObject(post)
                                                            Log.d("location", post)
                                                            val jsonArray =
                                                                jsonObj.getJSONArray("predictions")
                                                            for (i in 0 until jsonArray.length()) {
                                                                if (!activeSuggestion) {
                                                                    break
                                                                }
                                                                val address = jsonArray.getJSONObject(i)
                                                                val formattedAddress = address.getString("description")
                                                                val placeId = address.getString("place_id")
                                                                resultset.add(BusinessEntity(formattedAddress, 0.0, 0.0, placeId))
                                                            }

                                                            suggestions = resultset
                                                            activeSuggestion = true
                                                        }

                                                    } else {
                                                        Log.d("error", "response error!!!")
                                                    }

                                                }

                                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                    // Handle failure
                                                }
                                            }
                                            )
                                }

                            }
},
                            onSearch = {

                            },
                            active = activeSuggestion,
                            onActiveChange = {


                            }
                        ) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )) {
                            suggestions.forEach { suggestion-> run {
                                val get = suggestion
                                ListItem(
                                    headlineContent = { Text(get.formattedAddress) },
                                    leadingContent = {
                                        Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = "Localized description",
                                        )
                                    },
                                    modifier = Modifier.clickable() {
                                        val placeId = get.locationId
                                        searchLocation = get.formattedAddress
                                        activeSuggestion = false
                                        coroutineScope.launch {
                                            ApiClient.apiService.getDetails(
                                                get.locationId,
                                                "AIzaSyBlBlflFyhquV_cbyY1HVrdz5-K8MDRTok"
                                            ).enqueue(object :
                                                Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        val post = response.body()?.string()
                                                        if (post != null) {
                                                            Log.d("search result", post)
                                                            val jsonObj = JSONObject(post)
                                                            get.latitude =
                                                                jsonObj.getJSONObject("result")
                                                                    .getJSONObject("geometry")
                                                                    .getJSONObject("location")
                                                                    .getDouble("lat")
                                                            get.longitude =
                                                                jsonObj.getJSONObject("result")
                                                                    .getJSONObject("geometry")
                                                                    .getJSONObject("location")
                                                                    .getDouble("lng")
                                                            eventPlace.value = get
                                                        }

                                                    } else {
                                                        Log.d("error", "response error!!!")
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseBody>,
                                                    t: Throwable
                                                ) {
                                                    // Handle failure
                                                }
                                            })
                                        }
                                        Log.d("placeId", placeId)
                                    })
                            }
                            }
                        }
                        }
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
                            onClick = {
                                if (title.isNotBlank() && introduction.isNotBlank())
                                {
                                    if (selectedDateString.isNotBlank())
                                    {
                                        if (!LocalDate.parse(selectedDateString).isBefore(currentDate))
                                        {
                                            writeToFirebase(
                                                userId,
                                                title,
                                                introduction,
                                                latitude,
                                                longitude,
                                                selectedDateString,
                                                selectedTimeString,
                                                isDone
                                            )
                                            showAddEvent.value = false

                                        }
                                        else
                                        {
                                            displayDateTimePicker.value = true
                                            Toast.makeText(
                                                context,
                                                "Can not select an early date",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            Log.d(ContentValues.TAG, "AddEvents: Failed")
                                        }
                                    }
                                    else
                                    {
                                        displayDateTimePicker.value = true
                                        Toast.makeText(
                                            context,
                                            "Please input a date",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        Log.d(ContentValues.TAG, "AddEvents: Failed")
                                    }
                                }
                                else
                                {
                                    showAddEvent.value = true
                                    Toast.makeText(
                                        context,
                                        "Please input title and introduction",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    Log.d(ContentValues.TAG, "AddEvents: Failed")
                                }


                                viewModel.fetchAndGroupTodoItems()
                                    permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    permissionRequest.launch(Manifest.permission.USE_EXACT_ALARM)
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        val splitDate = selectedDateString.split("-")
                                        val splitTime = selectedTimeString.split(":")

                                        val instance = Calendar.getInstance()
                                        instance.set(splitDate[0].toInt(), splitDate[1].toInt()-1, splitDate[2].toInt(), splitTime[0].toInt(), splitTime[1].toInt() )
                                        scheduler.schedule(instance, title, introduction)

                                    }
                                showAddEvent.value = false
                                      },
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





