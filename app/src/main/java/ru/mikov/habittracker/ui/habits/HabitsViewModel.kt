package ru.mikov.habittracker.ui.habits

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.repositories.HabitFilter
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState

class HabitsViewModel(handle: SavedStateHandle) :
    BaseViewModel<HabitsState>(handle, HabitsState()) {
    private val repository = RootRepository

    private val habitsList = Transformations.switchMap(state) {
        val filter = it.toHabitFilter()
        return@switchMap repository.rawQueryHabits(filter)
    }

    fun observeList(
        owner: LifecycleOwner,
        type: HabitType = HabitType.GOOD,
        onChange: (list: List<Habit>) -> Unit
    ) {
        updateState { it.copy(type = type) }
        habitsList.observe(owner) { onChange(it) }
    }

}

data class HabitsState(
    val searchQuery: String? = null,
    val typeOfSort: Int = -1,
    val type: HabitType = HabitType.GOOD,
    val numberOfTab: Int = 0,
    val isAscending: Boolean = true
) : IViewModelState

private fun HabitsState.toHabitFilter(): HabitFilter =
    HabitFilter(
        searchQuery = searchQuery,
        sortBy = isAscending,
        typeOfSort = typeOfSort,
        type = type
    )