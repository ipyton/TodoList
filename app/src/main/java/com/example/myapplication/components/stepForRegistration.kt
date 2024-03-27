package com.example.myapplication.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.Stepper

@Composable
fun stepForRegistration(step: Int) {
    val numberStep = 3
    var currentStep by rememberSaveable { mutableStateOf(step) }
    val titleList= arrayListOf("Step 1","Step 2","Step 3")

    Stepper(
        numberOfSteps = numberStep,
        currentStep = currentStep,
        stepDescriptionList = titleList
    )
}
