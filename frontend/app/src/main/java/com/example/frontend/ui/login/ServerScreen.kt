package com.example.frontend.ui.login



import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.CarApiService
import com.example.frontend.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ServerScreen(navController: NavController, viewModel: ServerViewModel = hiltViewModel()) {
    val serverIp by viewModel.serverIp.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val context = LocalContext.current

    var textFieldValue by remember { mutableStateOf(TextFieldValue(serverIp)) }

    val sharedPreferences = remember {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    LaunchedEffect(Unit) {
        sharedPreferences.edit().remove("server_address").apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Server IP Address", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text("Server IP Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveServerIp(textFieldValue.text) {
                    navController.navigate("login") {
                        popUpTo("serverScreen") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isConnecting
        ) {
            Text(if (isConnecting) "Connecting..." else "Save")
        }

        if (errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissError() },
                confirmButton = {
                    Button(onClick = { viewModel.dismissError() }) {
                        Text("OK")
                    }
                },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Error",
                            tint = MaterialTheme.colors.error,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                },
                text = {
                    Text(
                        errorMessage!!,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            )
        }
    }
}








