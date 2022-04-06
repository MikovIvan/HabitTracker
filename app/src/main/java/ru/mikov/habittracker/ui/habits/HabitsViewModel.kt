package ru.mikov.habittracker.ui.habits

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.data.repositories.HabitFilter
import ru.mikov.habittracker.data.repositories.RootRepository

class HabitsViewModel : ViewModel() {
    private val repository = RootRepository

    var sortParam: Int = -1
    var isAscending: Boolean = false

    private val isAscendingOrder: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    private val searchQuery: MutableLiveData<String> = MutableLiveData<String>()

    private val typeOfSort: MutableLiveData<Int> = MutableLiveData<Int>()

    private val habitType: MutableLiveData<HabitType> = MutableLiveData<HabitType>()

    fun setType(type: HabitType) {
        habitType.value = type
    }

    fun search(string: String) {
        searchQuery.value = string
    }

    fun setTypeOfSort(type: Int) {
        typeOfSort.value = type
    }

    fun setOrder(isAscending: Boolean) {
        isAscendingOrder.value = isAscending
    }

    var state = MediatorLiveData<State>().apply {
        value = State()
        addSource(searchQuery) {
            value = value?.copy(searchQuery = it)
        }
        addSource(isAscendingOrder) {
            value = value?.copy(sortBy = it)
        }
        addSource(habitType) {
            value = value?.copy(type = it)
        }
        addSource(typeOfSort) {
            value = value?.copy(typeOfSort = it)
        }
    }

    val habitsList = Transformations.switchMap(state) {
        val filter = it.toHabitFilter()
        return@switchMap repository.rawQueryArticles(filter)
    }

}

data class State(
    val searchQuery: String? = null,
    val sortBy: Boolean = true,
    val typeOfSort: Int = -1,
    val type: HabitType = HabitType.GOOD,
    val list: List<Habit> = listOf()
) {
    fun toHabitFilter(): HabitFilter =
        HabitFilter(
            searchQuery, sortBy, typeOfSort, type
        )
}