package ru.mikov.habittracker.ui.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.mikov.habittracker.ui.habit.HabitViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle = bundleOf(),
    private val params: Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(
                handle,
                params as Int
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}