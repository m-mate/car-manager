package com.example.frontend.ui.user

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.frontend.RetrofitClient
import com.example.frontend.model.User
import com.example.frontend.CarApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/*
@Composable
fun UserScreen(navController: NavHostController, viewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val user = viewModel.user
    val allUsers = viewModel.allUsers
    var isAdmin by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    // User details
    var usernameState by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var currentUserId by remember { mutableStateOf(1) }

    // Fetch user when the screen loads
    LaunchedEffect(Unit) {
        user = fetchUser(context)
        user?.let {
            currentUserId = it.id!!
            usernameState = it.username
            emailState = it.email
            isAdmin = it.role == "ROLE_ADMIN" // Check if the user is an admin
            if (isAdmin) {
                allUsers.clear()
                allUsers.addAll( fetchAllUsers(context) )
            }
        } ?: run {
            errorMessage = "Failed to load user data."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Details", style = MaterialTheme.typography.h6)

        // Display error if any
        errorMessage.takeIf { it.isNotEmpty() }?.let {
            Text(text = it, color = MaterialTheme.colors.error)
        }

        // Username (Non-editable)
        OutlinedTextField(
            value = usernameState,
            onValueChange = { usernameState = it },
            label = { Text("Username") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Email Input
        OutlinedTextField(
            value = emailState,
            onValueChange = { emailState = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save Button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    updateUser(context, password, emailState)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                Text("Save Changes")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete your profile? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            deleteUser(
                                context,
                                userList = allUsers,
                                userId = currentUserId,
                                currentUserId = currentUserId,
                                navController = navController
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                Text("Delete Profile", color = Color.White)
            }
        }

        if (isAdmin) {
            Text(text = "All Users", style = MaterialTheme.typography.h6, modifier = Modifier.padding(top = 16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allUsers) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Row layout to position the user details and the buttons (delete, change role)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween, // Spacing between the elements
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User details wrapped in Column and taking up space
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Username: ${user.username}")
                                    Text(text = "Email: ${user.email}")
                                    Text(text = "Role: ${user.role}")
                                }

                                // Row with the two buttons: Delete and Change Role
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between the buttons
                                ) {
                                    // Delete Button
                                    IconButton(onClick = { user.id?.let {
                                        deleteUser(context,allUsers ,
                                            it, currentUserId, navController)
                                    } }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete User",
                                            tint = MaterialTheme.colors.error
                                        )
                                    }

                                    // Change Role Button
                                    IconButton(onClick = { user.id?.let {
                                        changeRole(context, allUsers ,
                                            it
                                        )
                                    } }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit, // Use an appropriate icon here
                                            contentDescription = "Change Role",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

suspend fun fetchAllUsers(context: Context): MutableList<User> {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty()) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
        return mutableListOf()
    }

    return try {
        val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)

        val response = withContext(Dispatchers.IO) {
            apiService.getAllUsers().execute()
        }

        if (response.isSuccessful) {
            response.body()?.filterNot { it.username == username }?.toMutableList() ?: mutableListOf()
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to fetch users. Error: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
            mutableListOf()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        mutableListOf()
    }
}


private fun deleteUser(context: Context, userList:MutableList<User>, userId: Int, currentUserId: Int, navController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)

    if (token.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
    apiService.deleteUser(userId).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                if(userId == currentUserId){
                    navController.navigate("login")
                }else{
                    userList.removeAll { it.id == userId }
                }
                Toast.makeText(context, "Car deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete car", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
    }

private fun changeRole(context: Context, userList: MutableList<User>, userId: Int) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)

    if (token.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
    apiService.changeUserRole(userId).enqueue(object : Callback<User> {
        override fun onResponse(call: Call<User>, response: Response<User>) {
            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    // Find and update the user in the list
                    val index = userList.indexOfFirst { it.id == userId }
                    if (index != -1) {
                        userList[index] = updatedUser
                    }
                }
                Toast.makeText(context, "User role changed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to change user role: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}



suspend fun fetchUser(context: Context): User? {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)

    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
        return null
    }

    return try {
        val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)
        val response: Response<User> = withContext(Dispatchers.IO) {
            apiService.getUser(username).execute()
        }

        if (response.isSuccessful) {
            response.body()
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "User not found or server error.", Toast.LENGTH_SHORT).show()
            }
            null
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null
    }
}



private fun updateUser(context: Context,  password: String, email: String) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)
    val username = sharedPreferences.getString("username", null)
    if (token.isNullOrEmpty() || username.isNullOrEmpty()) {
        Toast.makeText(context, "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
        return
    }

    val updatedUser = User(username, password, email)

    try {

        val apiService = RetrofitClient.create(context, token).create(CarApiService::class.java)

        apiService.updateUser(username, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // Handle success: If the update is successful, show a success message
                    Toast.makeText(context, "User details updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure: Show error message if the update fails
                    Toast.makeText(context, "Failed to update user details. ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure: Show error message if the request fails
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()

    }
}
*/
@Composable
fun UserScreen(navController: NavHostController, viewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }


    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""
        viewModel.fetchUser()


    }

    LaunchedEffect(user) {
        username = user?.username ?: ""
        email = user?.email ?: ""  // ✅ Ensure `email` updates when `user` changes
        user?.let{
            if (it.role == "ROLE_ADMIN"){
                viewModel.fetchAllUsers()
            } }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Details", style = MaterialTheme.typography.h6)

        user?.let {
            OutlinedTextField(value = it.username, onValueChange = {}, label = { Text("Username") }, enabled = false)
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        viewModel.updateUser(username, password, email)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete Profile", color = Color.White)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete your profile?") },
                confirmButton = {
                    Button(
                        onClick = {
                            user?.id?.let { viewModel.deleteUser(it) }
                            navController.navigate("login")
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancel") } }
            )
        }

        LazyColumn {
            items(allUsers.filter { it.username != username }) { user ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = 6.dp) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Username: ${user.username}")
                        Text(text = "Email: ${user.email}")
                        Text(text = "Role: ${user.role}")

                        Row {
                            IconButton(onClick = { showDialog = true }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                            IconButton(onClick = { user.id?.let { viewModel.changeRole(it) } }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Change Role", tint = Color.Blue)
                            }
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text("Confirm Deletion") },
                                    text = { Text("Are you sure you want to delete this profile?") },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                user?.id?.let { viewModel.deleteUser(it) }

                                            },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                                        ) {
                                            Text("Delete", color = Color.White)
                                        }
                                    },
                                    dismissButton = { Button(onClick = { showDialog = false }) { Text("Cancel") } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
