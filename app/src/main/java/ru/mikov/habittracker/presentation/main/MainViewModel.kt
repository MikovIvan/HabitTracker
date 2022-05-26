package ru.mikov.habittracker.presentation.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mikov.data.local.PrefManager
import ru.mikov.domain.repository.Resource
import ru.mikov.domain.usecase.IsNoHabitsUseCase
import ru.mikov.domain.usecase.LoadHabitsUseCase
import ru.mikov.domain.usecase.SyncDeletedHabitsWithServerUseCase
import ru.mikov.domain.usecase.UploadUnSyncHabitsToServerUseCase
import ru.mikov.habittracker.presentation.base.BaseViewModel
import ru.mikov.habittracker.presentation.base.IViewModelState
import ru.mikov.habittracker.presentation.base.Notify

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
        viewModelScope.launch {
            if (isNoHabitsUseCase.isNoHabits()) {
                loadHabitsUseCase()
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                hideLoading()
                            }
                            is Resource.Error -> {
                                notify(Notify.TextMessage(result.message!!))
                            }
                            is Resource.Loading -> {
                                showLoading()
                            }
                        }
                    }
                    .collect()
            }
        }.invokeOnCompletion { prefManager.saveDbStatus(false) }
    }

    //загружаем на сервер добавленные и измененные привычки в офлайне
    private fun uploadUnSyncHabitsToServer() {
        viewModelScope.launch {
            uploadUnSyncHabitsToServerUseCase().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage("${result.data?.title} is sync"))
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

    //удаляем с сервера привычки, которые были удалены офлайн
    private fun syncDeletedHabitsWithServer() {
        viewModelScope.launch {
            syncHabitsToServerUseCase().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        notify(Notify.TextMessage("${result.data?.uid} is delete"))
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

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): MainViewModel
    }
}

data class RootState(
    val isConnected: Boolean = true
) : IViewModelState

