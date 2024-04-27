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
import com.example.myapplication.components.stepForResetPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ForgetPageOne(navController: NavHostController, login: MutableState<Boolean>)
{
    var username by remember { mutableStateOf("") }
    val regex = Regex("[a-zA-Z0-9._%+-]+@gmail\\.com")
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

        Text (text = "Reset Password page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text (text = "We will send an email to verify your account",
            style = TextStyle(fontSize = 14.sp,
                fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(16.dp))

        TextField(value = username,
            onValueChange = { username = it},
            label = {Text(text = "Email")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        Text(text = "Click this to receive email",
            style = TextStyle(fontSize = 10.sp,
                fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(16.dp).clickable {
                val emailAddress = username
                if (emailAddress.isEmpty())
                {
                    Toast.makeText(
                        context,
                        "Email can not be empty",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else if (!emailAddress.matches(regex))
                {
                    Toast.makeText(
                        context,
                        "Email address must be gmail",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else
                {
                    Firebase.auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "Email sent.")
                                Toast.makeText(
                                    context,
                                    "Email has been send",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            })

        Button(onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .offset(y = 128.dp))
        {
            Text(text = "Return to login")
        }
    }
}

//@Composable
//fun ForgetPageTwo(navController: NavHostController, login: MutableState<Boolean>)
//{
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    Column (modifier = Modifier
//        .fillMaxWidth()
//        .padding(16.dp))
//    {
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start)
//        {
//            IconButton(onClick = { navController.navigate("forgetOne") }, modifier = Modifier.padding(8.dp))
//            {
//                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//            }
//        }
//        Text (text = "Reset Password page",
//            style = TextStyle(fontSize = 24.sp,
//                fontWeight = FontWeight.Bold),
//            modifier = Modifier.padding(16.dp))
//
//        stepForResetPassword(step = 2)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        TextField(value = password,
//            onValueChange = { password = it},
//            label = {Text(text = "Enter new Password")},
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp))
//
//        TextField(value = confirmPassword,
//            onValueChange = {confirmPassword = it},
//            label = { Text(text = "Confirm Password")},
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp))
//
//        Button(onClick = { navController.navigate("forgetThree")  },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .offset(y = 128.dp))
//        {
//            Text(text = "NEXT")
//        }
//    }
//}
//
//@Composable
//fun ForgetPageThree(navController: NavHostController, login: MutableState<Boolean>)
//{
//    Column (modifier = Modifier
//        .fillMaxWidth()
//        .padding(16.dp))
//    {
//        Text (text = "Reset Password page",
//            style = TextStyle(fontSize = 24.sp,
//                fontWeight = FontWeight.Bold),
//            modifier = Modifier.padding(16.dp))
//
//        stepForResetPassword(step = 3)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text (text = "Password Successfully Reset",
//            style = TextStyle(fontSize = 24.sp,
//                fontWeight = FontWeight.Bold),
//            modifier = Modifier.padding(16.dp))
//
//        Button(onClick = { navController.navigate("login") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .offset(y = 128.dp))
//        {
//            Text(text = "RETURN")
//        }
//    }
//}