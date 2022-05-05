package ru.mikov.habittracker.ui.habit

import androidx.lifecycle.SavedStateHandle
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitPriority
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState
import ru.mikov.habittracker.ui.base.Notify

class HabitViewModel(
    handle: SavedStateHandle,
) : BaseViewModel<HabitState>(handle, HabitState()) {
    private val repository = RootRepository
    private val args: HabitFragmentArgs = HabitFragmentArgs.fromSavedStateHandle(handle)
    private val habitId = args.habitId

    init {
        subscribeOnDataSource(repository.getHabit(habitId)) { habit, state ->
            if (habit == null) return@subscribeOnDataSource state
            state.copy(
                name = habit.name,
                description = habit.description,
                periodicity = habit.periodicity,
                numberOfExecutions = habit.numberOfExecutions,
                isAddingMode = habitId.isBlank(),
                type = habit.type,
                priority = habit.priority,
                pickedColor = habit.color,
                isHabitLoaded = false,
                isHabitDeleted = false
            )
        }
    }

    fun addHabit(habit: Habit) {
        launchSafety(completeHandler = { updateState { it.copy(isHabitLoaded = true) } }) {
            val id = repository.uploadHabitToNetwork(habit).uid
            repository.addHabitToDb(habit.copy(id = id))
        }
        notify(Notify.TextMessage("${habit.name} is added"))
    }

    fun update(habit: Habit) {
        launchSafety(completeHandler = { updateState { it.copy(isHabitLoaded = true) } }) {
            repository.uploadHabitToNetwork(habit)
            repository.updateHabit(habit)
        }
        notify(Notify.TextMessage("${habit.name} is updated"))
    }

    fun deleteHabit(habitId: String) {
        launchSafety(completeHandler = { updateState { it.copy(isHabitDeleted = true) } }) {
            repository.deleteHabitFromNetwork(habitId)
            repository.deleteHabitFromDb(habitId)
        }
        notify(Notify.TextMessage("Habit is deleted"))
    }

    fun chooseType(habitType: HabitType) {
        updateState { it.copy(type = habitType) }
    }

    fun choosePriority(habitPriority: HabitPriority) {
        updateState { it.copy(priority = habitPriority) }
    }

    fun chooseColor(color: Int) {
        updateState { it.copy(pickedColor = color) }
    }

}

data class HabitState(
    var name: String = "",
    var description: String = "",
    var periodicity: String = "",
    var numberOfExecutions: String = "",
    val isAddingMode: Boolean = true,
    val type: HabitType = HabitType.GOOD,
    val priority: HabitPriority = HabitPriority.HIGH,
    val pickedColor: Int = -1,
    val isHabitLoaded: Boolean = false,
    val isHabitDeleted: Boolean = false
) : IViewModelState