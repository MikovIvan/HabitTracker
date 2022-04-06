package ru.mikov.habittracker.ui.base

import androidx.lifecycle.SavedStateHandle

interface IViewModelState {
    //override this if need save state in bundle
    fun save(outState: SavedStateHandle) {
        //default empty implementation
    }

    //override this if need restore state in bundle
    fun restore(savedState: SavedStateHandle): IViewModelState {
        //default empty implementation
        return this
    }
}