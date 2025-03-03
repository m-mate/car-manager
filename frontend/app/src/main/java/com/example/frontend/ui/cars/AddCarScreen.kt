package com.example.frontend.ui.cars


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController



@Composable
fun AddCarScreen(navController: NavController, viewModel: AddCarViewModel = hiltViewModel()) {
    var carModel by remember { mutableStateOf("") }
    val uiState by viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AddCarViewModel.CarUiState.Success) {
            navController.navigate("carList") { popUpTo(0) { inclusive = true } }
        } else if (uiState is AddCarViewModel.CarUiState.Error) {
            Toast.makeText(context, (uiState as AddCarViewModel.CarUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = carModel,
            onValueChange = { carModel = it },
            label = { Text("Car Model") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.saveCar(carModel) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is AddCarViewModel.CarUiState.Loading
        ) {
            Text(if (uiState is AddCarViewModel.CarUiState.Loading) "Saving..." else "Add Car")
        }
    }
}
