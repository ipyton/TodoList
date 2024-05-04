package com.example.myapplication.Pages

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.entities.EventEntity
import com.example.myapplication.viewmodel.TodoListViewModel
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.cameraPosition



@Composable
fun Location(viewModel: TodoListViewModel = viewModel()) {
    val singapore = LatLng(1.35, 103.87)
    val todayItems = viewModel.todayTodoItems.collectAsState()
    val scheduledItems = viewModel.scheduledTodoItems.collectAsState()
    val list = todayItems.value + scheduledItems.value
    val locationRequest = com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000)
    var currentLat = remember{
        mutableDoubleStateOf(-1.0)
    }
    var currentLng = remember{
        mutableDoubleStateOf(-1.0)
    }

    var cameraPositionState = rememberCameraPositionState {
         position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }


    val fineLocation = remember {
    mutableStateOf(false)
    }
    val coarseLocation = remember {
        mutableStateOf(false)
    }

    val request = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
            permissions->
        run {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    fineLocation.value = true
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    coarseLocation.value = true
                }

                else -> {
                    // No location access granted.
                    println("No permission was granted.");
                }
            }

        }
    }
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        LocalContext.current
    )

//    fusedLocationProviderClient.
//    fusedLocationProviderClient.
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location : Location? ->
        // Got last known location. In some rare situations this can be null.
        if (location != null) {

            currentLat.doubleValue = location.latitude
            currentLng.doubleValue = location.longitude
            cameraPositionState.move(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
        } else {
            Log.d("location", "did not get the user location")
        }
    }

    if (ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        SideEffect {
            request.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    } else {
        if (ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fineLocation.value = true

        }
        if(ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                coarseLocation.value = true
        }

    }

    if (!fineLocation.value && !coarseLocation.value) {
        println("not using location")
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState

        ) {
            list.forEach {
                run{
                    Marker (
                        state = MarkerState(position = LatLng(it.latitude,it.longitude)),
                        title = it.title,
                        snippet = it.introduction
                    )
                }
            }
        }
    }
    else{
        println("using location")
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
            ,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)

        ) {
            todayItems.value.forEach {
                run{
                    Marker (
                        state = MarkerState(position = LatLng(it.latitude,it.longitude)),
                        title = it.title + it.date + it.time,
                        snippet = it.introduction
                    )
                }
            }
        }
    }
}


