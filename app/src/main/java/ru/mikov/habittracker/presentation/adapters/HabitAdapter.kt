package ru.mikov.habittracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mikov.data.local.entities.HabitType
import ru.mikov.domain.models.Habit
import ru.mikov.habittracker.R
import ru.mikov.habittracker.databinding.ItemHabitBinding

class HabitAdapter(
    private val listener: (Habit) -> Unit,
    private val listener2: (Habit) -> Unit
) : ListAdapter<Habit, HabitViewHolder>(HabitDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHabitBinding.inflate(layoutInflater, parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) =
        holder.bind(getItem(position), listener, listener2)

}

class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean =
        oldItem.uid == newItem.uid

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem == newItem
}

class HabitViewHolder(
    private val binding: ItemHabitBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        habit: Habit,
        listener: (Habit) -> Unit,
        listener2: (Habit) -> Unit,
    ) {
        with(binding) {
            tvHabitName.text =
                this@HabitViewHolder.itemView.context.getString(R.string.tv_habit_name, habit.title)
            tvHabitDescription.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_description,
                habit.description
            )
            tvHabitPeriodicity.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_periodicity,
                habit.frequency.toString()
            )
            tvHabitPriority.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_priority,
                when (habit.priority) {
                    0 -> itemView.context.resources.getString(R.string.low)
                    1 -> itemView.context.resources.getString(R.string.medium)
                    else -> itemView.context.resources.getString(R.string.high)
                }
            )
            tvHabitType.text = when (HabitType.getById(habit.type)) {
                HabitType.GOOD -> this@HabitViewHolder.itemView.context.getString(R.string.type_good)
                HabitType.BAD -> this@HabitViewHolder.itemView.context.getString(R.string.type_bad)
            }
            cvHabit.setCardBackgroundColor(habit.color)
            btnDone.setOnClickListener { listener2.invoke(habit) }
        }
        itemView.setOnClickListener { listener.invoke(habit) }
    }
}