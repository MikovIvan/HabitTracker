package ru.mikov.habittracker.presentation.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.mikov.data.local.entities.HabitPriority
import ru.mikov.data.local.entities.HabitType
import ru.mikov.data.remote.NetworkMonitor
import ru.mikov.domain.models.Habit
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
        launchSafety(completeHandler = { updateState { it.copy(isHabitLoaded = true) } }) {
            saveHabitUseCase.save(habit, NetworkMonitor.isConnected)
        }
        notify(Notify.TextMessage("${habit.title} is added"))
    }

    fun update(habit: Habit) {
        launchSafety(completeHandler = { updateState { it.copy(isHabitLoaded = true) } }) {
            updateHabitUseCase.update(habit, NetworkMonitor.isConnected)
        }
        notify(Notify.TextMessage("${habit.title} is updated"))
    }

    fun deleteHabit(habitId: String) {
        launchSafety(completeHandler = { updateState { it.copy(isHabitDeleted = true) } }) {
            deleteHabitUseCase.deleteHabit(
                habitId,
                NetworkMonitor.isConnected,
                currentState.isSynchronized
            )
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