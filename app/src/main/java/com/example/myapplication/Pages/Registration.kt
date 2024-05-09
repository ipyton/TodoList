package com.example.myapplication.Pages

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun RegistrationPageOne(navController: NavHostController)
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val regex = Regex("[a-zA-Z0-9._%+-]+@gmail\\.com")
    val regexPassword = Regex("\\S{6,}")
    val context = LocalContext.current
    var passwordVisibility by remember { mutableStateOf(false) }


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


        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(value = email,
            onValueChange = { email = it },
            label = { Text(text = "User Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        TextButton(
            onClick = { passwordVisibility = !passwordVisibility },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (passwordVisibility) "Hide password" else "Show password")
        }

        Button(onClick = {
            if (email.isEmpty() && password.isEmpty())
            {
                Toast.makeText(
                    context,
                    "Email or password can not be empty",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if (!password.equals(confirmPassword))
            {
                Toast.makeText(
                    context,
                    "The two password entries must be consistent",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if (!email.matches(regex))
            {
                Toast.makeText(
                    context,
                    "Please sign up with gmail",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if (!password.matches(regexPassword))
            {
                Toast.makeText(
                    context,
                    "The password must be six characters or more and cannot contain spaces.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else
            {
                Firebase.auth.createUserWithEmailAndPassword(email, confirmPassword)
                    .addOnCompleteListener(Activity()) { task ->
                        if (task.isSuccessful)
                        {
                            val auth = Firebase.auth.currentUser
                            auth?.sendEmailVerification()?.addOnCompleteListener()
                            {
                                    task->
                                if (task.isSuccessful)
                                {
                                    Log.d(TAG, "createUserWithEmail:success")
                                    navController.navigate("registrationTwo")
                                }
                            }

                        }
                        else
                        {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                context,
                                "Authentication failed, email has been already used",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }
                    }

            } },
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
fun RegistrationPageTwo(navController: NavHostController)
{
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Text (text = "Registration page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text (text = "Successful Registration",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        Text (text = "Welcome to TODOLIST",
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

