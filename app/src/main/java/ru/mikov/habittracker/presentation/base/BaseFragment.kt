package ru.mikov.habittracker.presentation.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ru.mikov.habittracker.presentation.main.MainActivity

abstract class BaseFragment<S, T : BaseViewModel<S>>(@LayoutRes layout: Int) :
    Fragment(layout) where S : IViewModelState {

    val root: MainActivity
        get() = activity as MainActivity
    abstract val viewModel: T

    abstract fun renderUi(data: S)
    abstract fun setupViews()

    open fun observeViewModelData() {

    }

    open fun init() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewModel.observeState(viewLifecycleOwner, ::renderUi)
        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeLoading(viewLifecycleOwner) { renderLoading(it) }

        observeViewModelData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun renderLoading(loadingState: Loading) {
        root.renderLoading(loadingState)
    }
}