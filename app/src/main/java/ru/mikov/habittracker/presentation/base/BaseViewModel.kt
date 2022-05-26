package ru.mikov.habittracker.presentation.base

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.mikov.data.remote.NetworkMonitor

abstract class BaseViewModel<T : IViewModelState>(
    private val handleState: SavedStateHandle,
    initState: T
) : ViewModel() {

    companion object {
        private const val REQUEST_RETRY_DELAY = 10000L
    }

    private val notifications = MutableLiveData<Event<Notify>>()
    private val loading = MutableLiveData(Loading.HIDE_LOADING)
    private val connection = NetworkMonitor.isConnectedLive

    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    val currentState
        get() = state.value!!

    inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    fun showLoading(loadingType: Loading = Loading.SHOW_LOADING) {
        loading.value = loadingType
    }

    fun hideLoading() {
        loading.value = Loading.HIDE_LOADING
    }

    fun observeLoading(owner: LifecycleOwner, onChanged: (newState: Loading) -> Unit) {
        loading.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner,
            EventObserver { onNotify(it) })
    }

    fun observeConnection(owner: LifecycleOwner, onChanged: (isConnected: Boolean) -> Unit) {
        connection.observe(owner, Observer { onChanged(it!!) })
    }

    protected fun launchSafety(
        completeHandler: ((Throwable?) -> Unit)? = null,
        request: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            request()
        }.invokeOnCompletion {
            completeHandler?.invoke(it)
        }
    }
}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify {
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()

    data class ErrorMessage(
        override val message: String,
        val errLabel: String? = null,
        val errHandler: (() -> Unit)? = null
    ) : Notify()
}

enum class Loading {
    SHOW_LOADING, HIDE_LOADING
}