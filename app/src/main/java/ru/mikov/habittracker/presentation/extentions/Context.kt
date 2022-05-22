package ru.mikov.habittracker.presentation.extentions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.mikov.habittracker.presentation.base.Factory

inline fun <reified T : ViewModel> Fragment.lazyViewModel(
    noinline create: (stateHandle: SavedStateHandle) -> T
) = viewModels<T> { Factory(this, create) }

inline fun <reified T : ViewModel> Fragment.lazyActivityViewModel(
    noinline create: (stateHandle: SavedStateHandle) -> T
) = activityViewModels<T> { Factory(this, create) }

inline fun <reified T : ViewModel> ComponentActivity.lazyViewModel(
    noinline create: (stateHandle: SavedStateHandle) -> T,
) = viewModels<T> { Factory(this, create) }

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.quantityFromRes(id_: Int, qtt: Int, vararg format: Any): String {
    return resources.getQuantityString(id_, qtt, *format)
}