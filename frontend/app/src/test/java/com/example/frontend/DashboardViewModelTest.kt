package com.example.frontend
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.frontend.model.CarDataLive
import com.example.frontend.ui.dashboard.DashboardViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import okhttp3.MediaType
import okhttp3.ResponseBody

import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DashboardViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // ✅ For StateFlow updates

    private lateinit var viewModel: DashboardViewModel

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)

        viewModel = DashboardViewModel(context, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchCarData - success response updates carData`() = testScope.runTest {
        // Arrange
        `when`(sharedPreferences.getString("vin", null)).thenReturn("VIN12345")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val mockCarData = CarDataLive(1, 60.0, 3000, 5.0, 90, "2024-03-03T12:00:00", true)
        `when`(apiService.getCarStatus(anyString(), anyString())).thenReturn(Response.success(mockCarData))

        // Act
        val job = launch { viewModel.fetchCarData() }
        advanceTimeBy(1100) // Simulate 1 fetch iteration (1s delay)

        viewModel.stopFetching() // ✅ Stop the loop
        job.join() // Wait for coroutine to finish

        // Assert
        assertEquals(mockCarData, viewModel.carData.first())
    }

    @Test
    fun `fetchCarData - user not logged in returns error`() = testScope.runTest {
        // Arrange
        `when`(sharedPreferences.getString("vin", null)).thenReturn(null)
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.fetchCarData()
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.first())
    }

    @Test
    fun `fetchCarData - API failure sets error message`() = testScope.runTest {
        // Arrange
        `when`(sharedPreferences.getString("vin", null)).thenReturn("VIN12345")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val errorBody = ResponseBody.create(MediaType.parse("application/json"), "Server error")
        `when`(apiService.getCarStatus(anyString(), anyString()))
            .thenReturn(Response.error(500, errorBody))

        // Act
        val job = launch { viewModel.fetchCarData() }
        advanceTimeBy(1100) // Simulate one loop iteration
        viewModel.stopFetching() // ✅ Stop the loop
        job.join() // Wait for coroutine to finish

        // Assert
        assertEquals("Failed to fetch car data: Response.error()", viewModel.errorMessage.first())
    }
}