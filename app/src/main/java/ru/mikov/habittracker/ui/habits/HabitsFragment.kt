package ru.mikov.habittracker.ui.habits

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentHabitsBinding
import ru.mikov.habittracker.ui.ViewPagerFragmentDirections
import ru.mikov.habittracker.ui.adapters.HabitAdapter

class HabitsFragment : Fragment(R.layout.fragment_habits) {

    private val viewBinding: FragmentHabitsBinding by viewBinding()
    private val viewModel: HabitsViewModel by viewModels()
    private lateinit var habitType: HabitType
    private val habitsAdapter: HabitAdapter = HabitAdapter {
        val action = ViewPagerFragmentDirections.actionNavViewPagerToNavHabit(it)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitType = arguments?.get(ARGS_TYPE) as HabitType

        with(viewBinding) {
            fabAddHabit.setOnClickListener {
                findNavController().navigate(R.id.nav_habit)
            }

            rvHabits.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = habitsAdapter
            }
        }

        viewModel.getHabits().observe(requireActivity()) { habits ->
            val filteredList = habits.filter { habit -> habit.type == habitType }
            habitsAdapter.submitList(filteredList)
        }
    }


    companion object {
        private const val ARGS_NAME = "args_name"
        private const val ARGS_TYPE = "args_type"

        fun newInstance(name: String, habitType: HabitType): HabitsFragment {
            val fragment = HabitsFragment()
            val bundle = Bundle()
            bundle.putString(ARGS_NAME, name)
            bundle.putSerializable(ARGS_TYPE, habitType)
            fragment.arguments = bundle
            return fragment
        }
    }

}