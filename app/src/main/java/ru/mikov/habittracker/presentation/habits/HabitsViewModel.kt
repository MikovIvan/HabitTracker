package ru.mikov.habittracker.presentation.habits

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mikov.data.local.entities.HabitType
import ru.mikov.data.mappers.toDomain
import ru.mikov.data.mappers.toEntity
import ru.mikov.data.remote.NetworkMonitor.Companion.isConnected
import ru.mikov.domain.models.Habit
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.usecase.DoneHabitUseCase
import ru.mikov.domain.usecase.GetHabitsUseCase
import ru.mikov.habittracker.R
import ru.mikov.habittracker.app.App
import ru.mikov.habittracker.presentation.base.BaseViewModel
import ru.mikov.habittracker.presentation.base.IViewModelState
import ru.mikov.habittracker.presentation.base.Notify
import ru.mikov.habittracker.presentation.extentions.quantityFromRes

class HabitsViewModel @AssistedInject constructor(
    @Assisted handle: SavedStateHandle,
    getHabitsUseCase: GetHabitsUseCase,
    private val doneHabitUseCase: DoneHabitUseCase
) : BaseViewModel<HabitsState>(handle, HabitsState()) {

    init {
        subscribeOnDataSource(
            getHabitsUseCase.getHabits(HabitType.GOOD.toDomain()).asLiveData()
        ) { habits, state ->
            state.copy(goodHabits = habits)
        }

        subscribeOnDataSource(
            getHabitsUseCase.getHabits(HabitType.BAD.toDomain()).asLiveData()
        ) { habits, state ->
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
                    if (state.isAscending) currentHabits.sortedBy { it.title }
                    else currentHabits.sortedByDescending { it.title }
                }
                Sort.PERIODICITY -> {
                    if (state.isAscending) currentHabits.sortedBy { it.frequency.toInt() }
                    else currentHabits.sortedByDescending { it.frequency.toInt() }
                }
            }
            if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.NONE) {
                habits = currentHabits.filter { it.title.startsWith(state.searchQuery!!, true) }
            } else if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.NAME) {
                habits = habits.filter { it.title.startsWith(state.searchQuery.toString(), true) }
            } else if (!state.searchQuery.isNullOrBlank() && state.sort == Sort.PERIODICITY) {
                habits = habits.filter { it.title.startsWith(state.searchQuery.toString(), true) }
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
        viewModelScope.launch {
            doneHabitUseCase.invoke(habit, isConnected).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage(getText(result.data!!)))
                    }
                    is Resource.Error -> {
                        notify(Notify.TextMessage(result.message!!))
                    }
                    is Resource.Loading -> {

                    }
                }
            }.collect()
        }
    }

    private fun getText(habit: Habit, leftToDo: Int = habit.toEntity().getLeftToDo()): String {
        return when (HabitType.getById(habit.type)) {
            HabitType.GOOD -> if (leftToDo <= 0) {
                App.applicationContext().resources.getString(R.string.habit_good_toast)
            } else
                App.applicationContext().quantityFromRes(
                    R.plurals.habit_good_toast,
                    habit.toEntity().getLeftToDo(),
                    habit.toEntity().getLeftToDo()
                )
            HabitType.BAD -> if (leftToDo <= 0) {
                App.applicationContext().resources.getString(R.string.habit_bad_toast)
            } else {
                App.applicationContext().quantityFromRes(
                    R.plurals.habit_bad_toast,
                    habit.toEntity().getLeftToDo(),
                    habit.toEntity().getLeftToDo()
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): HabitsViewModel
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