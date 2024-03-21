package com.example.myapplication.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.Stepper

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
