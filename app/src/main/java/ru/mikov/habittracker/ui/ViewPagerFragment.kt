package ru.mikov.habittracker.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.local.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentViewPagerBinding
import ru.mikov.habittracker.ui.adapters.ViewPagerAdapter
import ru.mikov.habittracker.ui.habits.HabitsViewModel


class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private val viewBinding: FragmentViewPagerBinding by viewBinding()
    private val viewModel: HabitsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle
        )

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

            viewModel.state.observe(viewLifecycleOwner) {
                cvSort.visibility =
                    if (it.typeOfSort != -1 || !it.searchQuery.isNullOrBlank()) View.VISIBLE else View.GONE
                btnSort.visibility =
                    if (it.typeOfSort == -1 && it.searchQuery.isNullOrBlank()) View.VISIBLE else View.GONE

                when (it.typeOfSort) {
                    0 -> tvNameOfSort.text = getString(R.string.by_name)
                    else -> tvNameOfSort.text = getString(R.string.by_periodicity)
                }

                when (it.isAscending) {
                    true -> {
                        btnAsDes.apply {
                            text = getString(R.string.ascending)
                            setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_baseline_sort_asending_24,
                                0,
                            )
                        }
                    }
                    else -> {
                        btnAsDes.apply {
                            text = getString(R.string.descending)
                            setCompoundDrawablesRelativeWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_baseline_sort_desending_24,
                                0,
                            )
                        }
                    }
                }

                tvNameOfSort.visibility = if (it.typeOfSort != -1) View.VISIBLE else View.GONE
                btnAsDes.visibility = if (it.typeOfSort != -1) View.VISIBLE else View.GONE

                tvSearchFor.apply {
                    visibility = if (!it.searchQuery.isNullOrBlank()) View.VISIBLE else View.GONE
                    text = getString(R.string.search_for, it.searchQuery)
                }

                viewPager.setCurrentItem(it.numberOfTab, false)
            }

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
}