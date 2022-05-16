package ru.mikov.habittracker.ui.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import ru.mikov.habittracker.data.local.PrefManager
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.data.toHabit
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState

class MainViewModel(handle: SavedStateHandle) : BaseViewModel<RootState>(handle, RootState()) {
    private val repository = RootRepository

    fun synchronizeWithNetwork() {
        loadHabitsFromNetwork()
        uploadUnSyncHabitsToServer()
        syncDeletedHabitsWithServer()
    }

    //загружаем привычки с сервера
    private fun loadHabitsFromNetwork() {
        launchSafety(completeHandler = { PrefManager.saveDbStatus(false) }) {
            if (repository.isNoHabits()) {
                showLoading()
                val habits = repository.loadHabitsFromNetwork().map { it.toHabit() }
                repository.addHabitsToDb(habits)
                hideLoading()
            }
        }
    }

    //загружаем на сервер добавленные и измененные привычки в офлайне
    private fun uploadUnSyncHabitsToServer() {
        launchSafety {
            val unSyncHabits = repository.getUnSyncHabits()
            unSyncHabits.forEach { habit ->
                syncHabit(habit)
            }
            Log.d("SYNC", "All Habits uploaded to Server")
        }
    }

    //удаляем с сервера привычки, которые были удалены офлайн
    private fun syncDeletedHabitsWithServer() {
        launchSafety {
            val uids = repository.getUIDToDelete()
            uids.forEach {
                repository.deleteHabitFromNetwork(it.id)
                Log.d("SYNC", "${it.id} deleted from server")
            }
            repository.clearUID()
            Log.d("SYNC", "All habits deleted from server")
        }
    }

    private suspend fun syncHabit(habit: Habit) {
        val id = repository.uploadHabitToNetwork(habit.copy(id = "")).uid
        repository.deleteHabitFromDb(habit.id)
        repository.addHabitToDb(habit.copy(id = id, isSynchronized = true))
        Log.d("SYNC", "$id uploaded to Server")
    }
}

data class RootState(
    val isConnected: Boolean = true
) : IViewModelState