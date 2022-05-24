package ru.mikov.habittracker.presentation

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.mikov.data.local.entities.HabitType
import ru.mikov.habittracker.R
import ru.mikov.habittracker.app.appComponent
import ru.mikov.habittracker.databinding.FragmentViewPagerBinding
import ru.mikov.habittracker.presentation.adapters.ViewPagerAdapter
import ru.mikov.habittracker.presentation.base.BaseFragment
import ru.mikov.habittracker.presentation.extentions.lazyActivityViewModel
import ru.mikov.habittracker.presentation.extentions.setParams
import ru.mikov.habittracker.presentation.extentions.visibleOrGone
import ru.mikov.habittracker.presentation.habit.HabitFragment.Companion.NUMBER_OF_TAB
import ru.mikov.habittracker.presentation.habit.HabitFragment.Companion.NUMBER_OF_TAB_KEY
import ru.mikov.habittracker.presentation.habits.HabitsState
import ru.mikov.habittracker.presentation.habits.HabitsViewModel
import ru.mikov.habittracker.presentation.habits.Sort


class ViewPagerFragment : BaseFragment<HabitsState, HabitsViewModel>(R.layout.fragment_view_pager) {

    private val viewBinding: FragmentViewPagerBinding by viewBinding()
    override val viewModel: HabitsViewModel by lazyActivityViewModel { stateHandle ->
        requireContext().appComponent.habitsViewModel().create(stateHandle)
    }

    override fun init() {
        setFragmentResultListener(NUMBER_OF_TAB_KEY) { _, bundle ->
            val result = bundle.getInt(NUMBER_OF_TAB)
            viewModel.chooseTabNumber(result)
        }
    }

    override fun renderUi(data: HabitsState) {
        with(viewBinding) {
            cvSort.visibleOrGone(data.sort != Sort.NONE || !data.searchQuery.isNullOrBlank())
            btnSort.visibleOrGone(data.sort == Sort.NONE && data.searchQuery.isNullOrBlank())

            when (data.sort) {
                Sort.NONE -> tvNameOfSort.text = getString(R.string.by_name)
                Sort.NAME -> tvNameOfSort.text = getString(R.string.by_name)
                Sort.PERIODICITY -> tvNameOfSort.text = getString(R.string.by_periodicity)
            }

            when (data.isAscending) {
                true -> btnAsDes.setParams(
                    R.string.ascending,
                    R.drawable.ic_baseline_sort_asending_24
                )
                else -> btnAsDes.setParams(
                    R.string.descending,
                    R.drawable.ic_baseline_sort_desending_24
                )
            }

            tvNameOfSort.visibleOrGone(data.sort != Sort.NONE)
            btnAsDes.visibleOrGone(data.sort != Sort.NONE)

            tvSearchFor.apply {
                visibleOrGone(!data.searchQuery.isNullOrBlank())
                text = getString(R.string.search_for, data.searchQuery)
            }

            viewPager.setCurrentItem(data.numberOfTab, false)
        }
    }

    override fun setupViews() {
        initViewPager()
        initViews()
    }

    private fun initViewPager() {
        with(viewBinding) {
            val adapter = ViewPagerAdapter(
                childFragmentManager,
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
                    viewModel.chooseTabNumber(position)
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
                viewModel.chooseFilterMode()
            }
        }
    }
}