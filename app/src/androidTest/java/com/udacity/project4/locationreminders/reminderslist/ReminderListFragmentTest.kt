package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: KoinTest {

    private val reminder1 = ReminderDTO("title1", "description1", "location1", -34.0, 151.0, "1")
    private val mockNavController = mock(NavController::class.java)
    private lateinit var reminderDataSource: ReminderDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var appContext: Application

    @Before
    fun setup() {
        stopKoin()
        appContext = getApplicationContext()
        val module = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }

        }
        startKoin {
            modules(listOf(module))
        }
        reminderDataSource = get()
        runBlocking {
            reminderDataSource.deleteAllReminders()
        }
        remindersListViewModel = RemindersListViewModel(getApplicationContext(), reminderDataSource)
    }

    @Test
    fun noDataDisplayed() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(ViewMatchers.withText("No Data")).check(matches(ViewMatchers.isDisplayed()))
    }

    // test the navigation of the fragments.
    @Test
    fun navigateToSaveReminderOnClick() {
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        fragmentScenario.onFragment { Navigation.setViewNavController(it.view!!, mockNavController) }
        onView(ViewMatchers.withId(R.id.addReminderFAB)).perform(ViewActions.click())
        Mockito.verify(mockNavController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }


    // test the displayed data on the UI.
    @Test
    fun reminderItemDisplayed() {
        runBlocking {
            reminderDataSource.saveReminder(reminder1)
        }
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(ViewMatchers.withText(reminder1.title)).check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminder1.description)).check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminder1.location)).check(matches(ViewMatchers.isDisplayed()))
    }
}