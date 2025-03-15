package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.frontend.model.Car
import com.example.frontend.ui.cars.CarListViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CarListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Allows LiveData to be updated immediately

    private lateinit var viewModel: CarListViewModel

    @Mock
    private lateinit var apiService: CarApiService

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)

        viewModel = CarListViewModel(context, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchCarsForUser - success response updates carList`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val mockCarList = listOf(Car(1,"1asd123" ,"Tesla Model S"), Car(2, "31sf32fs2", "BMW i8"))
        `when`(apiService.getCarsByUser(anyString(), anyString())).thenReturn(Response.success(mockCarList))

        // Act
        viewModel.fetchCarsForUser()
        advanceUntilIdle()

        // Assert
        assertEquals(mockCarList, viewModel.carList.value)
    }

    @Test
    fun `fetchCarsForUser - user not logged in returns error`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn(null)
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn(null)

        // Act
        viewModel.fetchCarsForUser()
        advanceUntilIdle()

        // Assert
        assertEquals("User not logged in. Please log in again.", viewModel.errorMessage.value)
    }

    @Test
    fun `fetchCarsForUser - API failure sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("username", null)).thenReturn("testUser")
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        `when`(apiService.getCarsByUser(anyString(), anyString())).thenReturn(Response.error(500, ResponseBody.create(null, "Server error")))

        // Act
        viewModel.fetchCarsForUser()
        advanceUntilIdle()

        // Assert
        assertEquals("Failed to fetch cars: Response.error()", viewModel.errorMessage.value)
        assertTrue(viewModel.carList.value.isEmpty())
    }

    @Test
    fun `deleteCar - success removes car from list`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        val initialCars = listOf(Car(1,"1asd123" ,"Tesla Model S"), Car(2, "31sf32fs2", "BMW"))
        viewModel = CarListViewModel(context, apiService)
        viewModel._carList.value = initialCars

        // Act
        viewModel.deleteCar(1)
        advanceUntilIdle()

        // Assert
        assertEquals(listOf(Car(2, "31sf32fs2","BMW")), viewModel.carList.value)
    }

    @Test
    fun `deleteCar - API failure sets error message`() = runTest {
        // Arrange
        `when`(sharedPreferences.getString("jwt_token", null)).thenReturn("testToken")

        `when`(apiService.deleteCar(anyInt(), anyString())).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.deleteCar(1)
        advanceUntilIdle()

        // Assert
        assertEquals("Error deleting car: Network error", viewModel.errorMessage.value)
    }
}