package ru.mikov.habittracker.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentViewPagerBinding
import ru.mikov.habittracker.ui.adapters.ViewPagerAdapter
import ru.mikov.habittracker.ui.extentions.setParams
import ru.mikov.habittracker.ui.extentions.visibleOrGone
import ru.mikov.habittracker.ui.habit.HabitFragment.Companion.NUMBER_OF_TAB
import ru.mikov.habittracker.ui.habit.HabitFragment.Companion.NUMBER_OF_TAB_KEY
import ru.mikov.habittracker.ui.habits.HabitsViewModel
import ru.mikov.habittracker.ui.habits.Sort


class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private val viewBinding: FragmentViewPagerBinding by viewBinding()
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(NUMBER_OF_TAB_KEY) { _, bundle ->
            val result = bundle.getInt(NUMBER_OF_TAB)
            viewModel.updateState { it.copy(numberOfTab = result) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initViews()
        observeState()
    }

    private fun initViewPager() {
        with(viewBinding) {
            val adapter = ViewPagerAdapter(
                requireActivity().supportFragmentManager,
                lifecycle
            )

            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> HabitType.GOOD.name
                    else -> HabitType.BAD.name
                }
            }.attach()

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.updateState { it.copy(numberOfTab = position) }
                }
            })
        }
    }

    private fun initViews() {
        with(viewBinding) {
            fabAddHabit.setOnClickListener {
                findNavController().navigate(R.id.nav_habit)
            }

            btnSort.setOnClickListener {
                findNavController().navigate(R.id.dialog_filter)
            }

            cvSort.setOnClickListener { findNavController().navigate(R.id.dialog_filter) }

            btnAsDes.setOnClickListener {
                viewModel.updateState { it.copy(isAscending = !viewModel.currentState.isAscending) }
            }
        }
    }

    private fun observeState() {
        with(viewBinding) {
            viewModel.observeState(viewLifecycleOwner) { state ->

                cvSort.visibleOrGone(state.sort != Sort.NONE || !state.searchQuery.isNullOrBlank())
                btnSort.visibleOrGone(state.sort == Sort.NONE && state.searchQuery.isNullOrBlank())

                when (state.sort) {
                    Sort.NONE -> tvNameOfSort.text = getString(R.string.by_name)
                    Sort.NAME -> tvNameOfSort.text = getString(R.string.by_name)
                    Sort.PERIODICITY -> tvNameOfSort.text = getString(R.string.by_periodicity)
                }

                when (state.isAscending) {
                    true -> btnAsDes.setParams(
                        R.string.ascending,
                        R.drawable.ic_baseline_sort_asending_24
                    )
                    else -> btnAsDes.setParams(
                        R.string.descending,
                        R.drawable.ic_baseline_sort_desending_24
                    )
                }

                tvNameOfSort.visibleOrGone(state.sort != Sort.NONE)
                btnAsDes.visibleOrGone(state.sort != Sort.NONE)

                tvSearchFor.apply {
                    visibleOrGone(!state.searchQuery.isNullOrBlank())
                    text = getString(R.string.search_for, state.searchQuery)
                }

//                viewPager.doOnPreDraw { viewPager.setCurrentItem(state.numberOfTab, false)}
                viewPager.setCurrentItem(state.numberOfTab, false)
            }
        }
    }
}