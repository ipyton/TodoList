package com.example.myapplication.Pages


import android.content.ContentValues.TAG
import android.widget.Toast
import android.content.Intent
import android.graphics.Matrix
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.graphics.Color.Companion.Magenta
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.NoCredentialException
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.util.GoogleAuthUIClient
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlin.math.max


var userEmail:String = ""
var userId :String = ""



fun handleSignIn(result: GetCredentialResponse, auth: FirebaseAuth, login:MutableState<Boolean>): Unit {

    // Handle the successfully returned credential.
    val credential = result.credential
    when (credential) {
        is PublicKeyCredential -> {
            val responseJson = credential.authenticationResponseJson
            // Share responseJson i.e. a GetCredentialResponse on your server to
            // validate and  authenticate
        }
        is PasswordCredential -> {
            val username = credential.id
            val password = credential.password
            // Use id and password to send to your server to validate
            // and authenticate
        }
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Use googleIdTokenCredential and extract id to validate and
                    // authenticate on your server.
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)).
                    addOnCanceledListener { println("canceled") }.
                    addOnCompleteListener {
                        println(googleIdTokenCredential.id)
                        println(googleIdTokenCredential.idToken)
                        userEmail = googleIdTokenCredential.id
                        userId = auth.currentUser?.uid.toString()
                        login.value = true
                        com.google.firebase.Firebase.firestore.collection("users")
                            .document(googleIdTokenCredential.idToken)
                            .set(mapOf("email" to googleIdTokenCredential.id))
                    }.
                    addOnFailureListener { println(it) }
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("Login error", "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e("Login error", "Unexpected type of credential")
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Log.e("Login error", "Unexpected type of credential")
        }
    }

}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun Login(
    navController: NavHostController,
    login: MutableState<Boolean>,
    googleAuthUiClient: GoogleAuthUIClient,
    ) {
    var name by remember { mutableStateOf ("") }
    var surname by remember { mutableStateOf ("") }
    val current = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val auth = com.google.firebase.Firebase.auth
    val rainbowColors = listOf(White, Magenta,  Blue, Cyan)

    var coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val credentialManager = CredentialManager.create(LocalContext.current)
    val gradientColors = listOf(Cyan, White /*...*/)
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                setVideoPath("android.resource://${context.packageName}/${R.raw.output}")
                setOnCompletionListener { mediaPlayer ->
                    mediaPlayer.start() // Replay the video
                }
                //transformMatrixToGlobal()
                setOnPreparedListener { mediaPlayer ->
                    val videoWidth = mediaPlayer.videoWidth
                    val videoHeight = mediaPlayer.videoHeight
                    val sx = width.toFloat() / videoWidth.toFloat()
                    val sy = height.toFloat() / videoHeight.toFloat()
                    println(width)
                    println(height)
                    //mediaPlayer.setVideoScalingMode()

                    val maxScale = max(sx.toDouble(), sy.toDouble()).toFloat()
                    val matrix = Matrix()

                    matrix.preTranslate((width - videoWidth.toFloat()) / 2, (height - videoHeight.toFloat()) / 2);
                    matrix.preScale(
                        videoWidth / width.toFloat(),
                        videoHeight / height.toFloat()
                    )
                    matrix.postScale(maxScale, maxScale,
                        (width.toFloat() / 2), height.toFloat() / 2);

                    transformMatrixToGlobal(matrix)
                    postInvalidate();
                    start()

                }
            }
        })

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 240.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to Todo List!",

                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = rainbowColors
                        )
                    ), fontSize = 35.sp, fontFamily = FontFamily.Cursive
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 30.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("User Email") },
                    modifier = Modifier
                        .width(300.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                TextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .width(300.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { navController.navigate("forgetOne") },
                    modifier = Modifier.padding(end = 30.dp)
                ) {
                    Text(
                        "Forget Your Password?", style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    if (name.isEmpty() || surname.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Email and password can not be empty.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        com.google.firebase.Firebase.auth.signInWithEmailAndPassword(name, surname)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "signInWithEmail:success")
                                    val user = com.google.firebase.Firebase.auth.currentUser
                                    userId = com.google.firebase.Firebase.auth.currentUser?.uid ?: "defaultUserId"
                                    val email = user?.email
                                    if (email != null) {
                                        userEmail = email

                                    }
                                    Log.d(userEmail, "userEmail:${userEmail}")
                                    login.value = true
                                    user?.email?.let { userEmail ->
                                        com.google.firebase.Firebase.firestore.collection("users")
                                            .document(user.uid)
                                            .set(mapOf("email" to userEmail))
                                            .addOnSuccessListener {
                                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w(TAG, "Error writing document", e)
                                            }
                                    }
                                } else {
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
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                TextButton(
                    onClick = {

                        val getGoogleIdOption =
                            GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(true)
                                .setAutoSelectEnabled(false)
                                .setServerClientId("467501865267-im97al3s39cei2j2l17a1karb2r7jmmj.apps.googleusercontent.com")

                        var request: GetCredentialRequest = GetCredentialRequest.Builder().setPreferImmediatelyAvailableCredentials(true)
                            .addCredentialOption(getGoogleIdOption.build()).build()
                        coroutineScope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = current,
                                )

                                handleSignIn(result, auth,login)

                            } catch (e: NoCredentialException) {
                                try {
                                    request = GetCredentialRequest.Builder()
                                        .addCredentialOption(getGoogleIdOption.setFilterByAuthorizedAccounts(false).build()).build()
                                    credentialManager.getCredential(
                                        request = request,
                                        context = current,
                                    )
                                } catch(e:NoCredentialException) {
                                    val barResult = snackbarHostState
                                        .showSnackbar(
                                            message = "You don't have a google account, do you want to add?",
                                            actionLabel = "Add",
                                            // Defaults to SnackbarDuration.Short
                                            duration = SnackbarDuration.Short
                                        )
                                    when (barResult) {
                                        SnackbarResult.ActionPerformed -> {
                                            val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
                                            intent.putExtra(
                                                Settings.EXTRA_ACCOUNT_TYPES,
                                                arrayOf("com.google")
                                            )
                                            current.startActivity(intent)
                                        }

                                        SnackbarResult.Dismissed -> {
                                            /* Handle snackbar dismissed */
                                        }
                                    }
                                }

                            } catch (err: Exception) {
                                println(err)
                                val barResult = snackbarHostState
                                    .showSnackbar(
                                        message = "Unknown error, please ask for developer!",
                                        actionLabel = "dismiss",
                                        // Defaults to SnackbarDuration.Short
                                        duration = SnackbarDuration.Short
                                    )
                            }
                        }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = {
                    navController.navigate("registrationOne")
                }) {
                    Text(
                        "Want to create an account?",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
            Spacer(Modifier.weight(1f).fillMaxHeight())
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.End))

            }
        }




