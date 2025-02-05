package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MESSAGE_REMINDER_NOT_FOUND
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private val reminder1 = ReminderDTO("title1", "description1", "location1", -34.0, 151.0, "1")
    private val reminder2 = ReminderDTO("title2", "description2", "location2", -35.0, 152.0, "2")
    private lateinit var database: RemindersDatabase
    private lateinit var localRepository: RemindersLocalRepository

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        localRepository = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun reminderNotFound() = runBlocking {
        database.reminderDao().saveReminder(reminder1)

        val result = localRepository.getReminder(reminder2.id)
        assertThat(result is Result.Error, `is`(true))

        result as Result.Error

        assertThat(result.message, `is`(MESSAGE_REMINDER_NOT_FOUND))

    }

    @Test
    fun insertingThenFindById() = runBlocking {
        localRepository.saveReminder(reminder1)
        val result = localRepository.getReminder(reminder1.id) as Result.Success<ReminderDTO>
        val foundReminder = result.data
        assertThat(foundReminder, CoreMatchers.notNullValue())
        assertThat(foundReminder.id, `is`(reminder1.id))
        assertThat(foundReminder.title, `is`(reminder1.title))
        assertThat(foundReminder.description, `is`(reminder1.description))
        assertThat(foundReminder.location, `is`(reminder1.location))
        assertThat(foundReminder.latitude, `is`(reminder1.latitude))
        assertThat(foundReminder.longitude, `is`(reminder1.longitude))
    }

    @Test
    fun insertRemindersAndFetchAll_2Value_true() = runTest {
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        val reminderDTOList = localRepository.getReminders() as Result.Success
        assertThat(reminderDTOList.data.size, `is`(2))
    }

    @Test
    fun deleteAllReminder_getEmpty() = runTest {
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)
        localRepository.deleteAllReminders()
        val reminderDTOList = localRepository.getReminders() as Result.Success
        assertThat(reminderDTOList.data.size, `is`(0))
    }
    @Test
    fun insertThenDeleteReminder_returnNull() = runTest {
        localRepository.saveReminder(reminder1)
        localRepository.deleteAllReminders()
        assertThat(localRepository.getReminder(reminder1.id), not(`is`(CoreMatchers.instanceOf(ReminderDTO::class.java))))
    }
}