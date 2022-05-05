package ru.mikov.habittracker.ui.main

import androidx.lifecycle.SavedStateHandle
import ru.mikov.habittracker.ui.base.BaseViewModel
import ru.mikov.habittracker.ui.base.IViewModelState

class MainViewModel(handle: SavedStateHandle) : BaseViewModel<RootState>(handle, RootState()) {

}

data class RootState(
    val isSync: Boolean = false
) : IViewModelState