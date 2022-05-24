package ru.mikov.habittracker.presentation.habits

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.mikov.data.local.entities.HabitType
import ru.mikov.habittracker.R
import ru.mikov.habittracker.app.appComponent
import ru.mikov.habittracker.databinding.FragmentHabitsBinding
import ru.mikov.habittracker.presentation.ViewPagerFragmentDirections
import ru.mikov.habittracker.presentation.adapters.HabitAdapter
import ru.mikov.habittracker.presentation.base.BaseFragment
import ru.mikov.habittracker.presentation.extentions.lazyActivityViewModel


class HabitsFragment : BaseFragment<HabitsState, HabitsViewModel>(R.layout.fragment_habits) {

    private val viewBinding: FragmentHabitsBinding by viewBinding()
    override val viewModel: HabitsViewModel by lazyActivityViewModel { stateHandle ->
        requireContext().appComponent.habitsViewModel().create(stateHandle)
    }
    private lateinit var habitType: HabitType
    private var habitsAdapter: HabitAdapter = HabitAdapter(
        listener = {
            val action = ViewPagerFragmentDirections.actionNavViewPagerToNavHabit(it.uid)
            findNavController().navigate(action)
        },
        listener2 = {
            viewModel.doneHabit(it)
        }
    )

    override fun init() {
        habitType = arguments?.get(ARGS_TYPE) as HabitType
        requireContext().appComponent.subComponent().create().inject(this)
    }

    override fun setupViews() {
        with(viewBinding) {
            rvHabits.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = habitsAdapter

                //need to go to the start of the list after sort/add/delete item
//                habitsAdapter.registerAdapterDataObserver(this)
            }
        }
    }

    override fun observeViewModelData() {
        viewModel.getHabits(habitType).observe(viewLifecycleOwner) {
            habitsAdapter.submitList(it)
        }
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

    override fun renderUi(data: HabitsState) {

    }

}
