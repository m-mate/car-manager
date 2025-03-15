package com.example.frontend

import android.app.Application
import androidx.navigation.NavHostController
import com.example.frontend.model.User
import com.example.frontend.ui.login.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var navController: NavHostController

    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = RegisterViewModel(application, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to avoid affecting other tests
    }

    @Test
    fun `registerUser with empty inputs should set Error state`() = runTest {
        viewModel.registerUser("", "", "", navController)
        assertEquals("Invalid inputs!", viewModel.registrationStatus.value)
    }

    @Test
    fun `registerUser with invalid email should set Error state`() = runTest {
        viewModel.registerUser("testUser", "invalidEmail", "password123", navController)
        assertEquals("Invalid inputs!", viewModel.registrationStatus.value)
    }

    @Test
    fun `registerUser with valid inputs - API success`() = runTest {
        // Arrange
        val successResponse = Response.success(User("testUser", "password123", "test@example.com"))
        whenever(apiService.registerUser(any())).thenReturn(successResponse)

        // Act
        viewModel.registerUser("testUser", "test@example.com", "password123", navController)
        advanceUntilIdle()

        // Assert
        assertEquals("Success", viewModel.registrationStatus.value)
    }

    @Test
    fun `registerUser with valid inputs - API failure`() = runTest {
        // Arrange
        val errorResponse = Response.error<User>(500, okhttp3.ResponseBody.create(null, ""))
        whenever(apiService.registerUser(any())).thenReturn(errorResponse)

        // Act
        viewModel.registerUser("testUser", "test@example.com", "password123", navController)
        advanceUntilIdle()

        // Assert
        assert(viewModel.registrationStatus.value!!.contains("Failed"))
    }

    @Test
    fun `registerUser with network error should set Error state`() = runTest {
        // Arrange
        whenever(apiService.registerUser(any())).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.registerUser("testUser", "test@example.com", "password123", navController)
        advanceUntilIdle()

        // Assert
        assert(viewModel.registrationStatus.value!!.contains("Error"))
    }
}
