package ru.mikov.habittracker.ui.base

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T : IViewModelState>(
    private val handleState: SavedStateHandle,
    initState: T
) : ViewModel() {
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    val currentState
        get() = state.value!!

    inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }
}