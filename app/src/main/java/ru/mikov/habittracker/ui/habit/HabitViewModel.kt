package ru.mikov.habittracker.ui.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitPriority
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState

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
                isAddingMode = habitId == -1,
                type = habit.type,
                priority = habit.priority,
                pickedColor = habit.color
            )
        }
    }


    fun addHabit(habit: Habit) {
        viewModelScope.launch { repository.addHabit(habit) }
    }

    fun update(habit: Habit) {
        viewModelScope.launch { repository.update(habit) }
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
    val name: String = "",
    val description: String = "",
    val periodicity: String = "",
    val numberOfExecutions: String = "",
    val isAddingMode: Boolean = true,
    val type: HabitType = HabitType.GOOD,
    val priority: HabitPriority = HabitPriority.HIGH,
    val pickedColor: Int = -1
) : IViewModelState