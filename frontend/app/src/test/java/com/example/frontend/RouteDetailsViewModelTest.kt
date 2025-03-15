package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.frontend.model.CarData
import com.example.frontend.model.Route
import com.example.frontend.model.RouteDetails
import com.example.frontend.ui.routes.RouteDetailsViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RouteDetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Allows LiveData & StateFlow updates

    private lateinit var viewModel: RouteDetailsViewModel

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)

        viewModel = RouteDetailsViewModel(context, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchRouteDetails - success updates routeDetails`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val mockRoute = Route(
            id = 1,
            avgSpeed = 60,
            distanceTraveled = 200,
            avgFuelConsumption = 5.5,
            fuelUsed = 11.0,
            startTime = "2024-03-03T12:00:00",
            finishTime = "2024-03-03T14:00:00",
            carId = 101,
            userId = 42
        )

        val mockCarData = listOf(
            CarData(1, 60.0, 3000, 5.0,  "2024-03-03T12:00:00", true),
            CarData(2, 65.0, 3200, 4.8,  "2024-03-03T12:05:00", true)
        )

        val mockRouteDetails = RouteDetails(mockRoute, mockCarData)

        doReturn(mockRouteDetails)
            .`when`(apiService)
            .getRouteDetails(anyInt(), anyString())

        // Act
        viewModel.fetchRouteDetails(1)
        advanceUntilIdle()  // Ensures the coroutine completes

        // Assert
        assertEquals(mockRouteDetails, viewModel.routeDetails.first()) // Check state update
    }

    @Test
    fun `fetchRouteDetails - missing token sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.fetchRouteDetails(1)
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.first())
    }

    @Test
    fun `fetchRouteDetails - API failure sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        `when`(apiService.getRouteDetails(anyInt(), anyString()))
            .thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.fetchRouteDetails(1)
        advanceUntilIdle()

        // Assert
        assertEquals("Error fetching route details: Network error", viewModel.errorMessage.first())
    }
}