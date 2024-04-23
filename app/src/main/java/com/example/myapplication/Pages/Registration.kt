package com.example.myapplication.Pages

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.Stepper
import com.example.myapplication.components.stepForRegistration
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.auth.auth

@Composable
fun RegistrationPageOne(navController: NavHostController)
{
    var username by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start)
        {
            IconButton(onClick = { navController.navigate("login") }, modifier = Modifier.padding(8.dp))
            {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        Text (text = "Registration page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

//        Stepper(numberOfSteps = numberStep,
//            currentStep = currentStep,
//            stepDescriptionList = titleList,)

        stepForRegistration(step = 1)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = username,
            onValueChange = { username = it },
            label = {Text(text = "User Email")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Text(
                text = "Click to receive verification email",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        if (username.isNotEmpty())
                        {
                            val actionCodeSettings = actionCodeSettings {
                                url = "https://todolist-3cd3d.firebaseapp.com"
                                handleCodeInApp = true
                                //setIOSBundleId("com.example.ios")
                                setAndroidPackageName(
                                    "com.example.myapplication",
                                    true,
                                    "12",
                                )
                            }

                            Firebase.auth.sendSignInLinkToEmail(username, actionCodeSettings)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Email sent.")
                                    }
                                }

                            Toast
                                .makeText(
                                    context, "Email has been sent", Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                        else
                        {
                            Toast
                                .makeText(
                                    context, "E-mail can not be empty", Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
            )
        }
        Button(onClick = { navController.navigate("registrationTwo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .offset(y = 128.dp))
        {
            Text(text = "NEXT")
        }
    }
}

@Composable
fun RegistrationPageTwo(navController: NavHostController, login: MutableState<Boolean>)
{
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start)
        {
            IconButton(onClick = { navController.navigate("registrationOne") }, modifier = Modifier.padding(8.dp))
            {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        Text (text = "Registration page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))


        stepForRegistration(step = 2)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = password,
            onValueChange = { password = it},
            label = {Text(text = "Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        TextField(value = confirmPassword,
            onValueChange = {confirmPassword = it},
            label = { Text(text = "Confirm Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        Button(onClick = { navController.navigate("registrationThree") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .offset(y = 128.dp))
        {
            Text(text = "NEXT")
        }
    }
}

@Composable
fun RegistrationPageThree(navController: NavHostController, login: MutableState<Boolean>)
{
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Text (text = "Registration page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

//        Stepper(numberOfSteps = numberStep,
//            currentStep = currentStep,
//            stepDescriptionList = titleList,)

        stepForRegistration(step = 3)

        Spacer(modifier = Modifier.height(16.dp))

        Text (text = "Successful Registration",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        Button(onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .offset(y = 128.dp))
        {
            Text(text = "RETURN")
        }
    }
}

