package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MESSAGE_REMINDER_NOT_FOUND
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainDispatcherRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import org.robolectric.android.internal.AndroidTestEnvironment

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val reminder1 = ReminderDataItem("title1", "description1", "location1", 25.0, 38.0, "1")
    private val reminder2 = ReminderDataItem("title2", "description2", "location2", 26.0, 39.0, "2")
    private val invalidReminder = ReminderDataItem(null, null, null, 27.0, 40.0, "3")

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun cleanUpData() {
        stopKoin()
    }

    @Test
    fun saveThenGetReminder_validData_matchData() = runTest {
        viewModel.validateAndSaveReminder(reminder1)
        val result = fakeDataSource.getReminder("1") as Result.Success

        assertThat(result.data.title, `is`(reminder1.title))
        assertThat(result.data.description, `is`(reminder1.description))
        assertThat(result.data.location, `is`(reminder1.location))
        assertThat(result.data.latitude, `is`(reminder1.latitude))
        assertThat(result.data.longitude, `is`(reminder1.longitude))
        assertThat(result.data.id, `is`(reminder1.id))
    }

    @Test
    fun saveThenGetReminder_validData_notFound() = runTest {
        fakeDataSource.deleteAllReminders()
        viewModel.validateAndSaveReminder(reminder1) // save reminder 1
        val reminderResult = fakeDataSource.getReminder(reminder2.id) as Result.Error // then try to get reminder 2

        assertThat(reminderResult.message, `is`(MESSAGE_REMINDER_NOT_FOUND))
    }

    @Test
    fun saveReminderAndCheckLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel.validateAndSaveReminder(reminder1)
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        advanceUntilIdle()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun validateData_invalidData_shouldFalse() = runTest {
        assertThat(viewModel.validateEnteredData(invalidReminder), `is`(false))
    }

    @Test
    fun validateData_validData_shouldTrue() = runTest {
        assertThat(viewModel.validateEnteredData(reminder1), `is`(true))
    }

    @Test
    fun saveReminderToastMessage_validReminder_shouldSaved() = runTest {
        viewModel.saveReminder(reminder1)
        val value = viewModel.showToast.getOrAwaitValue()
        assertThat(
            value,
            `is`(
                ApplicationProvider.getApplicationContext<Context>().getString(R.string.reminder_saved)
            )
        )
    }

}