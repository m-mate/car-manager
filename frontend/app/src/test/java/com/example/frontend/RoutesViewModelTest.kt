package com.example.frontend

import com.example.frontend.ui.routes.RoutesViewModel


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.frontend.model.Route
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RoutesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // For LiveData & StateFlow updates

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var viewModel: RoutesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
            .thenReturn(sharedPreferences)

        viewModel = RoutesViewModel(application, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockRoutes(): List<Route> {
        return listOf(
            Route(1, 60, 100, 6.5, 10.0, "2024-03-01T12:00:00", "2024-03-01T12:30:00", 101, 201),
            Route(2, 70, 200, 7.0, 20.0, "2024-03-02T15:00:00", "2024-03-02T16:00:00", 101, 201)
        )
    }

    @Test
    fun `fetchRoutes - success updates routes`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val mockRoutes = mockRoutes()
        `when`(apiService.getAllRoutes(anyString(), anyInt(), anyString()))
            .thenReturn(mockRoutes)

        // Act
        viewModel.fetchRoutes(101)
        advanceUntilIdle()

        // Assert
        assertEquals(mockRoutes, viewModel.routes.first())
    }

    @Test
    fun `fetchRoutes - user not logged in sets error`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn(null)
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.fetchRoutes(101)
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.first())
    }

    @Test
    fun `fetchRoutes - API error sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        `when`(apiService.getAllRoutes(anyString(), anyInt(), anyString()))
            .thenThrow(RuntimeException("API failure"))

        // Act
        viewModel.fetchRoutes(101)
        advanceUntilIdle()

        // Assert
        assertEquals("Error fetching routes: API failure", viewModel.errorMessage.first())
    }

    @Test
    fun `deleteRoute - success updates routes`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val mockRoutes = mockRoutes()
        viewModel._routes.value = mockRoutes

        // Mock API response
        `when`(apiService.deleteRoute(anyInt(), anyString()))
            .thenReturn(Response.success(null))


        // Act
        viewModel.deleteRoute(1)
        advanceUntilIdle()

        // Assert (route with id=1 should be removed)
        assertEquals(mockRoutes.filterNot { it.id == 1 }, viewModel.routes.first())
    }

    @Test
    fun `deleteRoute - user not logged in sets error`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.deleteRoute(1)
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.first())
    }

    @Test
    fun `deleteRoute - API error sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        `when`(apiService.deleteRoute(anyInt(), anyString()))
            .thenThrow(RuntimeException("Delete failed"))

        // Act
        viewModel.deleteRoute(1)
        advanceUntilIdle()

        // Assert
        assertEquals("Error deleting route: Delete failed", viewModel.errorMessage.first())
    }

    @Test
    fun `refreshRoutes - success triggers fetchRoutes`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")

        `when`(apiService.refreshRoutes(anyString(), anyInt(), anyString()))
            .thenReturn(Response.success(Unit))

        val mockRoutes = mockRoutes()
        `when`(apiService.getAllRoutes(anyString(), anyInt(), anyString()))
            .thenReturn(mockRoutes)

        // Act
        viewModel.refreshRoutes("testUser", 101)
        advanceUntilIdle()

        // Assert
        assertEquals(mockRoutes, viewModel.routes.first())
    }

    @Test
    fun `refreshRoutes - user not logged in sets error`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.refreshRoutes("testUser", 101)
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.first())
    }

    @Test
    fun `refreshRoutes - API error sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")
        lenient().`when`(sharedPreferences.getString("username", null)).thenReturn("testUser")

        // Ensure the API call actually happens before throwing
        `when`(apiService.refreshRoutes(anyString(), anyInt(), anyString()))
            .thenThrow(RuntimeException("Refresh failed"))

        // Act
        viewModel.refreshRoutes("testUser", 101)
        advanceUntilIdle()

        // Verify that refreshRoutes was actually called
        verify(apiService).refreshRoutes("testUser", 101, "Bearer testToken") // ✅ Ensure it runs

        // Assert
        assertEquals("Error refreshing routes: Refresh failed", viewModel.errorMessage.first())
    }
}