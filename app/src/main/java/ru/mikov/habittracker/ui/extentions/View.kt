package ru.mikov.habittracker.ui.extentions

import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner

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

fun Spinner.onItemSelectedListener(selected: (adapterView: AdapterView<*>, pos: Int) -> Unit) {
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?, view: View?, position: Int, id: Long
        ) {
            selected.invoke(parent as AdapterView<*>, position)
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }
    }
}