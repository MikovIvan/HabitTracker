package ru.mikov.habittracker.ui.habits

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.databinding.FragmentHabitsBinding
import ru.mikov.habittracker.ui.adapters.HabitAdapter

class HabitsFragment : Fragment(R.layout.fragment_habits) {

    private val viewBinding: FragmentHabitsBinding by viewBinding()
    private val viewModel: HabitsViewModel by viewModels()
    private val habitsAdapter: HabitAdapter = HabitAdapter {
        val action = HabitsFragmentDirections.actionNavHabitsToNavHabit(it)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            fabAddHabit.setOnClickListener {
                findNavController().navigate(R.id.nav_habit)
            }

            rvHabits.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = habitsAdapter
            }
        }

        viewModel.getHabits().observe(requireActivity(), Observer {
            habitsAdapter.submitList(it)
        })
    }

}