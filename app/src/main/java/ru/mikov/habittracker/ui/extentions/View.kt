package ru.mikov.habittracker.ui.extentions

import android.view.View
import android.widget.Button

fun Button.setParams(resId: Int, drawableId: Int) {
    this.apply {
        text = resources.getString(resId)
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            drawableId,
            0,
        )
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    hide(View.GONE)
}

fun View.visibleOrGone(show: Boolean) {
    if (show) visible() else gone()
}

private fun View.hide(hidingStrategy: Int) {
    visibility = hidingStrategy
}