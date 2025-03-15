package com.example.frontend.ui.routes



import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.RetrofitClient
import com.example.frontend.CarApiService
import com.example.frontend.model.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun RouteItem(index: Int, route: Route, navController: NavController, onDelete: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Route") },
            text = { Text("Are you sure you want to delete this route?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(route.id)
                    showDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("routeDetails/${route.id}") },
        shape = RoundedCornerShape(8.dp),
        elevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Route #${index + 1}", fontSize = 18.sp, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text( text = "Start: ${formatDateTime(route.startTime)}", fontSize = 14.sp, style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "End: ${formatDateTime(route.finishTime)}", fontSize = 14.sp, style = MaterialTheme.typography.body2)
            }

            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Route", tint = MaterialTheme.colors.error)
            }
        }
    }
}



@Composable
fun RoutesScreen(navController: NavController, carId: Int, viewModel: RoutesViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    val routes by viewModel.routes.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(carId) {
        if (!username.isNullOrEmpty()) {
            viewModel.fetchRoutes(carId)
        } else {
            Toast.makeText(context, "Please log in again.", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primary,
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Routes",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onPrimary
                )

                Button(
                    onClick = { username?.let { viewModel.refreshRoutes(it, carId) } },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh Routes", tint = MaterialTheme.colors.onSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(routes) {index, route ->
                RouteItem(
                    index = index,
                    route = route,
                    navController = navController,
                    onDelete = { viewModel.deleteRoute(it) }
                )
            }
        }

        if (routes.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            Text("No routes available", fontSize = 18.sp, style = MaterialTheme.typography.body1)
        }

        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}

fun formatDateTime(startTime: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // Adjust as per input
    val outputFormat = SimpleDateFormat("yyyy.MM.dd:HH.mm", Locale.getDefault())
    return try {
        val date = inputFormat.parse(startTime)
        date?.let { outputFormat.format(it) } ?: "Invalid date"
    } catch (e: Exception) {
        "Invalid date"
    }
}