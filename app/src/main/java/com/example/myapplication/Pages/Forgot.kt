package com.example.myapplication.Pages

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

@Composable
fun ForgetPageOne(navController: NavHostController, login: MutableState<Boolean>)
{
    var username by remember { mutableStateOf("") }
    var checkCode by remember { mutableStateOf("") }
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

        stepForResetPassword(step = 1)

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
            TextField(
                value = checkCode,
                onValueChange = { checkCode = it },
                label = { Text(text = "Check Code") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp))
            Text(
                text = "Click to receive code",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        Toast
                            .makeText(
                                context, "Code has been send", Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }
        Button(onClick = { navController.navigate("forgetTwo") },
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
fun ForgetPageTwo(navController: NavHostController, login: MutableState<Boolean>)
{
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start)
        {
            IconButton(onClick = { navController.navigate("forgetOne") }, modifier = Modifier.padding(8.dp))
            {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
        Text (text = "Reset Password page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        stepForResetPassword(step = 2)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = password,
            onValueChange = { password = it},
            label = {Text(text = "Enter new Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        TextField(value = confirmPassword,
            onValueChange = {confirmPassword = it},
            label = { Text(text = "Confirm Password")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp))

        Button(onClick = { navController.navigate("forgetThree")  },
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
fun ForgetPageThree(navController: NavHostController, login: MutableState<Boolean>)
{
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp))
    {
        Text (text = "Reset Password page",
            style = TextStyle(fontSize = 24.sp,
                fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp))

        stepForResetPassword(step = 3)

        Spacer(modifier = Modifier.height(16.dp))

        Text (text = "Password Successfully Reset",
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