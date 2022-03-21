package ru.mikov.habittracker.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.mikov.habittracker.R
import ru.mikov.habittracker.data.entities.HabitType
import ru.mikov.habittracker.databinding.FragmentViewPagerBinding
import ru.mikov.habittracker.ui.adapters.ViewPagerAdapter
import ru.mikov.habittracker.ui.habits.HabitsFragment


class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private val viewBinding: FragmentViewPagerBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(
            HabitsFragment(HabitType.GOOD),
            HabitsFragment(HabitType.BAD)
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        with(viewBinding) {
            viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> HabitType.GOOD.name
                    else -> HabitType.BAD.name
                }
            }.attach()
        }
    }


}