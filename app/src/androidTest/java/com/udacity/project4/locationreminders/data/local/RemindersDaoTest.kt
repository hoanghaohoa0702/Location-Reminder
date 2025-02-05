package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

//Unit test the DAO
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private val reminder1 = ReminderDTO("title1", "description1", "location1", -34.0, 151.0, "1")
    private val reminder2 = ReminderDTO("title2", "description2", "location2", -35.0, 152.0, "2")
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
                .allowMainThreadQueries().build()
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertThenFindReminderByID() =
        runTest {
            database.reminderDao().saveReminder(reminder1)
            val foundReminder = database.reminderDao().getReminderById(reminder1.id)
            assertThat(foundReminder as ReminderDTO, notNullValue())
            assertThat(foundReminder.id, `is`(reminder1.id))
            assertThat(foundReminder.title, `is`(reminder1.title))
            assertThat(foundReminder.description, `is`(reminder1.description))
            assertThat(foundReminder.location, `is`(reminder1.location))
            assertThat(foundReminder.longitude, `is`(reminder1.longitude))
            assertThat(foundReminder.latitude, `is`(reminder1.latitude))
        }

    @Test
    fun insertRemindersAndFetchAll_2Value_true() = runTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        val reminderDTOList = database.reminderDao().getReminders()
        assertThat(reminderDTOList.size, `is`(2))
    }

    @Test
    fun deleteAllReminder_getEmpty() = runTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().deleteAllReminders()
        assertThat(database.reminderDao().getReminders().isEmpty(), `is`(true))
    }

    @Test
    fun insertThenDeleteReminder_returnNull() = runTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().deleteAllReminders()
        assertThat(database.reminderDao().getReminderById(reminder1.id), `is`(CoreMatchers.nullValue()))
    }
}