package ru.mikov.habittracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.Habit
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.databinding.ItemHabitBinding

class HabitAdapter(
    private val listener: (Habit) -> Unit
) : ListAdapter<Habit, HabitViewHolder>(HabitDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHabitBinding.inflate(layoutInflater, parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) =
        holder.bind(getItem(position), listener)

}

class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem == newItem
}

class HabitViewHolder(
    private val binding: ItemHabitBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        habit: Habit,
        listener: (Habit) -> Unit
    ) {

        with(binding) {
            tvHabitName.text =
                this@HabitViewHolder.itemView.context.getString(R.string.tv_habit_name, habit.name)
            tvHabitDescription.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_description,
                habit.description
            )
            tvHabitPeriodicity.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_periodicity,
                habit.periodicity
            )
            tvHabitPriority.text = this@HabitViewHolder.itemView.context.getString(
                R.string.tv_habit_priority,
                habit.priority
            )
            tvHabitType.text = when (habit.type) {
                HabitType.GOOD -> this@HabitViewHolder.itemView.context.getString(R.string.type_good)
                HabitType.BAD -> this@HabitViewHolder.itemView.context.getString(R.string.type_bad)
            }
            cvHabit.setCardBackgroundColor(habit.color)
        }
        itemView.setOnClickListener { listener.invoke(habit) }
    }
}