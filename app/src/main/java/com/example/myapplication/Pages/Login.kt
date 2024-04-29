package com.example.myapplication.Pages

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.credentials.BeginGetCredentialRequest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.util.GoogleAuthUIClient
import com.example.myapplication.viewmodel.SignInViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch




fun handleSignIn(result: GetCredentialResponse, auth: FirebaseAuth): Unit {

    // Handle the successfully returned credential.
    val credential = result.credential
    println("-----------------------------------------------------")

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
                    addOnCompleteListener {   auth.currentUser?.uid }.
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
    val auth = Firebase.auth


    var selectorOpened = remember {
        mutableStateOf(false)
    }
//    val viewModel: SignInViewModel by viewModels()

    //val googleAuthUiClient by lazy{ GoogleAuthUIClient(context= LocalContext.current, Identity.getSignInClient(applicationContext)) }

    var coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(LocalContext.current)

    val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
        // Contains the request in JSON format. Uses the standard WebAuthn
        // web JSON spec.
        requestJson = "{\n" +
                "  \"challenge\": \"abc123\",\n" +
                "  \"rp\": {\n" +
                "    \"name\": \"Credential Manager example\",\n" +
                "    \"id\": \"credential-manager-test.example.com\"\n" +
                "  },\n" +
                "  \"user\": {\n" +
                "    \"id\": \"def456\",\n" +
                "    \"name\": \"helloandroid@gmail.com\",\n" +
                "    \"displayName\": \"helloandroid@gmail.com\"\n" +
                "  },\n" +
                "  \"pubKeyCredParams\": [\n" +
                "    {\n" +
                "      \"type\": \"public-key\",\n" +
                "      \"alg\": -7\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"public-key\",\n" +
                "      \"alg\": -257\n" +
                "    }\n" +
                "  ],\n" +
                "  \"timeout\": 1800000,\n" +
                "  \"attestation\": \"none\",\n" +
                "  \"excludeCredentials\": [\n" +
                "    {\"id\": \"ghi789\", \"type\": \"public-key\"},\n" +
                "    {\"id\": \"jkl012\", \"type\": \"public-key\"}\n" +
                "  ],\n" +
                "  \"authenticatorSelection\": {\n" +
                "    \"authenticatorAttachment\": \"platform\",\n" +
                "    \"requireResidentKey\": true,\n" +
                "    \"residentKey\": \"required\",\n" +
                "    \"userVerification\": \"required\"\n" +
                "  }\n" +
                "}",
        // Defines whether you prefer to use only immediately available
        // credentials, not hybrid credentials, to fulfill this request.
        // This value is false by default.
        //preferImmediatelyAvailableCredentials = false,
    )
    if (false) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
//            floatingActionButton = {
//                ExtendedFloatingActionButton(
//                    text = { Text("Show snackbar") },
//                    icon = { Icon(Icons.Default.Favorite, contentDescription = "") },
//                    onClick = {
//                        coroutineScope.launch {
//                            val result = snackbarHostState
//                                .showSnackbar(
//                                    message = "Snackbar",
//                                    actionLabel = "Action",
//                                    // Defaults to SnackbarDuration.Short
//                                    duration = SnackbarDuration.Indefinite
//                                )
//                            when (result) {
//                                SnackbarResult.ActionPerformed -> {
//                                    /* Handle snackbar action performed */
//                                }
//                                SnackbarResult.Dismissed -> {
//                                    /* Handle snackbar dismissed */
//                                }
//                            }
//                        }
//                    }
//                )
//            }
        )
        { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
            ) {
                Row (modifier=Modifier.fillMaxWidth().padding(top=40.dp), horizontalArrangement = Arrangement.Center){
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )}
                Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Button(onClick = { login.value = true
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
//                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//                        .setFilterByAuthorizedAccounts(false)
//                        .setServerClientId("467501865267-uigmi4qr0933hvr63at079hd748oa9n7.apps.googleusercontent.com")
//                        .build()
                            val getPasswordOption = GetPasswordOption()
                            val getGoogleIdOption = GetGoogleIdOption("467501865267-uigmi4qr0933hvr63at079hd748oa9n7.apps.googleusercontent.com")
                            //startActivityForResult(current.startActivity(), Intent(Settings.ACTION_ADD_ACCOUNT),1,ActivityOptionsCompat.makeBasic())

                            val getCredRequest = GetCredentialRequest(
                                listOf(getPasswordOption, getGoogleIdOption)
                            )
                            if (getCredRequest.credentialOptions.isEmpty()) {
                                println("empty")
                                coroutineScope.launch {
                                    println("print no credentials ")
                                    val result = snackbarHostState
                                        .showSnackbar(
                                            message = "Snackbar",
                                            actionLabel = "Action",
                                            // Defaults to SnackbarDuration.Short
                                            duration = SnackbarDuration.Short
                                        )
                                }
                            }else {
                                coroutineScope.launch {
                                    try {
                                        val result = credentialManager.getCredential(
                                            // Use an activity-based context to avoid undefined system UI
                                            // launching behavior.
                                            context = current,
                                            request = getCredRequest
                                        )
                                        handleSignIn(result, auth)
                                    } catch (e : GetCredentialException) {

                                    }


                                }
                                Log.d("click", "click")


                            }

//// Get passkey from the user's public key credential provider.
//                    val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
//                        requestJson = requestJson
//                    )

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
                SnackbarHost(hostState = snackbarHostState)

            }

        }

    }
    else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Row (modifier=Modifier.fillMaxWidth().padding(top=160.dp), horizontalArrangement = Arrangement.Center){
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )}
            Row (modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Button(onClick = { login.value = true
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
                                println(signInResult.errorMessage)
                                println(signInResult.data?.userId)
                                println(signInResult.data?.username)
                                println(signInResult.data?.profilePictureUrl)
                            }
                        }
                        else {
                            println(result.resultCode)
                        }
                    }
                )
                TextButton(
                    onClick = {
//                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//                        .setFilterByAuthorizedAccounts(false)
//                        .setServerClientId("467501865267-uigmi4qr0933hvr63at079hd748oa9n7.apps.googleusercontent.com")
//                        .build()
                        //val getPasswordOption = GetPasswordOption()
                        val getGoogleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setAutoSelectEnabled(false).setServerClientId("467501865267-im97al3s39cei2j2l17a1karb2r7jmmj.apps.googleusercontent.com").build()
                        val request: GetCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption).build()
                        coroutineScope.launch {
                        try {
                            val result = credentialManager.getCredential(
                                request = request,
                                context = current,
                            )
                            handleSignIn(result,auth)

                        } catch (e:NoCredentialException){
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
                                    intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                                    current.startActivity(intent)
                                    //startActivityForResult(current.startActivity(), Intent(Settings.ACTION_ADD_ACCOUNT),1,ActivityOptionsCompat.makeBasic())


                                }
                                SnackbarResult.Dismissed -> {
                                    /* Handle snackbar dismissed */
                                }
                            }

                        } catch (err: Exception) {
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
            Spacer(Modifier.weight(1f).fillMaxHeight().background(Color.Green))
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.End))

        }
    }


}

