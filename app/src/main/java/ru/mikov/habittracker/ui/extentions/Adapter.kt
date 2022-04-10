package ru.mikov.habittracker.ui.extentions

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.ui.adapters.HabitViewHolder

fun ListAdapter<Habit, HabitViewHolder>.registerAdapterDataObserver(recyclerView: RecyclerView) {
    this.registerAdapterDataObserver(object :
        RecyclerView.AdapterDataObserver() {

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            recyclerView.scrollToPosition(0)
        }

        override fun onItemRangeMoved(
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            recyclerView.scrollToPosition(0)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            recyclerView.scrollToPosition(0)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            recyclerView.scrollToPosition(0)
        }

    })
}