package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.frontend.CarApiService
import com.example.frontend.model.Car
import com.example.frontend.ui.cars.AddCarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import retrofit2.Response

@ExperimentalCoroutinesApi
class AddCarViewModelTest {

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: AddCarViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        `when`(application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        viewModel = AddCarViewModel(application, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher to avoid affecting other tests
    }

    @Test
    fun `saveCar with empty carModel should set Error state`() {
        viewModel.saveCar("")

        assertEquals(AddCarViewModel.CarUiState.Error("Car model cannot be empty"), viewModel.uiState.value)
    }

    @Test
    fun `saveCar with valid carModel should set Success state`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")
        `when`(apiService.saveCar(anyString(), anyOrNull(), anyString()))
            .thenReturn(Response.success(null))

        // Act
        viewModel.saveCar("TestModel")
        advanceUntilIdle()

        // Assert
        assertEquals(AddCarViewModel.CarUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `saveCar with invalid token should set Error state`() = runBlockingTest {
        `when`(sharedPreferences.getString("username", null)).thenReturn(null)
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        viewModel.saveCar("TestModel")

        assertEquals(AddCarViewModel.CarUiState.Error("User not logged in. Please log in again."), viewModel.uiState.value)
    }
}