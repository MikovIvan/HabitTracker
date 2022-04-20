package ru.mikov.habittracker.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment<S, T : BaseViewModel<S>>(@LayoutRes layout: Int) :
    Fragment(layout) where S : IViewModelState {

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

        observeViewModelData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }
}