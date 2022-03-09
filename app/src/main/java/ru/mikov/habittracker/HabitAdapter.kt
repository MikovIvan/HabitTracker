package ru.mikov.habittracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean = oldItem == newItem
}

class HabitViewHolder(
    private val binding: ItemHabitBinding
) : RecyclerView.ViewHolder(binding.root) {

//    var item: Habit? = null
//        private set

    fun bind(
        habit: Habit,
        listener: (Habit) -> Unit
    ) {
//        this.item = habit
        with(binding) {
            tvHabitName.text = habit.name
            tvHabitDescription.text = habit.description
            tvHabitPeriodicity.text = habit.periodicity
            tvHabitPriority.text = habit.priority.toString()
            tvHabitType.text = habit.type
        }
        itemView.setOnClickListener { listener.invoke(habit) }
    }
}