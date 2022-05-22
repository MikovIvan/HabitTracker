package ru.mikov.habittracker.presentation.extentions

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mikov.domain.models.Habit
import ru.mikov.habittracker.presentation.adapters.HabitViewHolder

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