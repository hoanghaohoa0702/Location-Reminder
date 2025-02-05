package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MESSAGE_FETCHING_REMINDER_LIST_FAILED
import com.udacity.project4.locationreminders.MainDispatcherRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private val reminder1 = ReminderDTO("title1", "description1", "location1", -34.0, 151.0, "1")
    private val reminder2 = ReminderDTO("title2", "description2", "location2", -35.0, 152.0, "2")
    private val reminder3 = ReminderDTO("title3", "description3", "location3", -36.0, 153.0, "3")

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun loadReminders_hasError_shouldReturnError() = runTest {
        fakeDataSource.setError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`(MESSAGE_FETCHING_REMINDER_LIST_FAILED))
    }

    @Test
    fun showNoData_reminderListIsEmpty_shouldReturnTrue() = runTest {
        fakeDataSource.deleteAllReminders()
        viewModel.loadReminders()

        assertThat(viewModel.remindersList.getOrAwaitValue().isEmpty(), `is` (true))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is` (true))
    }

    @Test
    fun checkLoading() = runTest {

        Dispatchers.setMain(StandardTestDispatcher())
        fakeDataSource.deleteAllReminders()
        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)

        viewModel.loadReminders()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        advanceUntilIdle()

        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

}