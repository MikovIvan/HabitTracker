package ru.mikov.habittracker.ui.habits

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentHabitsBinding
import ru.mikov.habittracker.ui.ViewPagerFragmentDirections
import ru.mikov.habittracker.ui.adapters.HabitAdapter


class HabitsFragment : Fragment(R.layout.fragment_habits) {

    private val viewBinding: FragmentHabitsBinding by viewBinding()
    private val viewModel: HabitsViewModel by activityViewModels()
    private lateinit var habitType: HabitType
    private val habitsAdapter: HabitAdapter = HabitAdapter {
        val action = ViewPagerFragmentDirections.actionNavViewPagerToNavHabit(it.id)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitType = arguments?.get(ARGS_TYPE) as HabitType

        with(viewBinding) {
            rvHabits.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = habitsAdapter
            }
        }

        viewModel.observeList(viewLifecycleOwner, habitType) {
            habitsAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateState { it.copy(type = habitType) }
    }

    companion object {
        private const val ARGS_NAME = "args_name"
        private const val ARGS_TYPE = "args_type"

        fun newInstance(name: String, habitType: HabitType): HabitsFragment {
            return HabitsFragment().apply {
                arguments = bundleOf(
                    ARGS_NAME to name,
                    ARGS_TYPE to habitType
                )
            }
        }
    }

}