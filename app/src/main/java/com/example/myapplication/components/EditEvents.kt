package com.example.myapplication.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.example.myapplication.ApiClient
import com.example.myapplication.Pages.userId
import com.example.myapplication.entities.BusinessEntity
import com.example.myapplication.entities.TodoItem
import com.example.myapplication.util.FirebaseUtil.updateTodoItemInFirebase
import com.example.myapplication.viewmodel.TodoItemViewModel
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditEvents(
    showEditEvent: MutableState<Boolean>,
    viewModel: TodoListViewModel,
    todoItem: TodoItem
) {

    val todoItemViewModel: TodoItemViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )

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
                    // No location access granted.
                }
            }

        }
    }


    var title by remember {
        mutableStateOf(todoItem.title)
    }
    var introduction by remember {
        mutableStateOf(todoItem.introduction)
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


    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_DATE
    val formattedDate = currentDate.format(formatter)
    var selectedDateString by remember {
        mutableStateOf(formattedDate)
    }

    var selectedTimeString by remember {
        mutableStateOf("00:00")
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


    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location : Location? ->
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            Log.d("location",""+latitude + " " + longitude)
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
        Dialog(onDismissRequest = {  }) {
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
                        Log.d("enter", "1111")
                        if (ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            request.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION))


                        fusedLocationProviderClient.getLastLocation()


                        DockedSearchBar(
                            query = searchLocation,
                            onQueryChange = {
                                searchLocation = it
                                //suggestions.clear()
                                if (it.isEmpty()) {
                                    activeSuggestion = false
                                } else {
                                    if (latitude == -1.0 || longitude == -1.0) {
                                        return@DockedSearchBar
                                    }
                                    coroutineScope.launch {
                                        Log.d("request", URLEncoder.encode(searchLocation, "UTF-8"))
                                        ApiClient.apiService.autoCompelete("$latitude,$longitude",
                                            URLEncoder.encode(searchLocation, "UTF-8"), "AIzaSyBlBlflFyhquV_cbyY1HVrdz5-K8MDRTok", type="geocode").enqueue(object :
                                            Callback<ResponseBody> {
                                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
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
                                                        if (!activeSuggestion) {
                                                            suggestions.clear()
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
                                                val target = get
                                                ApiClient.apiService.getDetails(
                                                    target.locationId,
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
                                                                target.latitude =
                                                                    jsonObj.getJSONObject("result").getJSONObject("geometry")
                                                                        .getJSONObject("location")
                                                                        .getDouble("lat")
                                                                target.longitude =
                                                                    jsonObj.getJSONObject("result").getJSONObject("geometry")
                                                                        .getJSONObject("location")
                                                                        .getDouble("lng")
                                                                eventPlace.value = target
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
                                                }
                                                )
                                            }
                                            Log.d("placeId", placeId)
                                        }
                                    )
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
                            onClick = { showEditEvent.value = false },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                    todoItem.title = title
                                    todoItem.introduction = introduction
                                    todoItem.date = selectedDateString
                                    todoItem.time = selectedTimeString
                                    todoItemViewModel.insertTodoItem(todoItem)
                                    updateTodoItemInFirebase(userId, todoItem) {
                                        viewModel.fetchAndGroupTodoItems()
                                    }
                                    Log.d("todoItem.title", "todoItem.title: ${todoItem.title}")
                                    Log.d("todoItem.introduction", "todoItem.introduction: ${todoItem.introduction}")


                                showEditEvent.value = false},
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



