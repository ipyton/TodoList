package com.example.myapplication.Pages

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.util.GoogleAuthUIClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

var userEmail:String = ""
var userId :String = ""

@Composable
fun Login(
    navController: NavHostController,
    login: MutableState<Boolean>,
    googleAuthUiClient: GoogleAuthUIClient,

    ) {
    var name by remember { mutableStateOf ("") }
    var surname by remember { mutableStateOf ("") }
    var selectorOpened = remember {
        mutableStateOf(false)
    }
    var coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Column( modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,) {
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Text(text = "Login",
                style = MaterialTheme.typography.labelLarge)
        }
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("User Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )}
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Button(onClick = {
                if (name.isEmpty() || surname.isEmpty())
                {
                    Toast.makeText(
                        context,
                        "Email and password can not be empty.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else
                {
                    Firebase.auth.signInWithEmailAndPassword(name, surname)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                Log.d(TAG, "signInWithEmail:success")
                                val user = Firebase.auth.currentUser
                                userId = Firebase.auth.currentUser?.uid ?:"defaultUserId"
                                val email = user?.email
                                if (email != null)
                                {
                                    userEmail = email

                                }
                                Log.d(userEmail, "userEmail:${userEmail}")
                                login.value = true
                                user?.email?.let { userEmail ->
                                    Firebase.firestore.collection("users")
                                        .document(user.uid)
                                        .set(mapOf("email" to userEmail))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully written!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error writing document", e)
                                        }
                                }
                            }
                            else
                            {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    context,
                                    "Password does not match email.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }

                        }
                }

            }) {
                Text("Login")
            }
            Button(onClick = { navController.navigate("forgetOne") }) {
                Text("Forget")
            }
        }
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Button(onClick = { navController.navigate("registrationOne")
                Log.d("MainActivity","world")}) {
            Text("Registration")
        }
        }
        Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == RESULT_OK) {
                        coroutineScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                        }
                    }
                }
            )
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        val result = googleAuthUiClient.signIn()
                        val build = IntentSenderRequest.Builder(result ?: return@launch).build()
                        launcher.launch(build)
                        Log.d("result", result.toString())
                    }
                    Log.d("click", "click")
                },
                modifier = Modifier
                    .size(width = 210.dp, height = 60.dp)
                    .border(0.dp, Color.Transparent)
                    .padding(0.dp),
                border = BorderStroke(width = 0.dp, Color.White),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.signup),
                    contentDescription = "sign in with google",
                    modifier = Modifier
                        .size(200.dp)
                        .border(0.dp, Transparent)
                        .padding(0.dp),
                )
            }
        }


    }
}

fun handleSignIn(result: Any) {

}
