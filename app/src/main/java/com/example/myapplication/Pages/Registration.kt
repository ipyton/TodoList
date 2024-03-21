package com.example.myapplication.Pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.myapplication.Stepper

@Composable
fun registration(navController: NavHostController) {
    val numberStep = 4
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val titleList= arrayListOf("Step 1","Step 2")

    Stepper(
        numberOfSteps = numberStep,
        currentStep = currentStep,
        stepDescriptionList = titleList
    )

}