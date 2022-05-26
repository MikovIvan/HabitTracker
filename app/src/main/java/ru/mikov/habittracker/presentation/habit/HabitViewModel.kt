package ru.mikov.habittracker.presentation.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType
import ru.mikov.data.remote.NetworkMonitor
import ru.mikov.domain.models.Habit
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.usecase.DeleteHabitUseCase
import ru.mikov.domain.usecase.GetHabitUseCase
import ru.mikov.domain.usecase.SaveHabitUseCase
import ru.mikov.domain.usecase.UpdateHabitUseCase
import ru.mikov.habittracker.presentation.base.BaseViewModel
import ru.mikov.habittracker.presentation.base.IViewModelState
import ru.mikov.habittracker.presentation.base.Notify


class HabitViewModel @AssistedInject constructor(
    @Assisted handle: SavedStateHandle,
    @Assisted habitId: String,
    getHabitUseCase: GetHabitUseCase,
    private val saveHabitUseCase: SaveHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase
) : BaseViewModel<HabitState>(handle, HabitState()) {

    init {
        subscribeOnDataSource(getHabitUseCase.getHabit(habitId).asLiveData()) { habit, state ->
            if (habit == null) return@subscribeOnDataSource state
            state.copy(
                name = habit.title,
                description = habit.description,
                periodicity = habit.frequency.toString(),
                numberOfExecutions = habit.count.toString(),
                isAddingMode = habitId.isBlank(),
                type = HabitType.getById(habit.type),
                priority = HabitPriority.getById(habit.priority),
                pickedColor = habit.color,
                isSynchronized = habit.isSynchronized,
                isHabitLoaded = false,
                isHabitDeleted = false
            )
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            saveHabitUseCase.invoke(habit, NetworkMonitor.isConnected).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage("${habit.title} is added"))
                    }
                    is Resource.Error -> {
                        notify(Notify.ErrorMessage(result.message!!))
                    }
                    is Resource.Loading -> {

                    }
                }
            }.collect()
        }.invokeOnCompletion { updateState { it.copy(isHabitLoaded = true) } }
    }

    fun update(habit: Habit) {
        viewModelScope.launch {
            updateHabitUseCase.update(habit, NetworkMonitor.isConnected).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage("${habit.title} is updated"))
                    }
                    is Resource.Error -> {
                        notify(Notify.ErrorMessage(result.message!!))
                    }
                    is Resource.Loading -> {

                    }
                }
            }.collect()
        }.invokeOnCompletion { updateState { it.copy(isHabitLoaded = true) } }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            deleteHabitUseCase.invoke(
                habitId,
                NetworkMonitor.isConnected,
                currentState.isSynchronized
            ).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage("Habit ${result.data} is deleted"))
                    }
                    is Resource.Error -> {
                        notify(Notify.ErrorMessage(result.message!!))
                    }
                    is Resource.Loading -> {

                    }
                }
            }.collect()
        }.invokeOnCompletion { updateState { it.copy(isHabitDeleted = true) } }
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

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle, habitId: String): HabitViewModel
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
    val isSynchronized: Boolean = true,
    val isHabitLoaded: Boolean = false,
    val isHabitDeleted: Boolean = false
) : IViewModelState