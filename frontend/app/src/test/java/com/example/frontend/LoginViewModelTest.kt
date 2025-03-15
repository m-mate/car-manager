package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavHostController
import com.example.frontend.model.User
import com.example.frontend.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onErrorResume
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var apiService: CarApiService



    private lateinit var viewModel: LoginViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)

        `when`(application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)

        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(sharedPreferencesEditor.putString(anyString(), anyString())).thenReturn(sharedPreferencesEditor)
        `when`(sharedPreferencesEditor.apply()).then {}

        viewModel = LoginViewModel(application, apiService)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to avoid affecting other tests
    }

    @Test
    fun `loginUser with empty inputs should set Error state`() = runTest {
        viewModel.login("", "")
        assertEquals(LoginViewModel.LoginState.Error("Invalid inputs!"), viewModel.loginState.value)
    }

    @Test
    fun `loginUser with invalid email should set Error state`() = runTest {
        viewModel.login("invalidEmail", "password123")
        assertEquals(LoginViewModel.LoginState.Idle, viewModel.loginState.value)
    }

    @Test
    fun `loginUser with valid inputs - API success`() = runTest {
        // Arrange
        val successResponse = Response.success("Login successful")
        whenever(apiService.loginUser(any())).thenReturn(successResponse)

        // Act
        viewModel.login("test@example.com", "password123")
        advanceUntilIdle()

        // Assert
        assertEquals(LoginViewModel.LoginState.Success, viewModel.loginState.value)
    }

    @Test
    fun `loginUser with valid inputs - API failure`() = runTest {
        // Arrange
        val errorResponse = Response.error<String>(401, okhttp3.ResponseBody.create(null, "Unauthorized"))
        whenever(apiService.loginUser(any())).thenReturn(errorResponse)


        // Act
        viewModel.login("test@example.com", "password123")
        advanceUntilIdle()

        // Assert
        assertEquals(LoginViewModel.LoginState.Error("Invalid credentials or server error"), viewModel.loginState.value)
    }

    @Test
    fun `loginUser with network error should set Error state`() = runTest {
        // Arrange
        whenever(apiService.loginUser(any())).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.login("test@example.com", "password123")
        advanceUntilIdle()

        // Assert
        assertEquals(LoginViewModel.LoginState.Error("Network error"), viewModel.loginState.value)
    }
}
