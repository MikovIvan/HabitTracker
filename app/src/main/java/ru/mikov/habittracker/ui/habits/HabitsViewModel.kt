package ru.mikov.habittracker.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.remote.NetworkManager
import ru.mikov.habittracker.data.remote.res.HabitRes
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState

class HabitsViewModel(handle: SavedStateHandle) :
    BaseViewModel<HabitsState>(handle, HabitsState()) {
    private val repository = RootRepository
    private val network = NetworkManager.api

    init {
        subscribeOnDataSource(repository.getHabitsByType(HabitType.GOOD)) { habits, state ->
            state.copy(goodHabits = habits)
        }

        subscribeOnDataSource(repository.getHabitsByType(HabitType.BAD)) { habits, state ->
            state.copy(badHabits = habits)
        }
    }

    fun getHabits(currentType: HabitType): LiveData<List<Habit>> {
        val list = Transformations.switchMap(state) { state ->
            val currentHabits =
                if (currentType == HabitType.GOOD) state.goodHabits else state.badHabits
            val filteredLists = MutableLiveData<List<Habit>>()

            var habits = when (state.sort) {
                Sort.NONE -> currentHabits
                Sort.NAME -> {
                    if (state.isAscending) currentHabits.sortedBy { it.name }
                    else currentHabits.sortedByDescending { it.name }
                }
                Sort.PERIODICITY -> {
                    if (state.isAscending) currentHabits.sortedBy { it.periodicity.toInt() }
                    else currentHabits.sortedByDescending { it.periodicity.toInt() }
                }
            }
            if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.NONE) {
                habits = currentHabits.filter { it.name.startsWith(state.searchQuery, true) }
            } else if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.NAME) {
                habits = habits.filter { it.name.startsWith(state.searchQuery.toString(), true) }
            } else if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.PERIODICITY) {
                habits = habits.filter { it.name.startsWith(state.searchQuery.toString(), true) }
            }
            filteredLists.postValue(habits)
            return@switchMap filteredLists
        }
        return list
    }

    suspend fun getHabitsFromNetwork(): List<HabitRes> {
        return network.habits()
    }
}

data class HabitsState(
    val searchQuery: String? = null,
    val numberOfTab: Int = 0,
    val isAscending: Boolean = true,
    val sort: Sort = Sort.NONE,
    val badHabits: List<Habit> = emptyList(),
    val goodHabits: List<Habit> = emptyList(),
) : IViewModelState

enum class Sort {
    NONE,
    NAME,
    PERIODICITY
}