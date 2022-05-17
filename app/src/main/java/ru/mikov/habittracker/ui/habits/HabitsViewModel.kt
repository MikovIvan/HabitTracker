package ru.mikov.habittracker.ui.habits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import ru.mikov.habittracker.App
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitDone
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.remote.NetworkMonitor
import ru.mikov.habittracker.data.remote.res.HabitDoneRes
import ru.mikov.habittracker.data.repositories.RootRepository
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState
import ru.mikov.habittracker.ui.base.Notify
import ru.mikov.habittracker.ui.extentions.quantityFromRes
import java.util.*

class HabitsViewModel(handle: SavedStateHandle) :
    BaseViewModel<HabitsState>(handle, HabitsState()) {
    private val repository = RootRepository

    init {
        subscribeOnDataSource(repository.getHabitsByType(HabitType.GOOD)) { habits, state ->
            state.copy(goodHabits = habits)
        }

        subscribeOnDataSource(repository.getHabitsByType(HabitType.BAD)) { habits, state ->
            state.copy(badHabits = habits)
        }
    }

    fun getHabits(currentType: HabitType): LiveData<List<Habit>> {
        //ливдата и трансформайшен
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
                habits = currentHabits.filter { it.name.startsWith(state.searchQuery!!, true) }
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


    fun handleSearch(searchQuery: String) {
        updateState { it.copy(searchQuery = searchQuery) }
    }

    fun clearFilter() {
        updateState {
            it.copy(
                searchQuery = "",
                sort = Sort.NONE,
                isAscending = false
            )
        }
    }

    fun chooseFilterMode() {
        updateState { it.copy(isAscending = !currentState.isAscending) }
    }

    fun chooseSortMode(sort: Sort) {
        updateState { it.copy(sort = sort) }
    }

    fun chooseTabNumber(number: Int) {
        updateState { it.copy(numberOfTab = number) }
    }

    fun doneHabit(habit: Habit) {
        launchSafety {
            val date = (Date().time / 1000).toInt()
            if (NetworkMonitor.isConnected) {
                repository.updateHabit(
                    habit.copy(
                        doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                )
                repository.doneHabit(HabitDoneRes(date, habit.id))
            } else {
                repository.updateHabit(
                    habit.copy(
                        doneDates = habit.doneDates.toMutableList().also { it.add(date) })
                )
                repository.addHabitDone(HabitDone(uid = habit.id, doneDate = date))
            }
            notify(Notify.TextMessage(getText(habit)))
        }
    }

    private fun getText(habit: Habit, leftToDo: Int = habit.getLeftToDo()): String {
        return when (habit.type) {
            HabitType.GOOD -> if (leftToDo <= 0) {
                App.applicationContext().resources.getString(R.string.habit_good_toast)
            } else
                App.applicationContext().quantityFromRes(
                    R.plurals.habit_good_toast,
                    habit.getLeftToDo(),
                    habit.getLeftToDo()
                )
            HabitType.BAD -> if (leftToDo <= 0) {
                App.applicationContext().resources.getString(R.string.habit_bad_toast)
            } else {
                App.applicationContext().quantityFromRes(
                    R.plurals.habit_bad_toast,
                    habit.getLeftToDo(),
                    habit.getLeftToDo()
                )
            }
        }
    }
}

data class HabitsState(
    var searchQuery: String? = null,
    val numberOfTab: Int = 0,
    val isAscending: Boolean = true,
    val sort: Sort = Sort.NONE,
    val badHabits: List<Habit> = emptyList(),
    val goodHabits: List<Habit> = emptyList()
) : IViewModelState

enum class Sort {
    NONE,
    NAME,
    PERIODICITY
}