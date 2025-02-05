package com.udacity.project4.locationreminders.data

import com.udacity.project4.MESSAGE_FETCHING_REMINDER_FAILED
import com.udacity.project4.MESSAGE_FETCHING_REMINDER_LIST_FAILED
import com.udacity.project4.MESSAGE_REMINDER_NOT_FOUND
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var reminderDTOList = mutableListOf<ReminderDTO>()
    private var hasError = false

    fun setError(hasError: Boolean) {
        this.hasError = hasError
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (hasError) {
            return Result.Error(MESSAGE_FETCHING_REMINDER_LIST_FAILED)
        }
        return try {
            Result.Success(reminderDTOList)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTOList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (hasError) {
            return Result.Error(MESSAGE_FETCHING_REMINDER_FAILED)
        }
        val reminderDTO = reminderDTOList.firstOrNull { it.id == id }
        return reminderDTO?.let {
            Result.Success(it)
        } ?: run {
            Result.Error(MESSAGE_REMINDER_NOT_FOUND)
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDTOList.clear()
    }


}