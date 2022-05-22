package ru.mikov.habittracker.presentation.main

import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.mikov.data.local.PrefManager
import ru.mikov.domain.usecase.IsNoHabitsUseCase
import ru.mikov.domain.usecase.LoadHabitsUseCase
import ru.mikov.domain.usecase.SyncDeletedHabitsWithServerUseCase
import ru.mikov.domain.usecase.UploadUnSyncHabitsToServerUseCase
import ru.mikov.habittracker.presentation.base.BaseViewModel
import ru.mikov.habittracker.presentation.base.IViewModelState

class MainViewModel @AssistedInject constructor(
    @Assisted handle: SavedStateHandle,
    private val loadHabitsUseCase: LoadHabitsUseCase,
    private val isNoHabitsUseCase: IsNoHabitsUseCase,
    private val uploadUnSyncHabitsToServerUseCase: UploadUnSyncHabitsToServerUseCase,
    private val syncHabitsToServerUseCase: SyncDeletedHabitsWithServerUseCase,
    private val prefManager: PrefManager
) : BaseViewModel<RootState>(handle, RootState()) {

    fun synchronizeWithNetwork() {
        loadHabitsFromNetwork()
        uploadUnSyncHabitsToServer()
        syncDeletedHabitsWithServer()
    }

    //загружаем привычки с сервера
    private fun loadHabitsFromNetwork() {
        launchSafety(completeHandler = { prefManager.saveDbStatus(false) }) {
            if (isNoHabitsUseCase.isNoHabits()) {
                showLoading()
                loadHabitsUseCase.loadHabits()
                hideLoading()
            }
        }
    }

    //загружаем на сервер добавленные и измененные привычки в офлайне
    private fun uploadUnSyncHabitsToServer() {
        launchSafety {
            uploadUnSyncHabitsToServerUseCase.upload()
        }
    }

    //удаляем с сервера привычки, которые были удалены офлайн
    private fun syncDeletedHabitsWithServer() {
        launchSafety {
            syncHabitsToServerUseCase.sync()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): MainViewModel
    }
}

data class RootState(
    val isConnected: Boolean = true
) : IViewModelState

